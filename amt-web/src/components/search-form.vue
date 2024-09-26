<template>
    <el-form v-model="model" inline>
        <el-form-item v-for="field in fields" :label="field.label" :prop="field.prop">
            <base-select v-if="field.type == 'select'" v-model="model[field.prop]" :placeholder="field.placeholder"
                :options="field.options" @change="doQuery">
            </base-select>

            <el-input v-else v-model="model[field.prop]" :placeholder="field.placeholder" @change="doQuery"
                clearable></el-input>
        </el-form-item>

        <div class="buttons">
            <el-button type="primary" @click="doQuery">查询</el-button>
            <el-button @click="doReset">重置</el-button>
        </div>
    </el-form>
</template>

<script setup>
import BaseSelect from '@/components/base-select.vue'

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
        width: 200px;
    }
}

.buttons {
    margin-bottom: 18px;
    display: inline-flex;
    vertical-align: middle;
}
</style>