<template>
    <!-- 接口测试 -->
    <el-form labelWidth="120px">
        <el-form-item label="查询参数" v-if="apiInfo.method == 'get'">
            <QueryParams v-model="model.testData.queryParams" />
        </el-form-item>

        <el-form-item label="请求体" v-if="apiInfo.method == 'post'">
            <MonacoEditor height="200px" v-model="model.testData.body" language="json" />
        </el-form-item>

        <!-- <el-form-item label="请求头">
            <MonacoEditor height="200px" v-model="model.headers" language="json"/>
        </el-form-item> -->

        <div class="text-center mb-4">
            <el-button type="primary" @click="doTest">测试</el-button>
            <el-button @click="saveTestData">保存测试数据</el-button>
        </div>

        <div v-if="testResult" class="result mt-8 mb-8">
            <div class="page-title mt-4">处理结果</div>
            <div v-if="testFailed">失败</div>
            <div v-else>成功</div>

            <el-row :gutter="32" class="mt-8">
                <el-col :span="12">
                    <div class="page-title v-center">
                        <span>返回数据</span>
                        <el-link type="primary" class="ml-4" @click="doSaveExample" v-if="!testFailed">保存成示例</el-link>
                    </div>
                    <MonacoEditor height="600px" v-model="testResult" language="json" />
                </el-col>

                <el-col :span="12">
                    <div class="page-title">调试日志</div>
                    <div class="logs p-2 h-100">
                        <div v-for="log in logs" :key="log" class="mb-2" :class="{ error: log.level == 'error' }">
                            <span class="mr-1">{{ log.time }}</span>
                            <span class="mr-1">{{ log.level }}</span>
                            <span class="mr-1">{{ log.title }}</span>
                            <el-input type="textarea" autosize v-model="log.content" class="input" />
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </el-form>
</template>

<script setup>
import * as _ from 'lodash'
import * as apiApis from '@/apis/api'
import { ElMessage } from 'element-plus';
import MonacoEditor from '@/components/monaco-editor.vue'
import QueryParams from './query-params.vue';
import * as jsonUtils from '@/utils/json'

const props = defineProps(["apiInfo", "editing"])
const testResult = ref("")
const testFailed = ref(false)
const logs = ref([])
const emits = defineEmits(["save"])
const model = defineModel()

onMounted(() => {
    window.websocket.addListener('apiTest', ({ level, title, content, time }) => {
        // content = _.isString(content) ? content : JSON.stringify(content)
        // content = content.replace(/(\r\n)/g, '<br/>')
        if (_.isString(content)) {
            if (content.startsWith("[") || content.startsWith("{")) {
                content = JSON.stringify(JSON.parse(content), null, "\t");
            }
        } else {
            content = JSON.stringify(content, null, "\t")
        }
        logs.value.push({
            level,
            title,
            time,
            content
        })
    })
})


// 进行接口测试
function doTest() {
    let finalParams = {};

    if (props.apiInfo.method == 'post') {
        // post请求
        finalParams = model.value.testData.body || {}
    } else {
        // get请求
        let params = model.value.testData.queryParams
        if (params) {
            params.forEach(param => {
                finalParams[param.code] = param.value
            })
        }
    }
    apiApis.testApi(props.apiInfo.method, props.apiInfo.path, finalParams).then(resp => {
        ElMessage.success('测试成功')
        testResult.value = JSON.stringify(resp)
        testFailed.value = false
    }).catch(err => {
        testFailed.value = true
        testResult.value = JSON.stringify(err)
    })
}

// 保存示例及输入输出参数
function doSaveExample() {
    let result = testResult.value
    if (!result) {
        ElMessage.warning("内容为空")
        return
    }

    // 列表只取第一个元素
    result = JSON.parse(result)
    if (_.isArray(result) && result.length > 1) {
        result = result[0]
    } else if (_.isObject(result)) {
        // object处理第一层数据
        _.forIn(result, (v, k) => {
            if (_.isArray(v) && v.length > 1) {
                result.put(k, v[0])
            }
        })
    }

    let params = jsonUtils.loadJsonSchema(result)
    model.value.output = params || []
    model.value.outputExample = JSON.stringify(result)
    emits('save', true)
}

// 保存测试数据
function saveTestData() {
    let inputParams = model.value.inputParams

    if (!inputParams || !inputParams.length) {
        inputParams = model.value.inputParams = []

        // 没有配置输入参数时，使用测试参数进行填充
        let method = props.apiInfo.method
        let testData = model.value.testData
        if (testData) {
            if (method == 'get' && testData.queryParams) {
                testData.queryParams.forEach(param => {
                    let value = param.value
                    let type = 'string'
                    if (_.isInteger(value)) {
                        type = 'int'
                    } else if (_.isNumber(value)) {
                        type = 'float'
                    }

                    inputParams.push({
                        code: param.code,
                        name: param.code,
                        default: param.value,
                        type
                    })
                })
            } else if (method == 'post' && testData.body) {
                let arr = parseInputParamsFromJson(testData.body)
                inputParams.splice(0, 0, arr)
            }
        }
    }

    emits('save', true)
}

function parseInputParamsFromJson(obj) {
    obj = JSON.parse(obj)

    if (_.isArray(obj)) {
        // 暂只解析第一级
        let item = {
            code: '*',
            name: '*',
            type: 'array',
            default: JSON.stringify(obj)
        }

        return item
    } else if (_.isObject(obj)) {
        // 对象
        let children = []
        let item = {
            code: '*',
            name: '*',
            type: 'object',
            children: children
        }

        _.forEach(obj, (value, key) => {
            let subItem = parseInputParamsFromJson(value)
            subItem.name = key
            subItem.code = key
            children.push(subItem)
        })

        return item
    } else {
        // 不是数组也不是对象，是直接值
        let type = 'string'
        if (_.isInteger(obj)) {
            type = 'int'
        } else if (_.isNumber(obj)) {
            type = 'float'
        }
        return {
            type,
            default: obj
        }
    }
}
</script>

<style lang="scss" scoped>
.logs {
    background-color: #222;
    color: #ddd;
    line-height: 22px;
    height: 600px;
    box-sizing: border-box;
    overflow-y: auto;

    .error {
        color: #cc0000;

        :deep() {
            .el-textarea__inner {
                color: #cc0000;
            }
        }
    }

    .input {
        background-color: transparent;

        :deep() {
            .el-textarea__inner {
                background-color: transparent;
                border: none;
                box-shadow: none;
                color: #ddd;
            }
        }
    }
}

.result {
    border-top: 1px solid #ddd;
}
</style>