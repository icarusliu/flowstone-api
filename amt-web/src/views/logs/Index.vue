<template>
    <title-bar title="运行日志"></title-bar>
    <div class="d-flex my-2">
        <el-input placeholder="请输入接口信息" class="mr-2 input" clearable v-model="params.key" @change="reload" />
        <el-select placeholder="请选择执行状态" class="input" clearable v-model="params.status" @change="reload">
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
        </el-select>
    </div>
    <base-table :fields="fields" :dataSupplier="dataSupplier" :defaultExpandAll="false" @rowClick="onRowClick"
        ref="tableRef" :params="params" />
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { ref, h } from 'vue'
import { ElTag } from 'element-plus'

const tableRef = ref()
const params = ref({})
const fields = ref([
    { label: '接口', prop: 'apiName', width: '300px' },
    { label: '地址', prop: 'apiPath', converter: val => '/dua/' + val },
    { label: '执行时间', prop: 'createTime', width: '150px' },
    { label: '执行状态', prop: 'status', width: '80px', type: 'tag', tagType: val => {
        if (val == 0) {
            return {
                type: 'success',
                text: '成功'
            } 
        } else {
            return {
                type: 'danger',
                text: '失败'
            }
        }
    }},
    { label: '执行耗时', prop: 'spentTime', width: '80px', converter: val => val + 'ms' },
    // { label: '异常信息', prop: 'errorMsg' },
    {
        label: '', type: 'expand', prop: 'result', render: (val, row) => {
            let arr = []
            if (row.result) {
                row.result.split('\r\n').forEach(line => {
                    arr.push(h('div', { class: line.startsWith('\t') ? 'ml-4' : '' }, line))
                })
            }

            return h('div', { class: 'p-4' }, [
                h('div', { class: 'page-title' }, '请求参数'),
                h('div', row.params),
                h('div', { class: 'page-title mt-8' }, row.status == 0 ? '返回结果' : '异常详情'),
                ...arr
            ])
        }
    }
])

function onRowClick(row) {
    tableRef.value.toggleRowExpansion(row)
}

function dataSupplier(params) {
    return entityApis.load('/base/api-log', params);
}

function reload() {
    tableRef.value.reload()
}
</script>

<style lang="scss" scoped>
.input {
    width: 300px;
}
</style>