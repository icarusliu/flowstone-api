<template>
    <!-- 可编辑表格 -->
    <div class="mb-2">
        <el-button type="primary" plain @click="newRow" :disabled="readonly" v-if="showNew != false">新增</el-button>
        <slot name="appendButtons"></slot>
    </div>
    <el-table :data="data" class="table" :row-key="rowKey || 'id'" default-expand-all :border="!!border" @cell-dblclick="cellDblClick" :key="key">
        <el-table-column v-if="showIndex" type="index" label="序号" width="60px" align="center" />

        <template v-for="field in fields" :key="field.prop">
            <el-table-column :label="field.label" :width="field.width" :align="field.align"
                :headerAlign="field.headerAlign || field.align || 'center'" :sortable="field.sortable">
                <template #default="{ row, $index }">
                    <EditTableColumn :row="row" :index="$index" :field="field" :readonly="readonly" :fields="fields" :editable="editable"
                        :mode="mode" />
                </template>
            </el-table-column>
        </template>

        <!-- 操作列 -->
        <el-table-column label="" :width="operationsWidth || '60px'" v-if="showOperations != false">
            <template #default="{ $index, row }">
                <slot name="buttons" :row="row" :index="$index">
                </slot>
                <ElLink v-if="withDelete != false" type="danger" @click="doDelete(row.id)" :disabled="readonly || row.readonly">删除</ElLink>
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

const props = defineProps(["fields", "defRow", "readonly", "showNew", "operationsWidth", "rowKey", "showOperations", "border", "mode", 'withDelete', 'showIndex', 'editable'])
const data = defineModel()
const emits = defineEmits(["delete"])
const key = ref(uuid.v4())

// 新增行
function newRow() {
    const row = _.cloneDeep(props.defRow || {})
    row.id = uuid.v4()
    if (!data.value) {
        data.value = []
    }
    data.value.push(row)
}

// 删除行
function doDelete(id) {
    // 有可能删除的是子元素
    utils.removeFromTree(data.value, item => item.id == id)
    key.value = uuid.v4()

    emits('delete', id)
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
}
</style>