<template>
    <!-- 接口编辑 -->
    <div class="new-api bg-white br-1 border-box">
        <el-page-header :content="id ? '编辑接口' : '新增接口'" @back="goBack"></el-page-header>

        <el-tabs class="mt-4 tabs" finish-status="success" v-model="activeTab" @tab-change="tabChange">
            <el-tab-pane label="基础信息" name="base">
                <base-form :fields="fields" v-model="formModel" class="form" :readonly="!editing" ref="formRef" />
            </el-tab-pane>
            <el-tab-pane label="输入配置" name="input" v-if="showMore">
                <inputParams v-model="formModel.content.inputParams" :editing="editing" v-if="loaded" ref="inputRef" />
            </el-tab-pane>
            <el-tab-pane label="处理流程" name="flow">
                <apiFlow v-model="formModel.content.nodes" :inputParams="formModel.content.inputParams" ref="flowRef"
                    :editing="editing" v-if="loaded" />
            </el-tab-pane>
            <el-tab-pane label="输出配置" name="output" v-if="showMore">
                <outputParams v-model="formModel.content.output" :editing="editing" :apiInfo="formModel" v-if="loaded"
                    ref="outputRef" />
            </el-tab-pane>
            <el-tab-pane label="接口测试" v-if="!editing" name="test">
                <apiTest :apiInfo="formModel" :editing="editing" v-if="loaded" @save="doSaveTest"
                    v-model="formModel.content" />
            </el-tab-pane>
            <el-tab-pane>
                <template #label>
                    <el-link type="primary" class="more ml-4" @click.stop="showMore = true" v-if="!showMore">
                        <el-icon>
                            <DArrowRight />
                        </el-icon>
                        更多
                    </el-link>

                    <el-link type="primary" class="more ml-4" @click.stop="setSimpleModel" v-if="showMore">
                        <el-icon>
                            <DArrowLeft />
                        </el-icon>
                        简化
                    </el-link>
                </template>
            </el-tab-pane>
        </el-tabs>

        <div class="buttons text-center">
            <template v-if="editing">
                <el-link type="primary" class="mr-4" @click="doCancel">取消</el-link>
                <el-button type="primary" @click="doSave">保存</el-button>
            </template>
            <template v-else>
                <el-button type="primary" @click="startEdit">编辑</el-button>
                <el-button type="success" @click="doPublish" :disabled="formModel.status == 1">发布</el-button>
            </template>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import * as entityApis from '@/apis/entity'
import { ElMessage } from 'element-plus'
import * as _ from 'lodash'
import apiFlow from './flow/index.vue'
import apiTest from './test/index.vue'
import inputParams from './input/index.vue'
import * as apiApis from '@/apis/api'
import { useRouter } from 'vue-router'
import * as nodeUtils from '@/utils/node'
import OutputParams from './output/index.vue'

const router = useRouter()
const fields = ref([
    { label: '接口名称', prop: 'name', required: true },
    {
        label: '接口类型', prop: 'method', required: true, type: 'select', options: [
            { label: 'GET', value: 'get' },
            { label: 'POST', value: 'post' },
        ]
    },
    { label: '游客模式', prop: 'guestMode', default: false, type: 'checkbox' },
    { label: '接口路径', prop: 'path', required: true, prepend: '/dua/' },
    { label: '接口分类', prop: 'typeId', required: true, type: 'tree-select', options: loadTypes },
    { label: '接口备注', prop: 'remark', type: 'textarea', rows: 6 },
])
const formModel = ref({})
let originModel = {}
const editing = ref(true)
const activeTab = ref('base')
const id = computed(() => router.currentRoute.value.query?.id)
const loaded = ref(false)
const showMore = ref(false) // 是否高级配置模式
const flowRef = ref()
const formRef = ref()
const inputRef = ref()

watch(() => router.currentRoute.value.query?.id, val => {
    if (!val) {
        editing.value = true
        formModel.value = {
            method: 'get',
            content: {
                nodes: [],
                testData: {
                    queryParams: []
                },
                inputParams: [],
                output: []
            }
        }
        loaded.value = true
        return
    }

    getApiInfo()
}, {
    immediate: true,
    deep: true
})

