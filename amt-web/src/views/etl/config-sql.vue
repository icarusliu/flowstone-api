<template>
    <el-form class="sql-info" ref="formRef" :model="model">
        <el-form-item label="数据源" required prop="ds">
            <el-select v-model="model.ds" :disabled="disabled">
                <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
            </el-select>
        </el-form-item>
        <el-form-item label="加工语句" required prop="sql">
            <monacoEditor height="67vh" v-model="model.sql" language="sql"  :editorOptions="{readOnly: disabled}"/>
        </el-form-item>
    </el-form>
</template>
<script setup>
import monacoEditor from '@/components/monaco-editor.vue'
const model = defineModel()
const props = defineProps({
    dses: { type: Array, required: true },
    disabled: {type: Boolean}
})

const formRef = ref()
function validate(callback) {
    formRef.value.validate(callback)
}

defineExpose({
    validate
})
</script>

<style lang='scss' scoped></style>