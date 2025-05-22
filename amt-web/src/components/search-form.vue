<template>
    <el-form v-model="model" inline>
        <el-form-item v-for="field in fields" :label="field.label" :prop="field.prop">
            <base-select v-if="field.type == 'select'" v-model="model[field.prop]" :placeholder="field.placeholder" clearable :options="field.options">
            </base-select>

            <dict-select v-else-if="field.dict" v-model="model[field.prop]" :dict="field.dict" :withDefault="false"></dict-select>

            <base-date-picker
                v-else-if="field.type == 'datePicker'"
                v-model="model[field.prop]"
                v-model:endDate="model[field.prop1]"
                :type="field.dateType"
                :format="field.format"
                :clearable="field.clearable"
            />

            <!-- slot -->
            <base-render v-else-if="field.type == 'render'" :content="field.render(model[field.prop], model)"></base-render>

            <el-input v-else v-model="model[field.prop]" :placeholder="field.placeholder || ('请输入' + field.label)" clearable></el-input>
        </el-form-item>

        <div class="buttons">
            <el-button type="primary" @click="doQuery" icon="search">查询</el-button>
            <el-button @click="doReset" icon="Refresh">重置</el-button>
        </div>
    </el-form>
</template>

<script setup>
import BaseSelect from "@/components/base-select.vue";
import BaseRender from "./base-render.js";
import DictSelect from "@/components/dict-select.vue";
import baseDatePicker from "./base-date-picker.vue";
import * as _ from "lodash";

const props = defineProps(["fields", "defParams"]);
const model = defineModel();
const emits = defineEmits(["query"]);

function doReset() {
    if (props.defParams) {
        model.value = _.cloneDeep(props.defParams);
    } else {
        model.value = {};
    }

    doQuery();
}

function doQuery() {
    emits("query", model.value);
}
</script>

<style lang="scss" scoped>
:deep() {
    .el-form-item__content {
        min-width: 240px;
    }

    .el-date-editor--daterange {
        width: 260px;
    }
}

.buttons {
    margin-bottom: 18px;
    display: inline-flex;
    vertical-align: middle;
}
</style>
