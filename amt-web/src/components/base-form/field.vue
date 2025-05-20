<template>
    <!-- 下拉 -->
    <base-select
        v-if="field.type == 'select'"
        v-model="model"
        :value-key="field.valueKey"
        :disabled="disabled"
        :multiple="field.multiple"
        :filterable="field.filterable"
        @change="onFieldChange"
        :options="field.options"
    >
    </base-select>

    <!-- 标签 -->
    <el-input-tag v-else-if="field.type == 'inputTag'" v-model="model" @change="onFieldChange" :disabled="disabled"></el-input-tag>

    <imageUploader
        v-else-if="field.type == 'imageUploader'"
        v-model="model"
        @change="onFieldChange"
        :disabled="disabled"
        :multiple="field.multiple"
        :limit="field.limit"
    />

    <fileUploader
        v-else-if="field.type == 'fileUploader'"
        v-model="model"
        @change="onFieldChange"
        :disabled="disabled"
        :accept="field.accept"
        :multiple="field.multiple"
        :limit="field.limit"
    />

    <richText v-else-if="field.type == 'richText'" v-model="model" :disabled="disabled" :height="field.height" @change="onFieldChange"> </richText>

    <dictSelect v-else-if="field.dict" :dict="field.dict" v-model="model" :disabled="disabled" @change="onFieldChange" :dataType="field.dataType"></dictSelect>

    <!-- 树形下拉 -->
    <base-tree-select
        v-else-if="field.type == 'tree-select' || field.type == 'treeSelect'"
        v-model="model"
        :disabled="disabled"
        @change="onFieldChange"
        :options="field.options"
    >
    </base-tree-select>

    <!-- 数字输入 -->
    <template v-else-if="field.type == 'number'">
        <el-input-number
            v-model="model"
            :step="field.step || 1"
            :precision="field.precision"
            :max="field.max"
            :disabled="disabled"
            :min="field.min"
            @change="onFieldChange"
        >
        </el-input-number>
        <span class="ml-2">{{ field.unit }}</span>
    </template>

    <!-- 单选 -->
    <el-radio-group v-else-if="field.type == 'radioGroup'" v-model="model" :disabled="disabled" @change="onFieldChange">
        <el-radio-button v-for="option in field.options" :label="option.label" :value="option.value"></el-radio-button>
    </el-radio-group>

    <!-- 复选 -->
    <base-checkbox v-else-if="field.type == 'checkbox'" v-model="model" :options="field.options" :disabled="disabled" @change="onFieldChange" />

    <el-switch v-else-if="field.type == 'switch'" v-model="model" :disabled="disabled" @change="onFieldChange"></el-switch>

    <!-- 子表格 -->
    <TableField v-else-if="field.type == 'table'" v-model="model" :field="field" :disabled="disabled" @change="onFieldChange" />

    <!-- 日期选择 -->
    <template v-else-if="field.type == 'datePicker'">
        <BaseDatePicker
            v-model="model"
            v-model:endDate="form[field.prop1]"
            :type="field.dateType"
            :format="field.format"
            :disabled="disabled"
            @change="onFieldChange"
            :disabledDate="field.disabledDate"
        />
    </template>

    <template v-else-if="field.type == 'timePicker'">
        <el-time-picker v-model="model" @change="onFieldChange" :format="field.format" :value-format="field.format" :type="field.dateType"></el-time-picker>
    </template>

    <!-- 图标选择 -->
    <icon-selector v-else-if="field.type == 'iconSelector'" v-model="model"></icon-selector>

    <!-- 子可编辑表格 -->
    <template v-else-if="field.type == 'editTable'">
        <edit-table
            :fields="field.fields"
            v-model="model"
            :defRow="field.default"
            :readonly="readonly"
            :showIndex="field.showIndex"
            :mode="field.mode"
            :height="field.height"
            :showNew="field.showNew"
            :withDelete="field.withDelete"
        >
            <template #appendButtons>
                <el-button v-for="button in field.buttons" @click="button.action()" :type="button.type" :disabled="disabled">{{ button.label }}</el-button>
            </template>
        </edit-table>
    </template>

    <!-- testarea -->
    <el-input
        v-else-if="field.type == 'textarea'"
        type="textarea"
        v-model="model"
        :disabled="disabled"
        @change="onFieldChange"
        :placeholder="placeholder"
        :rows="field.rows"
    >
    </el-input>

    <!-- Cron表达式编辑 -->
    <cron-editor v-else-if="field.type == 'cron'" v-model="model" :disabled="disabled" @change="onFieldChange" />

    <!-- 其它情况 -->
    <el-input
        v-else
        v-model="model"
        :disabled="disabled"
        @change="onFieldChange"
        ref="inputRef"
        :placeholder="disabled ? '' : placeholder"
        :type="field.inputType"
        :showPassword="field.inputType == 'password'"
    >
        <template #prepend v-if="field.prepend">
            {{ field.prepend }}
        </template>
        <template #append v-if="field.append">{{ field.append }}</template>
    </el-input>
</template>

<script setup>
import BaseSelect from "../base-select.vue";
import baseCheckbox from "../base-checkbox.vue";
import baseTreeSelect from "../base-tree-select.vue";
import CronEditor from "../cron-editor.vue";
import EditTable from "../edit-table/index.vue";
import iconSelector from "../icon-selector.vue";
import TableField from "./table-field.vue";
import dictSelect from "../dict-select.vue";
import imageUploader from "../image-uploader.vue";
import richText from "../rich-text.vue";
import BaseDatePicker from "../base-date-picker.vue";
import * as _ from "lodash";
import fileUploader from "../file-uploader.vue";

const props = defineProps(["field", "fields", "form", "readonly", "showPlaceholder"]);

const disabled = computed(() => {
    let editable = props.field.editable;
    if (editable == false || props.readonly) {
        return true;
    }

    if (_.isFunction(editable)) {
        return editable(model.value, props.form);
    }

    return false;
});
const placeholder = computed(() => {
    if (!props.showPlaceholder) {
        return;
    }

    return props.field.placeholder || "请输入" + props.field.label;
});

const model = computed({
    get() {
        return _.get(props.form, props.field.prop);
    },
    set(val) {
        _.set(props.form, props.field.prop, val);
    },
});
const inputRef = ref();

onMounted(() => {
    if (props.field.autofocus) {
        nextTick(() => {
            inputRef.value && inputRef.value.focus();
        });
    }
});

// 字段值变化事件
function onFieldChange(val, item) {
    let field = props.field;
    if (field.change) {
        field.change(val, props.form, {
            fields: props.fields,
            item,
        });
    }
}
</script>
