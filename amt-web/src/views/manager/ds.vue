<template>
    <div class="page-title">数据源</div>
    <entity-manager :fields="fields" apiPrefix="/base/ds" operationsWidth="100px">
        <template #formButtons="{ model }">
            <el-button @click="doTest(model)">测试</el-button>
        </template>
    </entity-manager>
</template>

<script setup>
import { ref } from 'vue'
import { dsFields } from './fields'
import * as dsApis from '@/apis/ds'
import { ElMessage } from 'element-plus'

const fields = ref(dsFields)

function doTest(model) {
    dsApis.test(model).then(() => {
        ElMessage.success('测试成功')
    }).catch(() => {
        ElMessage.error("连接失败")
    })
}
</script>