<template>
    <el-link type="primary" v-if="tree" @click="emit('addSub')" v-perm="'new'" icon="Plus">新增</el-link>
    <el-link type="primary" v-if="withEdit != false" @click="emit('goEdit')" v-perm="'update'" icon="Edit">编辑</el-link>
    <el-link type="danger" v-if="withDelete != false" @click="emit('doDelete')" v-perm="'delete'" icon="Delete">删除</el-link>
</template>

<script setup>
const props = defineProps(["tree", "withEdit", "withDelete", "row", "index"]);
const emits = defineEmits(["addSub", "goEdit", "doDelete"]);

function emit(event) {
    emits(event, props.row);
}
</script>
