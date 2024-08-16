<template>
    <!-- 接口测试 -->
    <el-form labelWidth="120px">
        <el-form-item label="查询参数" v-if="apiInfo.method == 'get'">
            <QueryParams v-model="formModel.params" />
        </el-form-item>

        <el-form-item label="请求体" v-if="apiInfo.method == 'post'">
            <MonacoEditor height="200px" v-model="formModel.body" language="json" />
        </el-form-item>

        <!-- <el-form-item label="请求头">
            <MonacoEditor height="200px" v-model="formModel.headers" language="json"/>
        </el-form-item> -->

        <div class="text-center mb-4">
            <el-button type="primary" @click="doTest">测试</el-button>
        </div>

        <div v-if="testResult" class="result mt-8 mb-8">
            <div class="page-title mt-4">处理结果</div>
            <div v-if="testFailed">失败</div>
            <div v-else>成功</div>

            <el-row :gutter="32" class="mt-8">
                <el-col :span="12">
                    <div class="page-title">返回数据</div>
                    <MonacoEditor height="600px" v-model="testResult" language="json" />
                </el-col>

                <el-col :span="12">
                    <div class="page-title">调试日志</div>
                    <div class="logs p-2 h-100">
                        <div v-for="log in logs" :key="log" class="mb-2" :class="{error: log.level == 'error'}">
                            <span class="mr-1">{{ log.time }}</span>
                            <span class="mr-1">{{ log.level }}</span>
                            <span class="mr-1">{{ log.title }}</span>
                            <p class="px-8" v-html="log.content"></p>
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
import * as nodeUtils from '@/utils/node.js'

const initModel = {
    params: [],
    body: '{}',
    headers: '{}'
}
const formModel = ref(initModel)
const props = defineProps(["apiInfo", "editing"])
const testResult = ref("")
const testFailed = ref(false)
const logs = ref([])

onMounted(() => {
    window.websocket.addListener('apiTest', ({level, title, content, time}) => {
        content = _.isString(content) ? content : JSON.stringify(content)
        content = content.replace(/(\r\n)/g, '<br/>')
        logs.value.push({
            level,
            title,
            time,
            content
        })
    })
})

// 解析保存的测试数据
watch(() => props.apiInfo.content.testData, (val) => {
    if (!val) {
        return
    }

    _.defaults(val, initModel)
    formModel.value = val
}, {
    immediate: true
})

// 进行接口测试
function doTest() {
    let finalParams = {};

    if (props.apiInfo.method == 'post') {
        // post请求
        finalParams = formModel.value.body || {}
    } else {
        // get请求
        let params = formModel.value.params
        if (params) {
            finalParams.forEach(param => {
                params[param.code] = param.value
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
    }
}
.result {
    border-top: 1px solid #ddd;
}
</style>