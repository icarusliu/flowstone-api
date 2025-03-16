<template>
    <!-- 下拉 -->
    <base-select v-if="field.type == 'select'" v-model="model" :value-key="field.valueKey" :disabled="disabled"
        :multiple="field.multiple" @change="onFieldChange" :options="field.options">
    </base-select>

    <!-- 树形下拉 -->
    <base-tree-select v-else-if="field.type == 'tree-select'" v-model="model" :disabled="disabled" @change="onFieldChange"
        :options="field.options">
    </base-tree-select>

    <!-- 数字输入 -->
    <template v-else-if="field.type == 'number'">
        <el-input-number v-model="model" :step="field.step || 1" :precision="field.precision" :max="field.max"
            :disabled="disabled" @change="onFieldChange">
        </el-input-number>
        <span class="ml-2">{{ field.unit }}</span>
    </template>

    <!-- 单选 -->
    <el-radio-group v-else-if="field.type == 'radioGroup'" v-model="model" :disabled="disabled" @change="onFieldChange">
        <el-radio-button v-for="option in field.options" :label="option.label" :value="option.value"></el-radio-button>
    </el-radio-group>

    <!-- 复选 -->
    <el-checkbox v-else-if="field.type == 'checkbox'" v-model="model" :disabled="disabled" @change="onFieldChange">
    </el-checkbox>

    <!-- 子表格 -->
    <TableField v-else-if="field.type == 'table'" v-model="model" :field="field" :disabled="disabled" @change="onFieldChange"/>

    <!-- 日期选择 -->
    <template v-else-if="field.type == 'datePicker'">
        <el-date-picker v-model="model" @change="onFieldChange" :format="field.format" :value-format="field.format"
            :type="field.dateType"></el-date-picker>
    </template>

    <template v-else-if="field.type == 'timePicker'">
        <el-time-picker v-model="model" @change="onFieldChange" :format="field.format" :value-format="field.format"
            :type="field.dateType"></el-time-picker>
    </template>

    <!-- 图标选择 -->
    <icon-selector v-else-if="field.type == 'iconSelector'" v-model="model"></icon-selector>

    <!-- 子可编辑表格 -->
    <template v-else-if="field.type == 'editTable'">
        <edit-table :fields="field.fields" v-model="model" :defRow="field.default" :readonly="readonly"></edit-table>
    </template>

    <!-- testarea -->
    <el-input v-else-if="field.type == 'textarea'" type="textarea" v-model="model" :disabled="disabled"
        @change="onFieldChange">
    </el-input>

    <!-- Cron表达式编辑 -->
    <cron-editor v-else-if="field.type == 'cron'" v-model="model" :disabled="disabled" @change="onFieldChange" />

    <!-- 其它情况 -->
    <el-input v-else v-model="model" :disabled="disabled" @change="onFieldChange" ref="inputRef" :placeholder="field.placeholder || '请输入' + field.label">
        <template #prepend v-if="field.prepend">
            {{ field.prepend }}
        </template>
    </el-input>
</template>

<script setup>
import BaseSelect from '../base-select.vue'
import baseTreeSelect from '../base-tree-select.vue'
import CronEditor from '../cron-editor.vue';
import EditTable from '../edit-table/index.vue'
import iconSelector from '../icon-selector.vue';
import TableField from './table-field.vue'
import * as _ from 'lodash'

const props = defineProps(['field', 'fields', 'form', 'readonly'])

const disabled = computed(() => {
    let editable = props.field.editable 
    if (editable == false || props.readonly) {
        return true
    }

    if (_.isFunction(editable)) {
        return editable(model.value, props.form)
    }

    return false
})

const model = computed({
    get() {
        return props.form[props.field.prop]
    },
    set(val) {
        props.form[props.field.prop] = val
    }
})
const inputRef = ref()

onMounted(() => {
    if (props.field.autofocus) {
        nextTick(() => {
            inputRef.value && inputRef.value.focus()
        })
    }
})

// 字段值变化事件 
function onFieldChange(val, item) {
    let field = props.field
    if (field.change) {
        field.change(val, props.form, {
            fields: props.fields,
            item
        })
    }
}
</script>