<template>
    <el-descriptions :column="1" border>
        <el-descriptions-item label="数据源">
            <ds-select v-model="currentNode.config.ds" :disabled="!editing" />
        </el-descriptions-item>
        <el-descriptions-item label="SQL语句">
            <el-link type="primary" @click="showDialog">编辑</el-link>
        </el-descriptions-item>
    </el-descriptions>

    <el-dialog v-model="visible" title="脚本编辑" width="80%">
        <template #title>
            <div class="d-flex v-center">
                <div>脚本编辑</div>
                <el-popover title="说明" :width="600" trigger="hover" placement="bottom-end">
                    <div class="text-left">
                        <div>
                            1、SQL语句支持mybatis语法，比如foreach/if/where等，参数使用#{}或者${}来表示；
                        </div>
                        <div>2、使用请求参数：#{request.a}表示从请求参数中获取参数</div>
                        <div>3、使用节点参数：#{nodeCode.a}表示从节点编码为nodeCode的节点输出中获取参数a</div>
                    </div>
                    <template #reference>
                        <el-icon class="cursor-pointer ml-4">
                            <QuestionFilled />
                        </el-icon>
                    </template>
                </el-popover>
            </div>
        </template>
        <monacoEditor height="600px" v-model="script" language="sql" />
        <template #footer>
            <div>
                <el-link type="primary" class="mr-4" @click="visible = false">取消</el-link>
                <el-button type="primary" @click="doSaveSql" :disabled="!editing">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import dsSelect from './components/ds-select.vue';
import monacoEditor from '@/components/monaco-editor.vue'

const visible = ref(false)
const script = ref()
const props = defineProps(["currentNode", "editing"])

// 保存SQL语句
function doSaveSql() {
    props.currentNode.config.sql = script.value
    visible.value = false
}

function showDialog() {
    script.value = props.currentNode.config.sql
    visible.value = true
}

</script>