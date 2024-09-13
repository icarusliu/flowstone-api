<template>
    <div class="page-title">接入方管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/supplier">
        <template #prefixButtons="{row}">
            <el-link type="primary" class="mr-2" @click="configAuth(row)">鉴权</el-link>
        </template>
    </entity-manager>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const fields = ref([
    { label: '名称', prop: 'name', required: true, width: '200px' },
    { label: 'URL', prop: 'url', required: true }, 
    { label: '添加时间', prop: 'createTime', width: '160px', system: true },
    { label: '修改时间', prop: 'updateTime', width: '160px', system: true },
])

function configAuth(item) {
    router.push('/manager/supplier-auth/' + item.id)
}

</script>