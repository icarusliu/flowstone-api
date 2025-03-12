<template>
    <el-form v-model="model" inline>
        <el-form-item v-for="field in fields" :label="field.label" :prop="field.prop">
            <base-select v-if="field.type == 'select'" v-model="model[field.prop]" :placeholder="field.placeholder" clearable
                :options="field.options">
            </base-select>

            <el-date-picker v-else-if="field.type == 'datePicker'" v-model="model[field.prop]" :type="field.dateType" clearable
                :value-format="field.format || 'YYYY-MM-DD'" :format="field.format || 'YYYY-MM-DD'"/>

            <!-- slot -->
            <base-render v-else-if="field.type == 'render'" :content="field.render(model[field.prop], model)"  ></base-render>

            <el-input v-else v-model="model[field.prop]" :placeholder="field.placeholder" 
                clearable></el-input>
        </el-form-item>

        <div class="buttons">
            <el-button type="primary" @click="doQuery" icon="search">查询</el-button>
            <el-button @click="doReset" icon="Refresh">重置</el-button>
        </div>
    </el-form>
</template>

<script setup>
import BaseSelect from '@/components/base-select.vue'
import BaseRender from './base-render.js'

const props = defineProps(["fields"])
const model = defineModel()
const emits = defineEmits(["query"])

function doReset() {
    model.value = {}
    doQuery()
}

function doQuery() {
    emits('query', model.value)
}
</script>

<style lang="scss" scoped>
:deep() {
    .el-form-item__content {
        min-width: 260px;
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