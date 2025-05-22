<template>
    <entity-manager :fields="fields" apiPrefix="/base/ds" operationsWidth="140px">
        <template #formButtons="{ model }">
            <el-button @click="doTest(model)" :loading="loading">测试</el-button>
        </template>
    </entity-manager>
</template>

<script setup>
import { ref } from 'vue'
import { dsFields } from './fields'
import * as dsApis from '@/apis/ds'
import { ElMessage } from 'element-plus'

const fields = ref(dsFields)
const loading = ref(false)

function doTest(model) {
    loading.value = true
    dsApis.test(model).then(() => {
        ElMessage.success('测试成功')
    }).catch(() => {
        ElMessage.error("连接失败")
    }).finally(() => {
        loading.value = false
    })
}
</script>