<template>
    <edit-table v-model="data" :fields="fields" :defRow="{}"/>
</template>

<script setup>
import editTable from '@/components/edit-table.vue';

const data = defineModel()

const fields = ref([
    { label: '参数', prop: 'code'},
    { label: '值', prop: 'value'},
    // { label: '类型', prop: 'type' },
    // { label: '说明', prop: 'remark', placeholder: '请输入参数说明，做文档使用，可为空' },
])

</script>