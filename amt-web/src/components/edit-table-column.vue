<!-- 可编辑表格列的内容实现 -->
<template>
    <div class="cell-content" :class="field.class">
        <div class="cell-main" v-if="!field.show || field.show(row)" :class="field.mainClass">
            <!-- 脚本编辑 -->
            <el-link type="primary" v-if="field.script && field.script(row) != ''" @click="showEditScript" class="mr-2">
                编辑
            </el-link>

            <span v-else-if="field.type == 'text'">{{ row[field.prop] }}</span>

            <!-- 其它编辑组件 -->
            <component v-else :is="is" v-model="row[field.prop]" :disabled="getDisabled(field, row)" autofocus
                :options="field.options" :placeholder="field.placeholder" @change="doChange(field, $event, row, index)"
                valueFormat="YYYY-MM-DD">
            </component>
        </div>

        <!-- 一个格式中放两个字段的情况 -->
        <div class="cell-sub" :class="field.subClass" v-if="field.sub && (!field.sub.show || field.sub.show(row))">
            <edit-table-column :field="field.sub" :row="row" :readonly="readonly" :index="index" />
        </div>
    </div>

    <!-- 脚本编辑器 -->
    <ScriptDialog :lang="scriptType" v-model:visible="visible" v-model="row[field.prop]" :readonly="readonly">
    </ScriptDialog>
</template>

<script setup>
import baseSelect from '@/components/base-select.vue'
import BaseAutoComplete from '@/components/base-autocomplete.vue'
import ScriptDialog from './script-dialog.vue'
import { ElDatePicker } from 'element-plus';

const props = defineProps(["field", "row", "readonly", "index"])
const scriptType = ref('javascript')
const visible = ref(false)
const is = computed(() => {
    let type = props.field.type
    return getIs(type, props.row)
})

// 获取组件
function getIs(type, row) {
    if (type instanceof Function) {
        type = type(row)
    }

    if (type == 'select') {
        return baseSelect
    } else if (type == 'autocomplete') {
        return BaseAutoComplete
    } else if (type == 'datepicker') {
        return ElDatePicker
    } else if (type == 'checkbox') {
        return 'el-checkbox'
    } else {
        return 'el-input'
    }
}

// 显示脚本编辑器
function showEditScript() {
    scriptType.value = props.field.script(props.row)
    visible.value = true
}

function getDisabled(field, row) {
    return props.readonly || (field.disabled && field.disabled(row[field.prop], row))
}

// 值改变
function doChange(field, val, row, idx) {
    if (!field.change) {
        return
    }

    field.change(val, row, { fields: props.fields, idx })
}

</script>

<style lang="scss" scoped>
:deep() {
    .el-date-editor {
        --el-date-editor-width: 160px;
    }
}
</style>