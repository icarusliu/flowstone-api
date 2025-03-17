<template>
    <!-- 模型管理 -->
    <div class="d-flex page-content">
        <edit-tree v-model="currentType" class="tree" @select="selectType" apiPrefix="/base/model-type" title="任务分类"
            :newFields="typeFields" />

        <div class="flex-auto">
            <entity-manager apiPrefix="/base/model" :fields="fields" :queryFields="queryFields" ref="entityManagerRef" :params="params"
                :withNew="false" operationsWidth="220px">
                <template #rowButtons="{ row }">
                    <buttons :buttons="rowButtons" :data="row" />
                </template>

                <template #buttons>
                    <el-button type="primary" @click="showNewModel">新增</el-button>
                </template>
            </entity-manager>
        </div>

        <model-detail v-if="modelVisible" class="full-panel" @close="closeModel" :modelId="editingModel.id"
            :type="currentType"></model-detail>
    </div>
</template>
<script setup>
import modelDetail from './model-detail.vue'
import editTree from '@/components/edit-tree/index.vue'
import { ElMessageBox, ElMessage, ElTag, ElIcon, ElLoading } from 'element-plus'
import https from '@/utils/https'
import buttons from '@/components/buttons.vue';
import { h } from 'vue'
import { useRouter } from 'vue-router';

const router = useRouter()
const currentType = ref({})
const fields = ref([
    { label: '分类', prop: 'typeId', showInTable: false },
    { label: '编码', prop: 'code', required: true },
    { label: '名称', prop: 'name', required: true },
    {
        label: '状态', prop: 'status', width: '140px', render: (val, row) => {
            if (!val) {
                return h(ElTag, { type: 'info' }, () => '草稿')
            } else if (val == 1) {
                return h(ElTag, { type: 'success' }, () => '已应用')
            } else if (val == 2) {
                return h(ElTag, { type: 'warning' }, () => '修改中')
            }

            return h(ElTag, { type: 'info' }, () => '已下线')
        }, align: 'center'
    },
    { label: '创建时间', prop: 'createTime', width: '200px' },
    { label: '修改时间', prop: 'updateTime', width: '200px' }
])
const queryFields = [{ label: '关键字', prop: 'key', placeholder: '请输入关键字进行查询' }]
const typeFields = [
    { label: '分类名称', prop: 'name', required: true }
]
const params = ref({})
const editingModel = ref({})
const entityManagerRef = ref()
const rowButtons = ref([
    { label: '详情', action: goEdit },
    { label: '应用', action: publish, type: 'success', display: row => row.status != 1 },
    { label: '下线', action: offline, type: 'danger', display: row => row.status == 1 },
    { label: '删除', type: 'danger', action: doDelete },
    { label: '数据管理', action: showData, display: row => row.status != 0 && row.status != 3 },
])

function goEdit(row) {
    editingModel.value = row
    modelVisible.value = true
}

function selectType(type) {
    fields.value[0].default = type.id
    params.value.typeId = type.id
    entityManagerRef.value.reload()
}

function doDelete(row) {
    ElMessageBox.confirm('确定删除当前记录？').then(() => {
        https.del('/base/model/delete/' + row.id).then(() => {
            entityManagerRef.value.reload()
            ElMessage.success('删除成功')
        })
    })
}

const modelVisible = ref(false)
function showNewModel() {
    editingModel.value = {}
    modelVisible.value = true
}
function closeModel(val) {
    modelVisible.value = false
    entityManagerRef.value.reload()
}

function publish(model) {
    let loading = ElLoading.service({
        text: '发布中'
    })
    https.get('/base/model/publish', { id: model.id }).then(() => {
        model.publishedVersion = model.version
        ElMessage.success('操作成功')
    }).finally(() => {
        loading.close()
    })
}

function offline(model) {
    ElMessageBox.confirm('确定下线当前任务？').then(() => {
        https.get('/base/model/offline', { id: model.id }).then(() => {
            model.publishedVersion = 0
            ElMessage.success('操作成功')
        })
    })
}

function showData(row) {
    router.push(`/base/model-data?modelId=${row.id}`)
}
</script>

<style lang='scss' scoped>
.tree {
    min-width: 300px;
}

.page-content {
    position: relative;
    height: 100%;
    box-sizing: border-box;

    .full-panel {
        width: 100%;
        height: 100%;
        position: absolute;
        left: 0;
        top: 0;
        background: #fff;
        z-index: 100;
    }
}
</style>