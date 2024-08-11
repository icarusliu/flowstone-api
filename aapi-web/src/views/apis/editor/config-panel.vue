<template>
    <div class="d-flex-col">
        <el-descriptions :column="1" border>
            <el-descriptions-item label="节点编码">
                <el-input v-model="currentNode.code" @change="val => updateField('code', val)" :disabled="!editing"/>
            </el-descriptions-item>
            <el-descriptions-item label="节点名称">
                <el-input v-model="currentNode.name" @change="val => updateField('name', val)"  :disabled="!editing"/>
            </el-descriptions-item>
            <el-descriptions-item label="节点类型">{{ currentNode.typeName || currentNode.type }}</el-descriptions-item>
        </el-descriptions>
        <table-config v-if="currentNode.type == 'table'" :dag="dag" :editing="editing">
        </table-config>
        <sql-config v-else-if="currentNode.type == 'sql'" :dag="dag" :editing="editing">
        </sql-config>
        <js-config v-else-if="currentNode.type == 'js'" :dag="dag" :editing="editing"/>
        <groovyConfig v-else-if="currentNode.type == 'groovy'" :dag="dag" :editing="editing"/>
        <httpConfig v-else-if="currentNode.type == 'http'" :dag="dag" :editing="editing"/>

        <!-- <div class="text-center mt-2">
            <el-button type="danger">删除节点</el-button>
        </div> -->
    </div>
</template>

<script setup>
import { computed } from 'vue'
import $ from 'jquery'
import TableConfig from './type-config/table-config.vue';
import SqlConfig from './type-config/sql-config.vue';
import jsConfig from './type-config/js-config.vue';
import groovyConfig from './type-config/groovy-config.vue';
import httpConfig from './type-config/http-config.vue';

const props = defineProps(["dag", "editing"])

const currentNode = computed(() => {
    return props.dag.currentNode?.options || {}
})

function updateField(field, val) {
    props.dag.currentNode.options[field] = val
    if (field == 'name') {
        $(props.dag.currentNode.dom).html(val)
    }
}
</script>

<style lang="scss" scoped>
:deep() {
    .el-descriptions__label.el-descriptions__cell.is-bordered-label {
        width: 160px;
    }
}
</style>
