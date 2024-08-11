<template>
    <div class="page-title">数据源</div>
    <entity-manager :fields="fields" apiPrefix="/base/ds"></entity-manager>
</template>

<script setup>
import { ref } from 'vue'

const fields = ref([
    { label: '编码', prop: 'code', required: true, width: '100px', maxLength: 16 },
    { label: '名称', prop: 'name', required: true, width: '200px' },
    {
        label: '类型', prop: 'type', required: true,
        default: 'mysql',
        type: 'radio', options: [
            { label: 'mysql', value: 'mysql' }
        ],
        width: '80px'
    },
    { label: 'URL', prop: 'url', required: true },
    { label: '用户名', prop: 'username', required: true, width: '100px' },
    { label: '密码', prop: 'password', required: true, width: '100px' },
])

</script>