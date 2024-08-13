<template>
    <div class="page-title">分类管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/api-type" :tree="true"></entity-manager>
</template>

<script setup>
import { ref } from 'vue'

const fields = ref([
    {label: '名称', prop: 'name', required: true},
    {label: '排序', prop: 'sort', default: 0},
    {label: '添加时间', prop: 'createTime', width: '200px', system: true}, 
    {label: '修改时间', prop: 'updateTIme', width: '200px', system: true}, 
])

</script>