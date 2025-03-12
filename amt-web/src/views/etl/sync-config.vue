<template>
    <el-form class="space-between sync-info" :model="model" ref="formRef">
        <!-- 数据同步配置 -->
        <div class="mr-4">
            <el-form-item label="来源数据源" required prop="sourceDs">
                <el-select v-model="model.sourceDs" :disabled="disabled">
                    <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
                </el-select>
            </el-form-item>
            <el-form-item label="源表查询语句" required prop="sourceSql">
                <monacoEditor height="68vh" v-model="model.sourceSql" language="sql" :editorOptions="{ readOnly: disabled }" />
            </el-form-item>
        </div>
        <div class="ml-4">
            <el-form-item label="目标数据源" required prop="destDs">
                <el-select v-model="model.destDs" :disabled="disabled">
                    <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
                </el-select>
            </el-form-item>
            <el-form-item label="目标写入语句" required prop="destSql">
                <monacoEditor height="44vh" v-model="model.destSql" language="sql" :editorOptions="{ readOnly: disabled }" />
            </el-form-item>
            <el-form-item label="预处理语句" prop="prepareSql">
                <monacoEditor height="20vh" v-model="model.prepareSql" language="sql" :editorOptions="{ readOnly: disabled }" />
            </el-form-item>
        </div>
    </el-form>
</template>
<script setup>
import monacoEditor from '@/components/monaco-editor.vue'

const model = defineModel()
const props = defineProps({
    dses: { type: Array, required: true },
    disabled: { type: Boolean }
})
const formRef = ref()

function validate(callback) {
    formRef.value.validate(resp => {
        callback(resp)
    })
}

defineExpose({
    validate
})
</script>

<style lang='scss' scoped>
.sync-info {
    >div {
        width: 50%;

    }
}
</style>