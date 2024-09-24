<template>
    <el-descriptions :column="1" border>
        <el-descriptions-item label="数据源">
            <ds-select v-model="model.ds" @change="loadTables" :disabled="!editing" />
        </el-descriptions-item>
        <el-descriptions-item label="表">
            <el-select v-model="model.table" :disabled="!editing">
                <el-option v-for="table in tables" :label="table" :value="table" />
            </el-select>
        </el-descriptions-item>
    </el-descriptions>

    <div class="p-2">
        <table-params title="查询条件" v-model="model.params" :readonly="!editing"
            :table="model.table" :ds="model.ds" :dag="dag" :inputParams="inputParams"></table-params>
    </div>
</template>

<script setup>
import dsSelect from './components/ds-select.vue';
import * as apiApis from '@/apis/api.js'
import tableParams from './components/table-params.vue';

const props = defineProps(["dag", "editing", "inputParams"])
const tables = ref([])
onMounted(() => {
    let ds = model.value.ds
    loadTables(ds)
})
const model = defineModel()

// 加载表列表
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