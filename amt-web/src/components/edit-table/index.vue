<template>
    <!-- 可编辑表格 -->
    <div class="mb-2">
        <el-button type="primary" plain @click="newRow" :disabled="readonly" v-if="showNew != false">新增</el-button>

        <template v-if="showSortButtons">
            <el-button :disabled="readonly" @click="toTop" icon="Upload">移至顶部</el-button>
            <el-button :disabled="readonly" @click="up" icon="top">上移</el-button>
            <el-button :disabled="readonly" @click="down" icon="bottom">下移</el-button>
            <el-button :disabled="readonly" @click="toBottom" icon="download">移至底部</el-button>
        </template>

        <slot name="appendButtons"></slot>
    </div>
    <el-table :data="data" class="table" :row-key="rowKey || 'id'" default-expand-all :border="!!border" @cell-dblclick="cellDblClick"
        :key="key" stripe :maxHeight="height" ref="tableRef" @selectionChange="selectionChange">
        <el-table-column v-if="showSelection || showSortButtons" type="selection" align="center" width="40px" :selectable="selectable" />
        <el-table-column v-if="showIndex" type="index" label="序号" width="60px" align="center" />

        <template v-for="field in fields" :key="field.prop">
            <el-table-column :label="field.label" :width="field.width" :align="field.align"
                :headerAlign="field.headerAlign || field.align || 'center'" :sortable="field.sortable">
                <template #default="{ row, $index }">
                    <EditTableColumn :row="row" :index="$index" :field="field" :readonly="readonly || row.readonly" :fields="fields"
                        :editable="editable" :mode="mode" />
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

const props = defineProps(["fields", "defRow", "readonly", "showNew", "operationsWidth", "rowKey", "showOperations", "border",
    "mode", 'withDelete', 'showIndex', 'editable', 'showSelection', "height", "newIdx", "selectable", "showSortButtons"])
const data = defineModel()
const emits = defineEmits(["delete", "selectionChange"])
const key = ref(uuid.v4())
const tableRef = ref()

// 新增行
function newRow() {
    const row = _.cloneDeep(props.defRow || {})
    row.id = uuid.v4()
    if (!data.value) {
        data.value = []
    }

    if (props.newIdx) {
        data.value.splice(props.newIdx, 0, row)
    } else {
        data.value.push(row)
    }
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

// 获取选择的记录
function getSelection() {
    // 返回的顺序是选择的顺序，而不是表中记录的顺序，因此需要处理
    let rows = tableRef.value.getSelectionRows()

    if (!rows || !rows.length) {
        return rows
    }

    let ids = rows.map(item => item[props.rowKey || 'id'])
    return data.value.filter(item => ids.includes(item[props.rowKey || 'id']))
}

function selectionChange(val) {
    emits('selectionChange', val)
}

function getData() {
    return data.value
}

function toTop() {
    let rows = data.value
    let selectedRows = getSelection()

    // 从最后一个往上，都放第1个位置 
    selectedRows.reverse().forEach(row => {
        let idx = rows.indexOf(row)
        rows.splice(idx, 1)
        rows.splice(0, 0, row)
    })
}

function up() {
    let rows = data.value
    let selectedRows = getSelection()

    // 选择的行可能不连续，需要从第一个开始上移
    let lastMoved = false
    let lastIdx = 0

    selectedRows.forEach(row => {
        let idx = rows.indexOf(row)

        if (idx <= 0) {
            lastMoved = false
            lastIdx = idx
            return;
        }

        // 如果上一个没移动，而且现在的这行与上一行是连续的，也不进行移动 
        if (lastIdx && !lastMoved && lastIdx == idx - 1) {
            lastIdx = idx
            return
        }
        lastIdx = idx
        lastMoved = true

        rows.splice(idx, 1)
        rows.splice(idx - 1, 0, row)
    })
}

function down() {
    let rows = data.value
    let selectedRows = getSelection()
    let length = rows.length

    // 选择的行可能不连续，需要从最后一个往上处理
    let lastMoved = false
    let lastIdx = 0

    selectedRows.reverse().forEach(row => {
        let idx = rows.indexOf(row)

        if (idx == length - 1) {
            lastIdx = idx
            lastMoved = false
            return;
        }
        if (lastIdx && !lastMoved && lastIdx == idx + 1) {
            lastIdx = idx
            return
        }

        lastIdx = idx
        lastMoved = true

        rows.splice(idx, 1)
        rows.splice(idx + 1, 0, row)
    })
}

function toBottom() {
    let rows = data.value
    let selectedRows = getSelection()
    let lastIdx = rows.length -1

    // 从第一个往下，均放在最后一个 
    selectedRows.forEach(row => {
        let idx = rows.indexOf(row)
        rows.splice(idx, 1)
        rows.splice(lastIdx, 0, row)
    })
}

defineExpose({ newRow, getSelection, getData })
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