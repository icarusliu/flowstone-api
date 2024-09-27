<!-- 基础新增或编辑表单 -->

<template>
    <div>
        <el-form :model="form" :label-position="labelPosition" :label-width="labelWidth" class="base-form" ref="formRef"
            :validate-on-rule-change="false" :rules="rules">
            <template v-for="field,index in fields">
                <el-form-item :label="field.label" :required="field.required"
                    v-if="field.type != 'operations' && showField(field)" :prop="field.prop">
                    <!-- 下拉 -->
                    <base-select v-if="field.type == 'select'" v-model="form[field.prop]" :value-key="field.valueKey"
                        :disabled="field.editable == false || readonly"
                        @change="(val, item) => onFieldChange(field, val, item)" :options="field.options">
                    </base-select>

                    <!-- 树形下拉 -->
                    <base-tree-select v-else-if="field.type == 'tree-select'" v-model="form[field.prop]"
                        :disabled="field.editable == false || readonly" @change="val => onFieldChange(field, val)"
                        :options="field.options">
                    </base-tree-select>

                    <!-- 数字输入 -->
                    <el-input-number v-else-if="field.type == 'number'" v-model="form[field.prop]"
                        :disabled="field.editable == false || readonly" @change="val => onFieldChange(field, val)">
                    </el-input-number>

                    <!-- 单选 -->
                    <el-radio-group v-else-if="field.type == 'radioGroup'" v-model="form[field.prop]"
                        :disabled="field.editable == false || readonly" @change="val => onFieldChange(field, val)">
                        <el-radio-button v-for="option in field.options" :label="option.label"
                            :value="option.value"></el-radio-button>
                    </el-radio-group>

                    <!-- 复选 -->
                    <el-checkbox v-else-if="field.type == 'checkbox'" v-model="form[field.prop]"
                        :disabled="field.editable == false || readonly" @change="val => onFieldChange(field, val)">
                    </el-checkbox>

                    <!-- 子表格 -->
                    <template v-else-if="field.type == 'table'">
                        <div class="buttons mb-2" v-if="field.editable == false || !readonly">
                            <el-button type="primary" @click="showNew(field)">新增</el-button>
                        </div>
                        <data-table :fields="getColumns(field)" v-model="form[field.prop]"></data-table>
                    </template>

                    <!-- 子可编辑表格 -->
                    <template v-else-if="field.type == 'editTable'">
                        <edit-table :fields="field.fields" v-model="form[field.prop]" :defRow="field.default"></edit-table>
                    </template>

                    <!-- testarea -->
                    <el-input v-else-if="field.type == 'textarea'" type="textarea"
                        v-model="form[field.prop]" :disabled="field.editable == false || readonly"
                        @change="val => onFieldChange(field, val)">
                    </el-input>

                    <!-- Cron表达式编辑 -->
                    <cron-editor v-else-if="field.type == 'cron'" v-model="form[field.prop]"
                        :disabled="field.editable == false || readonly" @change="val => onFieldChange(field, val)" />

                    <!-- 其它情况 -->
                    <el-input v-else v-model="form[field.prop]" :disabled="field.editable == false || readonly"
                        @change="val => onFieldChange(field, val)">
                        <template #prepend v-if="field.prepend">
                            {{ field.prepend }}
                        </template>
                    </el-input>
                </el-form-item>
            </template>
        </el-form>

        <!-- 子表格新增的情况 -->
        <el-drawer v-model="newVisible" title="新增" width="300px">
            <base-form :fields="drawerField.fields" v-model="drawerModel" label-position="top"
                ref="tableBaseForm"></base-form>
            <template #footer>
                <div class="buttons">
                    <el-button type="primary" @click="doSaveField">保存</el-button>
                    <el-button @click="newVisible = false">取消</el-button>
                </div>
            </template>
        </el-drawer>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import * as _ from 'lodash';
