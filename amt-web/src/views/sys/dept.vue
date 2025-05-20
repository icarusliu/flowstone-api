<template>
    <entity-manager apiPrefix="/sys/dept" :fields="fields" ref="entityManagerRef" :tree="true"
        operationsWidth="140px">
    </entity-manager>
</template>

<script setup name="sysDept">
import * as _ from 'lodash'

const fields = [
    { label: '编码', prop: 'code', required: true, width: '200px' },
    { label: '名称', prop: 'name', required: true },
    { label: '排序', prop: 'sort', width: '80px', default: 0 },
    { label: '创建时间', prop: 'createTime', width: '160px', needNew: false },
]
const entityManagerRef = ref()
</script>