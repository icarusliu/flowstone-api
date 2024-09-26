<template>
    <title-bar title="运行日志"></title-bar>
    <SearchForm v-model="params" :fields="queryFields" @query="reload"></SearchForm>
    <!-- <div class="d-flex my-2">
        <el-input placeholder="请输入接口信息" class="mr-2 input" clearable v-model="params.key" @change="reload" />
        <el-select placeholder="请选择客户端" class="mr-2 input" clearable v-model="params.clientId" @change="reload">
            <el-option v-for="client in clients" :label="client.name" :value="client.id"></el-option>
        </el-select>
        <el-select placeholder="请选择执行状态" class="input" clearable v-model="params.status" @change="reload">
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
        </el-select>
    </div> -->
    <base-table :fields="fields" :dataSupplier="dataSupplier" :defaultExpandAll="false" @rowClick="onRowClick"
        ref="tableRef" :params="params" />
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { ref, h, onMounted } from 'vue'
import { ElTag } from 'element-plus'
import * as clientApis from '@/apis/client'
import SearchForm from '../../components/search-form.vue';
import * as logFields from './fields'

const clients = ref([])
const tableRef = ref()
const params = ref({})
const fields = ref(logFields.tableFields)
const queryFields = ref(logFields.queryFields)

onMounted(() => {
    clientApis.getAllClients().then(resp => {
        clients.value = resp
    })
})

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