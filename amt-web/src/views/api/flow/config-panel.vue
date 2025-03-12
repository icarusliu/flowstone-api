<template>
    <div class="d-flex-col config-panel">
        <!-- 节点公共配置 -->
        <el-descriptions :column="1" border>
            <el-descriptions-item label="节点编码">
                <el-input v-model="currentNode.code" @change="val => updateField('code', val)" :disabled="!editing" />
            </el-descriptions-item>
            <el-descriptions-item label="节点名称">
                <el-input v-model="currentNode.name" @change="val => updateField('name', val)" :disabled="!editing" />
            </el-descriptions-item>
            <el-descriptions-item label="节点类型">{{ currentNode.typeName || currentNode.type }}</el-descriptions-item>
            <el-descriptions-item label="节点执行条件">
                <el-link type="primary" @click="showCondition" :disabled="!editing">编辑</el-link>
            </el-descriptions-item>
        </el-descriptions>

        <!-- 表节点配置 -->
        <table-config v-if="currentNode.type == 'table'" :dag="dag" v-model="currentNode.config" :inputParams="inputParams"
            :editing="editing">
        </table-config>

        <!-- SQL节点配置 -->
        <sql-config v-else-if="currentNode.type == 'sql'" v-model="currentNode.config" :inputParams="inputParams" :dag="dag"
            :editing="editing">
        </sql-config>

        <!-- JS节点配置 -->
        <js-config v-else-if="currentNode.type == 'js'" :readonly="!editing" v-model="currentNode.config" />

        <!-- Groovy节点配置 -->
        <groovyConfig v-else-if="currentNode.type == 'groovy'" v-model="currentNode.config" :readonly="!editing"
            :inputParams="inputParams" />

        <!-- Http节点配置 -->
        <httpConfig v-else-if="currentNode.type == 'http'" v-model="currentNode.config" :dag="dag" :readonly="!editing"
            :inputParams="inputParams" />

        <ScriptDialog lang="javascript" v-model:visible="visible" v-model="currentNode.config.condition" :readonly="!editing"></ScriptDialog>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import $ from 'jquery'
import TableConfig from './node-config/table-config.vue';
import SqlConfig from './node-config/sql-config.vue';
import jsConfig from './node-config/js-config.vue';
import groovyConfig from './node-config/groovy-config.vue';
import httpConfig from './node-config/http-config.vue';
import ScriptDialog from '@/components/script-dialog.vue';

const props = defineProps(["dag", "editing", "inputParams"])
const visible = ref(false)

const currentNode = computed(() => {
    return props.dag.currentNode?.options || {
        config: {}
    }
})

function updateField(field, val) {
    props.dag.currentNode.options[field] = val
    if (field == 'name') {
        $(props.dag.currentNode.dom).html(val)
    }
}

function showCondition() {
    if (!currentNode.value.config.condition) {
        currentNode.value.config.condition = `function func(params) {
    // 节点及后续节点是否执行标志，为0时不做限制；为1时当前节点不执行；为2时后续节点不执行；
    return 0; 
}`
    } 

    visible.value = true
}
</script>

<style lang="scss" scoped>
:deep() {
    .el-descriptions__label.el-descriptions__cell.is-bordered-label {
        width: 160px;
    }
}

.config-panel {
    overflow-y: auto;
}
</style>
