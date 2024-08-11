<template>
    <el-descriptions :column="1" border>
        <el-descriptions-item label="数据源">
            <ds-select v-model="currentNode.config.ds" :disabled="!editing" />
        </el-descriptions-item>
        <el-descriptions-item label="SQL语句">
            <el-link type="primary" @click="showDialog">编辑</el-link>
        </el-descriptions-item>
    </el-descriptions>

    <el-dialog v-model="visible" title="脚本编辑">
        <monacoEditor height="400px" v-model="script" language="sql" />
        <template #footer>
            <div>
                <el-link type="primary" class="mr-4" @click="visible = false">取消</el-link>
                <el-button type="primary" @click="doSaveSql" :disabled="readonly">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import dsSelect from '../base/ds-select.vue';
import monacoEditor from '@/components/monaco-editor.vue'

const visible = ref(false)
const script = ref()
const props = defineProps(["dag", "editing"])
const currentNode = computed(() => {
    return props.dag.currentNode?.options || {
        filters: []
    }
})

// 保存SQL语句
function doSaveSql(){
    currentNode.value.config.sql = script.value
    visible.value = false
}

function showDialog() {
    script.value = currentNode.value.config.sql
    visible.value = true
}

</script>