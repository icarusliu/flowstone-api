<template>
    <div class="page-title">定时任务</div>
    <entity-manager apiPrefix="/base/schedule-task" :fields="fields" operationsWidth="200px">
        <template #prefixButtons="{ row }">
            <el-link type="primary" v-show="!row.started && row.status" @click="start(row)" class="mr-2">启动</el-link>
            <!-- <el-link type="primary" v-show="row.started && row.status" @click="stop(row)" class="mr-2">停止</el-link> -->
            <el-link type="primary" v-show="row.status" @click="setInvalid(row)" class="mr-2">停用</el-link>
            <el-link type="primary" v-show="!row.status" @click="setValid(row)" class="mr-2">启用</el-link>
        </template>
    </entity-manager>
</template>

<script setup>
import EntityManager from '@/components/entity-manager.vue'
import * as apiApis from '@/apis/api.js'
import * as taskApis from '@/apis/task'
import { ElMessage } from 'element-plus'

const fields = [
    { label: '名称', prop: 'name', required: true },
    {
        label: '执行接口', prop: 'apiId', required: true, type: 'select',
        valueKey: 'id',
        change: (val, form, { item }) => {
            form.apiName = item.name
        },
        options: apiApis.selectApi, converter: (val, row) => row.apiName
    },
    { label: '调度表达式', prop: 'cron', width: '200px', placeholder: '使用cron表达式', required: true, type: 'cron' },
    { label: '启用状态', prop: 'status', width: '100px', needNew: false, converter: val => val == 0 ? '停用' : '启用' },
    { label: '启动状态', prop: 'started', width: '80px', needNew: false, converter: val => val == 1 ? '已启动' : '未启动' },
    { label: '创建时间', prop: 'createTime', width: '200px', needNew: false },
]

function start(row) {
    taskApis.start(row.id).then(() => {
        ElMessage.success('启动成功')
        row.started = true
    })
}

function stop(row) {
    taskApis.stop(row.id).then(() => {
        ElMessage.success('停止成功')
        row.started = false
    })
}

function setValid(row) {
    taskApis.setValid(row.id).then(() => {
        ElMessage.success("启用成功")
        row.started = true
        row.status = 1
    })
}

function setInvalid(row) {
    taskApis.setInvalid(row.id).then(() => {
        ElMessage.success("信用成功")
        row.started = false
        row.status = 0
    })
}
</script>