function goBack() {
    router.push('/apis/list')
}

// 获取接口详情
function getApiInfo() {
    return entityApis.findById('/base/api-draft', id.value).then(resp => {
        // 接口信息转换
        // 主要需要转换content下的内容
        if (resp.content) {
            resp.content = JSON.parse(resp.content)

            if (!resp.content.nodes) {
                resp.content.nodes = []
            }
            if (!resp.content.testData) {
                resp.content.testData = {
                    queryParams: []
                }
            } else if (!resp.content.testData.queryParams) {
                resp.content.testData.queryParams = []
            }

            if (!resp.content.inputParams) {
                resp.content.inputParams = []
            }

            if (!resp.content.output) {
                resp.content.output = []
            }
        } else {
            resp.content = {
                // 流程定义
                nodes: [],
                // 测试数据
                testData: {
                    queryParams: []
                },
                // 输入定义
                inputParams: []
            }
        }

        formModel.value = resp
        editing.value = false
        loaded.value = true
        originModel = resp
    })
}

function loadTypes() {
    return entityApis.tree('/base/api-type')
}

// 保存接口
function doSave() {
    // 数据校验
    formRef.value.validate((resp) => {
        if (!resp) {
            activeTab.value = 'base'
            return
        }

        flowRef.value.validate((resp) => {
            if (!resp) {
                activeTab.value = 'flow'
                return
            }

            if (!inputRef.value) {
                doSaveInternal()
            } else {
                // 输入参数校验
                inputRef.value.validate((resp) => {
                    if (!resp) {
                        activeTab.value = 'input'
                        return
                    }

                    doSaveInternal()
                })
            }
        })

    })

}

// 保存测试参数
function doSaveTest(changeStatus) {
    let newData = _.cloneDeep(formModel.value)
    let content = newData.content
    newData.content = JSON.stringify(content)
    newData.changeStatus = changeStatus
    entityApis.save('/base/api-draft', newData).then((resp) => {
        ElMessage.success('操作成功')

        if (changeStatus) {
            formModel.value.status = resp.status
        }
    })
}

// 参数校验通过后执行真正的保存动作
function doSaveInternal() {
    let newData = _.cloneDeep(formModel.value)

    let nodes = flowRef.value.getNodes()
    newData.content.nodes = nodes

    // 自动加载节点引用的请求参数并保存到测试参数中
    nodeUtils.loadParams(formModel.value.method, nodes, newData.content.testData)
    let content = newData.content

    newData.content = JSON.stringify(newData.content)
    newData.changeStatus = true
    entityApis.save('/base/api-draft', newData).then((resp) => {
        ElMessage.success("保存成功");
        if (resp.id) {
            formModel.value.id = resp.id
            formModel.value.status = resp.status

            // 新增后跳转编辑页面
            router.push('/apis/editor?id=' + resp.id)
        } else {
            formModel.value.status = 2
        }
        formModel.value.content = content
        originModel = _.cloneDeep(formModel.value)
        editing.value = false
    })
}

// 取消编辑或新增
function doCancel() {
    if (!id.value) {
        router.back()
    } else {
        editing.value = false
        formModel.value = _.cloneDeep(originModel)
    }
}

// 开始编辑
function startEdit() {
    editing.value = true
    if (activeTab.value == 'test') {
        activeTab.value = 'flow'
    }
}

function doPublish() {
    apiApis.publish(formModel.value.id).then(() => {
        ElMessage.success('发布成功')
        formModel.value.status = 1
    })
}

function tabChange(val) {
    flowRef.value.update()
}

// 设置成简化模式，隐藏input/output配置
function setSimpleModel() {
    let tab = activeTab.value
    if (tab == 'input' || tab == 'output') {
        activeTab.value = 'flow'
    }

    showMore.value = false
}
</script>

<style lang="scss" scoped>
.new-api {
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 400;
    overflow-y: auto;
    max-height: calc(100vh - 76px);
    padding: 16px 16px 0px 16px;

    .form {
        :deep() {

            .el-input,
            .el-select {
                max-width: 500px;
            }
        }
    }
}

.buttons {
    position: absolute;
    top: 58px;
    right: 16px;
}

.more {
    position: absolute;
}
</style>