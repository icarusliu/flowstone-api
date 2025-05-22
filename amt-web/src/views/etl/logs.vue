<template>
    <div class="content-panel">
        <search-form :fields="queryFields" v-model="params" @query="doQuery"></search-form>
        <base-table :fields="fields" :params="params" ref="tableRef" :dataSupplier="loadData" :pageSimple="true"></base-table>
    </div>
</template>
<script setup>
import baseTable from '@/components/base-table/index.vue'
import searchForm from '@/components/search-form.vue'
import https from '@/utils/https'

const fields = ref([
    { label: '任务编码', prop: 'jobCode', width: '200px' },
    { label: '任务名称', prop: 'jobName', width: '200px' },
    { label: '执行时间', prop: 'executeTime', width: '150px' },
    { label: '数据日期', prop: 'dataDate', width: '100px' },
    { label: '执行结果', prop: 'status', converter: val => !val ? '执行成功' : '执行失败', width: '100px' },
    { label: '异常信息', prop: 'errorMsg' }
])
const queryFields = ref([
    { label: '关键字', prop: 'key' },
    { label: '执行状态', prop: 'status', type: 'select', options: () => [{ label: '成功', value: 0 }, { label: '失败', value: 1 }] },
    { label: '执行日期', type: 'datePicker', dateType: 'daterange', prop: 'dates' },
])
const params = ref({})
const tableRef = ref()

function loadData(params) {
    if (params.dates) {
        params.beginDate = params.dates[0]
        params.endDate = params.dates[1]
    }
    return https.post('/etl/log/query', params)
}

function doQuery() {
    tableRef.value.reload()
}
</script>
