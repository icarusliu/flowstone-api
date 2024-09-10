<template>
    <!-- 可编辑表格 -->
    <div class="mb-2">
        <el-button type="primary" @click="newRow" :disabled="readonly" v-if="showNew != false">新增</el-button>
    </div>
    <el-table :data="data" class="table" :row-key="rowKey" default-expand-all :border="!!border"
        @cell-dblclick="cellDblClick">
        <el-table-column v-for="field in fields" :key="field.prop" :label="field.label" :width="field.width"
            :align="field.align" :headerAlign="field.headerAlign || field.align || 'center'" :sortable="field.sortable">
            <template #default="{ row, $index }">
                <!-- 直接编辑模式，如果当前行编辑中或者是直接编辑模式或者是checkbox时直接显示编辑器 -->
                <EditTableColumn v-if="row.editing || mode != 'click' || field.type == 'checkbox'" :row="row"
                    :index="$index" :field="field" :readonly="readonly" />

                <!-- 非直接编辑模式，双击单元格进行编辑 -->
                <BaseTableColumn v-else :row="row" :field="field"/>
            </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="" :width="operationWidth || '60px'" v-if="showOperations != false">
            <template #default="{ $index, row }">
                <slot name="buttons" :row="row" :index="$index">
                </slot>
                <ElLink type="danger" @click="doDelete(row.id)" :disabled="readonly">删除</ElLink>
            </template>
        </el-table-column>
    </el-table>
</template>

<script setup>
import { ElLink } from 'element-plus';
import * as _ from 'lodash'
import * as utils from '@/utils/utils.js'
import * as uuid from 'uuid'
import EditTableColumn from './edit-table-column.vue';
import BaseTableColumn from './base-table-column.vue'

const props = defineProps(["fields", "defRow", "readonly", "showNew", "operationWidth", "rowKey", "showOperations", "border", "mode"])
const data = defineModel()
const emits = defineEmits(["delete"])

// 新增行
function newRow() {
    const row = _.cloneDeep(props.defRow)
    row.id = uuid.v4()
    data.value.push(row)
}

// 删除行
function doDelete(id) {
    // 有可能删除的是子元素
    utils.removeFromTree(data.value, item => item.id == id)

    emits('delete')
}

// 单元格双击
function cellDblClick(row, column, cell) {
    utils.loop(data.value, item => item.editing = false)
    row.editing = true
}

defineExpose({ newRow })
</script>

<style lang="scss" scoped>
:deep() {
    td.el-table__cell>div {
        padding: 0 4px;
    }

    .el-table {

        .el-select__wrapper,
        .el-input__wrapper {
            font-size: 12px;
            padding: 0 4px;
        }
    }
}</style>