import BaseSelect from './base-select.vue'
import baseTreeSelect from './base-tree-select.vue'
import CronEditor from './cron-editor.vue';
import EditTable from './edit-table.vue'
import DataTable from './data-table.vue'

const form = defineModel();
const rules = ref({});
const formRef = ref();
const tableBaseForm = ref()
const props = defineProps({
    // 字段列表
    fields: Array,
    labelWidth: {
        type: String,
        default: '100px'
    },
    labelPosition: {
        type: String,
        default: 'right'
    },
    readonly: {
        type: Boolean,
        default: false
    }
})
const newVisible = ref(false);
const drawerField = ref({})
const drawerModel = ref({})

onMounted(() => {
    // 加载校验规则
    loadRules()

    if (_.isEmpty(form.value)) {
        form.value = getFormDefaultModel(props.fields)
    }
})

// 当前编辑中的行
let editingRowIdx = -1

// 展示新增抽屉
function showNew(field) {
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

        console.log(form.value)

        const field = drawerField.value.prop
        let rows = form.value[field]
        if (!rows) {
            rows = form.value[field] = []
        }

        if (editingRowIdx != -1) {
            rows.splice(editingRowIdx, 1, drawerModel.value)
        } else {
            rows.push(drawerModel.value)
        }

        newVisible.value = false
    });
}

// 获取字段对应的表格字段列表
function getColumns(field) {
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

// 删除列表字段中的某一行
function deleteField(field, row, idx) {
    form.value[field.prop].splice(idx, 1)
}

// 编辑列表字段中的某一行 
function goEditField(field, row, idx) {
    drawerField.value = field
    drawerModel.value = _.cloneDeep(row)
    editingRowIdx = idx
    newVisible.value = true
}

// 获取默认值
function getFormDefaultModel(fields) {
    let obj = {}
    fields.forEach(field => {
        if (field.default || field.default == 0) {
            obj[field.prop] = field.default
        } else if (field.type == 'table') {
            obj[field.prop] = []
        }
    })

    return obj;
}

// 字段值变化事件 
function onFieldChange(field, val, item) {
    if (field.change) {
        field.change(val, form.value, {
            fields: props.fields,
            item
        })
    }
}

function validate(func) {
    return formRef.value.validate(func)
}

// 加载校验规则
function loadRules() {
    if (!props.fields) {
        return;
    }

    props.fields.forEach(field => {
        let fieldRules = []
        if (field.required) {
            fieldRules.push({ required: true, message: field.label + '不能为空' })
        }

        const validation = field.validation
        if (validation?.validator) {
            const validator = validation.validator;
            const type = validation.type;
            const msg = validation.msg;

            // 可能是校验函数或者正则表达式或者内置校验
            if (type == 'func' || _.isFunction(validator)) {
                fieldRules.push({
                    validator
                })
            } else if (type == 'regex') {
                let rule = {
                    validator: (rule, val, callback) => {
                        console.log(validator)
                        if (!val) {
                            callback()
                            return
                        }

                        if (!val.match(validator)) {
                            callback(new Error(msg || '校验失败'))
                            return;
                        }

                        callback();
                    }
                }

                // 正则校验
                fieldRules.push(rule)
            }
        }

        if (field.maxLength) {
            fieldRules.push({
                max: field.maxLength,
                message: field.label + '长度不能超过' + field.maxLength
            })
        }

        if (field.max || field.max == 0) {
            fieldRules.push({
                max: field.max,
                message: field.label + '不能超过' + field.max
            })
        }


        rules.value[field.prop] = fieldRules;
    })
}

function showField(field) {
    if (field.display == false) {
        return false
    }

    if (!field.display) {
        return true
    }

    if (_.isFunction(field.display)) {
        return field.display(form.value)
    }

    return field.display
}

defineExpose({
    validate
})
</script>

<style lang="scss" scoped>
.base-form {
    :deep() {
        label {
            align-items: flex-start;
        }
    }
}
</style>