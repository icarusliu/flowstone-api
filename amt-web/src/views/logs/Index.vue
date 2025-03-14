<template>
    <SearchForm v-model="params" :fields="queryFields" @query="reload"></SearchForm>
    <base-table :fields="fields" :dataSupplier="dataSupplier" :defaultExpandAll="false" @rowClick="onRowClick" :pageSimple="true"
        ref="tableRef" :params="params" />
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { ref, onMounted } from 'vue'
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