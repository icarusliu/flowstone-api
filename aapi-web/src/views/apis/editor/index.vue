<template>
    <!-- 接口编辑 -->
    <div class="new-api bg-white br-1 border-box">
        <el-page-header :content="id ? '编辑接口' : '新增接口'" @back="goBack"></el-page-header>

        <el-tabs class="mt-4 tabs" finish-status="success" v-model="activeTab">
            <el-tab-pane label="基础信息" name="1">
                <base-form :fields="fields" v-model="formModel" class="form" :readonly="!editing">
                </base-form>
            </el-tab-pane>
            <el-tab-pane label="节点配置" name="2">
                <apiFlow v-model="formModel" ref="flowRef" :editing="editing" v-if="loaded"/>
            </el-tab-pane>
            <el-tab-pane label="接口测试" v-if="!editing" name="3">
                <apiTest :apiInfo="formModel" :editing="editing"  v-if="loaded"/>
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
import apiFlow from './api-flow.vue'
import apiTest from './api-test.vue'
import * as apiApis from '@/apis/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const fields = ref([
    { label: '接口名称', prop: 'name', required: true },
    { label: '接口路径', prop: 'path', required: true, prepend: '/dua/' },
    { label: '接口分类', prop: 'typeId', required: true, type: 'tree-select', options: loadTypes },
    { label: '接口备注', prop: 'remark', type: 'textarea', rows: 6 },
])
const formModel = ref({})
let originModel = {}
const flowRef = ref()
const editing = ref(true)
const activeTab = ref('1')
const id = computed(() => router.currentRoute.value.query?.id)
const loaded = ref(false)

watch(() => router.currentRoute.value.query?.id, val => {
    if (!val) {
        editing.value = true
        formModel.value = {}
        loaded.value = true
        return
    }

    getApiInfo()
}, {
    immediate: true,
    deep: true
})

function goBack() {
    router.back()
}

// 获取接口详情
function getApiInfo() {
    return entityApis.findById('/base/api-draft', id.value).then(resp => {
        formModel.value = resp
        editing.value = false
        originModel = resp
        loaded.value = true
    })
}

function loadTypes() {
    return entityApis.tree('/base/api-type')
}

// 保存接口
function doSave() {
    formModel.value.content = JSON.stringify(flowRef.value.getContent())
    entityApis.save('/base/api-draft', formModel.value).then((resp) => {
        ElMessage.success("操作成功");
        if (resp.id) {
            formModel.value.id = resp.id
            formModel.value.status = resp.status

            // 新增后跳转编辑页面
            router.push('/apis/editor?id=' + resp.id)
        } else {
            formModel.value.status = 2
        }
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
    if (activeTab.value == '3') {
        activeTab.value = '2'
    }
}

function doPublish() {
    apiApis.publish(formModel.value.id).then(() => {
        ElMessage.success('发布成功')
        formModel.value.status = 1
    })
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
</style>