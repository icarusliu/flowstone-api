<template>
    <div class="buttons mb-2" v-if="!disabled">
        <el-button type="primary" @click="showNew()">新增</el-button>
    </div>
    <data-table :fields="getColumns()" v-model="model"></data-table>

    <!-- 子表格新增的情况 -->
    <el-drawer v-model="newVisible" title="新增" width="300px">
        <base-form :fields="drawerField.fields" v-model="drawerModel" label-position="top" ref="tableBaseForm"></base-form>
        <template #footer>
            <div class="buttons">
                <el-button type="primary" @click="doSaveField">保存</el-button>
                <el-button @click="newVisible = false">取消</el-button>
            </div>
        </template>
    </el-drawer>
</template>

<script setup>
import DataTable from '../base-table/data-table.vue'
import * as _ from 'lodash'

const props = defineProps(['disabled', 'fields', 'field'])
const model = defineModel()
const emits = defineEmits(["change"])

// 获取字段对应的表格字段列表
function getColumns() {
    let field = props.field
    const fields = field.fields;
    if (!fields || !fields.length) {
        return []
    }

    // 增加操作列（编辑与删除按钮）
    let lastColumn = fields[fields.length - 1]
    if (lastColumn.type != 'operations') {
        lastColumn = {
            label: '操作',
            type: 'operations',
            width: field.operationsWidth,
            buttons: []
        }
        fields.push(lastColumn);
    }

    let buttons = lastColumn.buttons;
    if (!buttons) {
        buttons = []
        lastColumn.buttons = buttons
    }

    let hasDelete = false, hasEdit = false
    buttons.forEach(button => {
        if (button.label == '删除') {
            hasDelete = true
        } else if (button.label == '编辑') {
            hasEdit = true
        }
    })

    if (!hasEdit) {
        buttons.push({
            label: '编辑',
            type: 'primary',
            action: (f, idx) => goEditField(field, f, idx)
        })
    }

    if (!hasDelete) {
        buttons.push({
            type: 'warning',
            label: '删除',
            action: (f, idx) => deleteField(field, f, idx)
        })
    }

    return fields;
}

// 当前编辑中的行
let editingRowIdx = -1
const newVisible = ref(false);
const drawerField = ref({})
const drawerModel = ref({})
const tableBaseForm = ref()

// 展示新增抽屉
function showNew() {
    let field = props.field
    drawerField.value = field
    newVisible.value = true
    editingRowIdx = -1
    drawerModel.value = {}
}

// 保存抽屉数据到主对象中
function doSaveField() {
    tableBaseForm.value.validate(resp => {
        if (!resp) {
            return;
        }

        const field = drawerField.value.prop
        let rows = model.value
        if (!rows) {
            rows = model.value = []
        }

        if (editingRowIdx != -1) {
            rows.splice(editingRowIdx, 1, drawerModel.value)
        } else {
            rows.push(drawerModel.value)
        }

        emits('change', rows)

        newVisible.value = false
    });
}

// 删除列表字段中的某一行
function deleteField(field, row, idx) {
    model.value.splice(idx, 1)
}

// 编辑列表字段中的某一行 
function goEditField(field, row, idx) {
    drawerField.value = field
    drawerModel.value = _.cloneDeep(row)
    editingRowIdx = idx
    newVisible.value = true
}
</script>