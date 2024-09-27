<template>
    <!-- 数据表格，与base-table不同点在于base-table支持远程获取数据，而data-table用于直接传入数据的情况，并且不进行分页 -->
    <el-table :data="rows" row-key="id" :default-expand-all="defaultExpandAll != false" border @rowClick="onRowClick"
        ref="tableRef">
        <el-table-column width="52px" label="序号" type="index" fixed v-if="showIndex != false"/>

        <el-table-column v-for="field in fields" :prop="field.prop" :key="field.prop" :label="field.label"
            :fixed="field.fixed" :width="field.width" :type="(field.type == 'expand' || field.type == 'selection') ? field.type : 'default'"
            :min-width="field.minWidth">

            <template v-if="field.type != 'selection'" #default="{ row, $index }">
                <base-table-column :row="row" :field="field" :index="$index"/>
            </template>
        </el-table-column>

        <slot name="append"></slot>
    </el-table>
</template>

<script setup>
import { ref } from 'vue'
import baseTableColumn from './base-table-column.vue';

const props = defineProps(["fields", "defaultExpandAll", "showIndex"])
const emits = defineEmits(["rowClick"])
const tableRef = ref()
const rows = defineModel()

function onRowClick(params) {
    emits('rowClick', params)
}

function toggleRowExpansion(row, expanded) {
    tableRef.value.toggleRowExpansion(row, expanded)
}

function getSelectionRows() {
    return tableRef.value.getSelectionRows()
}

defineExpose({
    toggleRowExpansion,
    getSelectionRows
})
</script>