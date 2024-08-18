<template>
    <el-descriptions :column="1" border>
        <el-descriptions-item label="JS">
            <el-link type="primary" @click="visible = true">编辑</el-link>
        </el-descriptions-item>
    </el-descriptions>

    <ScriptDialog lang="javascript" v-model:visible="visible" v-model="model.js" :readonly="readonly"></ScriptDialog>
</template>

<script setup>
import ScriptDialog from '@/components/script-dialog.vue';

defineProps(["readonly"])

const model = defineModel()
const visible = ref(false)
</script>