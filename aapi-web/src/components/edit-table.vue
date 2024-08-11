<template>
    <!-- 可编辑表格 -->
    <div class="mb-2">
        <el-button type="primary" @click="newRow" :disabled="readonly" v-if="showNew != false">新增</el-button>
    </div>
    <el-table :data="data" class="table">
        <el-table-column v-for="field in fields" :key="field.prop" :label="field.label" :width="field.width">
            <template #default="{ row }">
                <!-- 脚本编辑 -->
                <el-link type="primary" v-if="field.typeSupplier && field.typeSupplier(row) != ''"
                    @click="showEditScript(field, row, field.typeSupplier(row))">
                    编辑
                </el-link>

                <!-- 下拉 -->
                <base-select v-else-if="field.type == 'select'" v-model="row[field.prop]"
                    :disabled="getDisabled(field, row)" :options="field.options" @change="doChange(field, $event, row)">
                </base-select>

                <!-- 输入 -->
                <el-input v-else v-model="row[field.prop]" :disabled="getDisabled(field, row)"
                    @change="doChange(field, $event, row)" />
            </template>
        </el-table-column>

        <el-table-column label="" width="50px">
            <template #default="{ $index }">
                <ElLink type="danger" @click="doDelete($index)" :disabled="readonly">删除</ElLink>
            </template>
        </el-table-column>
    </el-table>

    <el-dialog v-model="visible" title="脚本编辑">
        <monacoEditor height="400px" v-model="script" :language="scriptType" />
        <template #footer>
            <div>
                <el-link type="primary" class="mr-4" @click="visible = false">取消</el-link>
                <el-button type="primary" @click="doSaveScript" :disabled="readonly">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import baseSelect from '@/components/base-select.vue'
import { ElLink } from 'element-plus';
import * as _ from 'lodash'
import monacoEditor from './monaco-editor.vue'

const props = defineProps(["fields", "defRow", "readonly", "showNew"])
const data = defineModel()
const visible = ref(false)

const scriptField = ref()
const scriptRow = ref()
const script = ref()
const scriptType = ref('javascript')

function getDisabled(field, row) {
    return props.readonly || (field.disabled && field.disabled(row[field.prop], row))
}

// 新增行
function newRow() {
    data.value.push(_.cloneDeep(props.defRow))
}

// 值改变
function doChange(field, val, row) {
    if (!field.change) {
        return
    }

    field.change(val, row, props.fields)
}

// 删除行
function doDelete(idx) {
    data.value.splice(idx, 1)
}

// 显示脚本编辑器
function showEditScript(field, row, lang) {
    script.value = row[field.prop]

    // 增加默认脚本 
    if (!script.value) {
        if (lang == 'js') {
            script.value = `function func(params) {
    return {
        a: 1
    }
}`
        } else if (lang == 'groovy') {
            script.value = `def fun(params) {
    return [
        a: 1
    ]
}`
        } else if (lang == 'json') {
            script.value = '{}'
        }
    }

    scriptType.value = lang == 'js' ? 'javascript' : lang
    visible.value = true
    scriptField.value = field
    scriptRow.value = row
}

// 保存脚本
function doSaveScript() {
    scriptRow.value[scriptField.value.prop] = script.value
    visible.value = false
}

defineExpose({ newRow })
</script>

<style lang="scss" scoped>
.table {
    :deep() {

        .el-select__wrapper,
        .el-input__wrapper {
            font-size: 12px;
            padding: 0 4px;
        }

        .cell {
            padding: 0 6px 0 2px;
        }
    }

}
</style>