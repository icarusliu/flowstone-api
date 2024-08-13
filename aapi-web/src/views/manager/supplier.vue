<template>
    <div class="page-title">接入方管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/supplier"></entity-manager>
</template>

<script setup>
import { ref } from 'vue'

const fields = ref([
    { label: '名称', prop: 'name', required: true, width: '200px' },
    { label: 'URL', prop: 'url', required: true }, 
    { label: '添加时间', prop: 'createTime', width: '160px', system: true },
    { label: '修改时间', prop: 'updateTIme', width: '160px', system: true },
])

</script>