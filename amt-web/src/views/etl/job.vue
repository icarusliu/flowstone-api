<template>
    <!-- 任务列表 -->
    <div class="d-flex page-content">
        <type-tree v-model="currentType" class="tree" @select="selectType" />

        <div class="flex-auto">
            <entity-manager apiPrefix="/etl/job" :fields="fields" :queryFields="queryFields" ref="entityManagerRef" :params="params"
                :withNew="false" operationsWidth="220px">
                <template #rowButtons="{ row }">
                    <buttons :buttons="rowButtons" :data="row" />
                </template>

                <template #buttons>
                    <el-button type="primary" @click="showNewJob">新增</el-button>
                </template>
            </entity-manager>
        </div>

        <new-job v-if="jobVisible" class="full-panel" @close="closeJob" :item="editingJob" :type="currentType"></new-job>
        <job-depend v-if="dependVisible" class="full-panel" :job="editingJob" @close="closeDepend"></job-depend>
    </div>
</template>
<script setup>
import newJob from './job-detail.vue'
import jobDepend from './job-depend.vue'
import typeTree from './type-tree.vue'
import { ElMessageBox, ElMessage, ElTag, ElIcon } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import https from '@/utils/https'
import buttons from '@/components/buttons.vue';
import { h } from 'vue'

const currentType = ref({})
const fields = ref([
    {
        label: '分类', prop: 'typeId', showInTable: false, type: 'tree-select', required: true, options: () => {
            return https.get('/etl/job-type/tree')
        }
    },
    { label: '编码', prop: 'code', required: true },
    { label: '名称', prop: 'name', required: true },
    {
        label: '任务类型', prop: 'type', type: 'select', width: '140px', options: [{ label: '数据同步任务', value: 'sync' }, { label: 'SQL加工任务', value: 'sql' }],
        converter: (val) => val == 'sync' ? '数据同步任务' : 'SQL加工任务'
    },
    {
        label: '触发规则', prop: 'autoTrigger', width: '140px', align: 'center', render: (val, row) => {
            if (!val) {
                return h(ElTag, () => '定时触发')
            }

            // 依赖触发时需要检查是否有配置依赖
            const b = !row.depends || !row.depends.length;
            if (b) {
                return h('span', [
                    h(ElIcon, { style: { marginRight: '2px', fontSize: '14px', verticalAlign: 'middle' } }, [
                        h(Warning, { color: 'red' })
                    ]),
                    h(ElTag, { type: 'danger' }, () => '请配置依赖')
                ])
            }

            return h(ElTag, {type: 'success'}, () => '依赖触发')
        }
    },
    {
        label: '状态', prop: 'version', width: '100px', render: (val, row) => {
            if (!row.publishedVersion) {
                return h(ElTag, { type: 'info' }, () => '草稿')
            } else if (row.publishedVersion == val) {
                return h(ElTag, { type: 'success' }, () => '已发布')
            }

            return h(ElTag, { type: 'warning' }, () => '修改中')
        }, align: 'center'
    },
    { label: '创建时间', prop: 'createTime', width: '200px' },
    { label: '修改时间', prop: 'updateTime', width: '200px' }
])
const queryFields = [{ label: '关键字', prop: 'key', placeholder: '请输入关键字进行查询' }]
const params = ref({})
const editingJob = ref({})
const entityManagerRef = ref()
const rowButtons = ref([
    { label: '详情', action: goEdit },
    { label: '任务依赖', action: showDepend },
    { label: '发布', action: publish, type: 'success', display: row => row.version != row.publishedVersion || !row.publishedVersion },
    { label: '下线', action: offline, type: 'danger', display: row => row.version == row.publishedVersion && row.publishedVersion },
    { label: '血缘分析', action: showBlood },
    { label: '删除', type: 'danger', action: doDelete }
])

function goEdit(row) {
    editingJob.value = row
    jobVisible.value = true
}

function selectType(type) {
    fields.value[0].default = type.id
    params.value.typeId = type.id
    entityManagerRef.value.reload()
}

function doDelete(row) {
    ElMessageBox.confirm('确定删除当前记录？').then(() => {
        https.del('/etl/job/delete/' + row.id).then(() => {
            entityManagerRef.value.reload()
            ElMessage.success('删除成功')
        })
    })
}

const jobVisible = ref(false)
function showNewJob() {
    editingJob.value = null
    jobVisible.value = true
}
function closeJob(val) {
    jobVisible.value = false
    entityManagerRef.value.reload()
}

const dependVisible = ref(false)
function showDepend(row) {
    editingJob.value = row
    dependVisible.value = true
}
function closeDepend() {
    dependVisible.value = false
}

function showBlood() {
    ElMessage.warning('功能暂未实现')
}

function publish(job) {
    https.get('/etl/job/publish', { jobId: job.id }).then(() => {
        job.publishedVersion = job.version
        ElMessage.success('操作成功')
    })
}

function offline(job) {
    ElMessageBox.confirm('确定下线当前任务？').then(() => {
        https.get('/etl/job/offline', { jobId: job.id }).then(() => {
            job.publishedVersion = 0
            ElMessage.success('操作成功')
        })
    })
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