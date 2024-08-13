<template>
    <div class="page-title">数据源</div>
    <entity-manager :fields="fields" apiPrefix="/base/ds" operationsWidth="100px"></entity-manager>
</template>

<script setup>
import { ref } from 'vue'

const fields = ref([
    { label: '编码', prop: 'code', required: true, width: '80px', maxLength: 16 },
    { label: '名称', prop: 'name', required: true, width: '160px' },
    {
        label: '类型', prop: 'type', required: true,
        default: 'mysql',
        type: 'radio', options: [
            { label: 'mysql', value: 'mysql' }
        ],
        width: '80px'
    },
    { label: 'URL', prop: 'url', required: true },
    { label: '用户名', prop: 'username', required: true, width: '80px' },
    {label: '添加时间', prop: 'createTime', width: '150px', system: true}, 
    {label: '修改时间', prop: 'updateTIme', width: '150px', system: true}, 
])

</script>