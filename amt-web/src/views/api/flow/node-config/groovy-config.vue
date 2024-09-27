<template>
    <el-descriptions :column="1" border>
        <el-descriptions-item label="Groovy">
            <el-link type="primary" @click="visible = true">编辑</el-link>
        </el-descriptions-item>
    </el-descriptions>

    <scriptDialog lang="groovy" v-model:visible="visible" v-model="model.groovy" :readonly="readonly"></scriptDialog>
</template>

<script setup>
import scriptDialog from '@/components/script-dialog.vue';

const props = defineProps(["readonly"])

const model = defineModel()
const visible = ref(false)
</script>