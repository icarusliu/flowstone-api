<!-- 可编辑表格列的内容实现 -->
<template>
    <BaseTableColumn v-if="!editing" :row="row" :field="field" />

    <div v-else class="cell-content" :class="field.class">
        <div class="cell-main" v-if="!field.show || field.show(row)" :class="field.mainClass">
            <!-- 脚本编辑 -->
            <el-link type="primary" v-if="field.script && field.script(row)" @click="showEditScript" class="mr-2">
                {{ readonly ? '查看' : '编辑' }}
            </el-link>

            <el-link type="primary" v-else-if="field.type == 'selectScript'" @click="showSelectEditScript" class="mr-2"
                :disabled="getDisabled(field, row)">
                {{ getDisabled(field, row) ? '--' : readonly ? '查看' : '编辑' }}
            </el-link>

            <span v-else-if="field.converter">{{ field.converter(model, row) }}</span>
            <span v-else-if="field.type == 'text'">{{ model }}</span>

            <BaseRender v-else-if="field.render" :content="field.render(model, row)"></BaseRender>

            <!-- 其它编辑组件 -->
            <component v-else :is="is" v-model="model" :disabled="readonly || getDisabled(field, row)" autofocus :options="field.options"
                :placeholder="placeholder" @change="doChange(field, $event, row, index)" valueFormat="YYYY-MM-DD">
            </component>
        </div>

        <!-- 一个格式中放两个字段的情况 -->
        <div class="cell-sub" :class="field.subClass" v-if="field.sub && (!field.sub.show || field.sub.show(row))">
            <edit-table-column :field="field.sub" :row="row" :readonly="readonly || getDisabled(field, row)" :index="index" />
        </div>
    </div>

    <!-- 脚本编辑器 -->
    <ScriptDialog :lang="scriptType" v-model:visible="visible" v-model="model" :readonly="readonly || getDisabled(field, row)"
        v-if="field.script">
    </ScriptDialog>
    <SelectScriptDialog v-model:visible="selectScriptVisible" v-model="model" :readonly="readonly || getDisabled(field, row)"
        :defJs="field.defJs" :defGroovy="field.defGroovy" />
</template>

<script setup>
    import baseSelect from '../base-select.vue'
    import baseRadioGroup from "../base-radio-group.vue"
    import BaseAutoComplete from '../base-autocomplete.vue'
    import ScriptDialog from '../script-dialog.vue'
    import BaseTableColumn from '../base-table/base-table-column.vue'
    import BaseRender from '../base-render'
    import * as _ from 'lodash'
    import SelectScriptDialog from '../select-script-dialog.vue'

    const props = defineProps(["field", "row", "readonly", "index", "fields", "editable", "mode"])
    const scriptType = ref('javascript')
    const visible = ref(false)
    const selectScriptVisible = ref(false);
    const editing = computed(() => {
        if (props.editable == false || props.field.editable == false) {
            return false
        }

        if (props.field.type == 'checkbox') {
            return true
        }

        if (props.mode == 'click') {
            return props.row.editing
        }

        return true
    })
    const is = computed(() => {
        let type = props.field?.type
        return getIs(type, props.row)
    })
    const model = computed({
        get() {
            return _.get(props.row, props.field.prop)
        },
        set(val) {
            _.set(props.row, props.field.prop, val)
        }
    })
    const placeholder = computed(() => {
        let p = props.field.placeholder
        if (!p) {
            return
        }

        if (_.isFunction(p)) {
            return p(props.row)
        } else {
            return p
        }
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
            return 'el-date-picker'
        } else if (type == 'checkbox') {
            return 'el-checkbox'
        } else if (type == 'radioGroup') {
            return baseRadioGroup
        } else {
            return 'el-input'
        }
    }

    // 显示脚本编辑器
    function showEditScript() {
        scriptType.value = props.field.script(props.row)
        visible.value = true
    }

    function showSelectEditScript() {
        selectScriptVisible.value = true
    }

    function getDisabled(field, row) {
        let disabled = field.disabled
        if (_.isFunction(disabled)) {
            return field.disabled(row[field.prop], row)
        }

        return field.disabled
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

        .el-checkbox {
            align-items: center !important;
        }
    }

    .cell-main {
        vertical-align: center;
    }
</style>