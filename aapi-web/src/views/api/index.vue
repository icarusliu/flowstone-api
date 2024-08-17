<template>
    <div class="h-100">
        <div class="page-title">接口管理</div>
        <div class="type-list mr-4 f-left">
            <div class="title p-2 font-bold">分类</div>
            <base-tree class="tree" apiPrefix="/base/api-type" @currentChange="selectType">
            </base-tree>
        </div>
        <div class="content f-left">
            <div class="mb-4 space-between">
                <div>
                    <el-input class="wf-3 mr-4" placeholder="请输入关键字进行查询" v-model="params.keyLike" clearable></el-input>
                    <el-button type="primary" @click="doQuery">查询</el-button>
                </div>
                <el-button type="primary" @click="showNewApi">新增</el-button>
            </div>
            <base-table :fields="fields" :dataSupplier="dataSupplier" :params="params" ref="tableRef"></base-table>
        </div>

    </div>
</template>

<script setup>
import { ref } from 'vue'
import * as entityApis from '@/apis/entity.js'
import * as apiApis from '@/apis/api.js'
import { ElMessageBox, ElMessage } from 'element-plus'
import * as _ from 'lodash'
import { useRouter } from 'vue-router'
import baseTree from '../../components/base-tree.vue'
import * as utils from '@/utils/utils'

const fields = ref([
    { label: '分类', prop: 'typeName', width: '140px' },
    { label: '名称', prop: 'name', width: '200px' },
    { label: '路径', prop: 'path', width: '300px', converter: val => '/dua/' + val },
    {
        label: '状态', prop: 'status', width: '100px', converter: val => {
            switch (val) {
                case 0: return '未发布';
                case 1: return '已发布';
                case 2: return '已修改';
                case 3: return '已下线';
                default: return '未知'
            }
        }
    },
    { label: '备注', prop: 'remark' },
    {label: '添加时间', prop: 'createTime', width: '150px', system: true}, 
    {label: '修改时间', prop: 'updateTime', width: '150px', system: true}, 
    {
        label: '操作', type: 'operations', width: '150px', buttons: [
            { label: '详情', type: 'primary', action: goEdit },
            { label: '文档', type: 'primary', action: goDoc },
            { label: '下线', type: 'danger', action: doOffline, visible: row => row.status == 1 || row.status == 2 },
            { label: '发布', type: 'success', action: doPublish, visible: row => row.status != 1 },
        ],
        fixed: 'right'
    }
])
const prefix = '/base/api-draft'
const tableRef = ref()
const router = useRouter()
const selectedType = ref()
const params = reactive({})

function dataSupplier(params) {
    if (selectedType.value) {
        params.typeIds = selectedType.value
    } else {
        delete params.typeIds
    }

    return entityApis.load(prefix, params)
}

// 选择分类
function selectType(type) {
    // 需要将所有子分类的id全部拼上进行查询
    const ids = []
    utils.loop(type, item => {
        item.id && ids.push(item.id)
    })

    selectedType.value = ids
    tableRef.value.reload()
}

// 新增接口
function showNewApi() {
    router.push('/apis/editor')
}

// 进行编辑
function goEdit(row) {
    router.push('/apis/editor?id=' + row.id)
}

// 接口下线
function doOffline(row) {
    ElMessageBox.confirm('确定下线当前接口？').then(() => {
        apiApis.offline(row.id).then(() => {
            row.status = 3
            ElMessage.success('下线成功')
        })
    })
}

// 接口发布
function doPublish(row) {
    apiApis.publish(row.id).then(() => {
        row.status = 1
        ElMessage.success('发布成功')
    })
}

// 查询
function doQuery() {
    tableRef.value.reload()
}

function goDoc(row) {
    router.push('/apis/doc?id=' + row.id)
}
</script>

<style lang="scss" scoped>
.type-list {
    min-width: 200px;
    width: 200px;
    background-color: #fafafa;
    border: 1px solid #f1f1f1;
    height: calc(100% - 32px);
    overflow: auto;

    .title {
        border-bottom: 1px solid #f1f1f1;
        padding-bottom: 4px;
    }

    .tree {
        background-color: transparent;
    }
}

.content {
    width: calc(100% - 218px);
}
</style>