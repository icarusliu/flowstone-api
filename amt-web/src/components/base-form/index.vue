<!-- 基础新增或编辑表单 -->
<template>
    <el-form :model="form" :label-position="labelPosition" :label-width="labelWidth" class="base-form" ref="formRef"
        :validate-on-rule-change="false" :rules="rules" :inline="inline">
        <el-row :gutter="24">
            <template v-for="field, index in fields">
                <div class="group mb-2" v-if="field.type == 'group'">
                    <div class="title">{{ field.label }}</div>
                    <el-col :span="24">
                        <el-row :gutter="24" class="p-4">
                            <el-col v-for="subField in field.fields" :span="subField.col || 24">
                                <el-form-item :label="subField.label" :required="subField.required"
                                    v-if="subField.type != 'operations' && showField(subField)" :prop="subField.prop">
                                    <BaseFormField :field="subField" :form="form" :fields="fields" :readonly="readonly"
                                        :showPlaceholder="showPlaceholder" />
                                    <div v-if="subField.remark" class="remark" v-html="subField.remark">
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-col>
                </div>

                <el-col :span="field.col || 24" v-else>
                    <el-form-item :label="field.label" :required="field.required" v-if="field.type != 'operations' && showField(field)"
                        :prop="field.prop">
                        <BaseFormField :field="field" :form="form" :fields="fields" :readonly="readonly"
                            :showPlaceholder="showPlaceholder" />
                        <div v-if="field.remark" class="remark" v-html="field.remark">
                        </div>
                    </el-form-item>
                </el-col>
            </template>

            <slot :form="form">
            </slot>
        </el-row>
    </el-form>
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import * as _ from 'lodash';
    import BaseFormField from './field.vue';

    const form = defineModel();
    const rules = ref({});
    const formRef = ref();

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
        },
        inline: {
            type: Boolean,
            default: false
        },
        showPlaceholder: {
            default: true
        }
    })

    onMounted(() => {
        // 加载校验规则
        loadRules()
        if (_.isEmpty(form.value)) {
            form.value = getFormDefaultModel(props.fields)
        }
    })


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

    function validate(func) {
        return formRef.value.validate(func)
    }

    // 加载校验规则
    function loadRules() {
        if (!props.fields) {
            return;
        }

        props.fields.forEach(field => {
            loadFieldRule(field)

            if (field.fields) {
                field.fields.forEach(subField => loadFieldRule(subField))
            }
        })
    }

    function loadFieldRule(field) {
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
                    trigger: 'blur',
                    validator: (rule, val, callback) => validator(rule, val, callback, form.value)
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
                type: 'nubmer',
                max: field.max,
                message: field.label + '不能超过' + field.max
            })
        }


        rules.value[field.prop] = fieldRules;
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

        .group {
            border: 1px solid #f1f1f1;
            flex: auto;

            .title {
                font-weight: bold;
                font-size: 15px;
                line-height: 40px;
                background: #f8f8fa;
                padding: 0 8px;
                margin-bottom: 12px;
                border-bottom: 1px solid #f1f1f1;
            }
        }
    }
</style>