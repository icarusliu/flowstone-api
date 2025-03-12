<template>
    <!-- 数据表格，与base-table不同点在于base-table支持远程获取数据，而data-table用于直接传入数据的情况，并且不进行分页 -->
    <el-table :data="rows" row-key="id" :default-expand-all="defaultExpandAll != false" border 
        @rowClick="onRowClick"
        ref="tableRef">
        <el-table-column width="52px" label="序号" type="index" fixed v-if="showIndex != false"/>
        <el-table-column v-if="showSelection" type="" width="60px">
            <template #header>
                <el-checkbox v-model="checkedAll" :indeterminate="selection.length > 0 && selection.length != rowLength" 
                    @change="selectAll"/>
            </template>
            <template #default="{row}">
                <el-checkbox v-model="row.checked" @change="rowSelected(row)"/>
            </template>
        </el-table-column>

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
import { nextTick, onMounted, ref } from 'vue'
import baseTableColumn from './base-table-column.vue';
import * as utils from '@/utils/utils'

const props = defineProps(["fields", "defaultExpandAll", "showIndex", "showSelection"])
const emits = defineEmits(["rowClick", "select"])
const tableRef = ref()
const rows = defineModel()
const checkedAll = ref(false)
const selection = computed(() => {
    const result = []
    utils.loop(rows.value, row => {
        if (row.checked) {
            result.push(row.id)
        }
    })

    return result
})
const rowLength = computed(() => {
    let result = 0
    utils.loop(rows.value, () => {
        result++
    });
    return result
})

watch(rows, reloadChedkedAll)

function onRowClick(params) {
    emits('rowClick', params)
}

// 获取选中的行
function getSelectionRows() {
    return selection.value
}

function selectAll() {
    utils.loop(rows.value, row => {
        row.checked = checkedAll.value
    })
}

function reloadChedkedAll() {
    if (selection.value.length == rowLength.value) {{
        checkedAll.value = true
    }} else {
        checkedAll.value = false
    }
}

function rowSelected(row) {
    utils.loop(row.children, subItem => {
        subItem.checked = row.checked
    })

    reloadChedkedAll()
}

defineExpose({
    getSelectionRows,
})
</script>