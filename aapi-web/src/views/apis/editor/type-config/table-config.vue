<template>
    <el-descriptions :column="1" border>
        <el-descriptions-item label="数据源">
            <ds-select v-model="currentNode.config.ds" @change="loadTables" :disabled="!editing" />
        </el-descriptions-item>
        <el-descriptions-item label="表">
            <el-select v-model="currentNode.config.table" :disabled="!editing">
                <el-option v-for="table in tables" :label="table" :value="table" />
            </el-select>
        </el-descriptions-item>
    </el-descriptions>

    <div class="p-2">
        <list-params title="查询条件" v-model="currentNode.config.filters" :readonly="!editing"
            :table="currentNode.config.table" :ds="currentNode.config.ds" :dag="dag"></list-params>
    </div>
</template>

<script setup>
import dsSelect from '../base/ds-select.vue';
import * as apiApis from '@/apis/api.js'
import listParams from '../base/list-params.vue';

const props = defineProps(["dag", "editing"])
const tables = ref([])
const currentNode = computed(() => {
    return props.dag.currentNode?.options || {
        filters: []
    }
})
onMounted(() => {
    let ds = props.dag.currentNode.options?.config?.ds
    loadTables(ds)
})

function loadTables(val) {
    if (!val) {
        tables.value = []
        return
    }

    apiApis.listTables(val).then(resp => {
        tables.value = resp || []
    })
}

</script>