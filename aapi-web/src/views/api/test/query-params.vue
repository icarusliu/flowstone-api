<template>
    <edit-table v-model="data" :fields="fields" :defRow="{}"/>
</template>

<script setup>
import editTable from '@/components/edit-table.vue';

const data = defineModel()

const fields = ref([
    { label: '参数', prop: 'code' },
    { label: '值', prop: 'value' },
])

</script>