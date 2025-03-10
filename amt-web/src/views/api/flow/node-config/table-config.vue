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
        <el-descriptions-item label="启用分页">
            <el-checkbox v-model="model.pageable" :disabled="!editing"/>
            <div class="remark">
                不启用分页时，节点返回的数据为列表类型的数据，如果启用分页，返回的数据将会是对象类型，其中total存储总记录数，records存储记录列表
            </div>
        </el-descriptions-item>
    </el-descriptions>

    <div class="p-2" v-if="model.pageable">
        <table-page-params v-model="pageData" :dag="dag"/>
    </div>

    <div class="p-2">
        <table-params title="查询条件" v-model="model.params" :readonly="!editing" :table="model.table" :ds="model.ds" :dag="dag"
            :inputParams="inputParams"></table-params>
    </div>
</template>

<script setup>
import dsSelect from './components/ds-select.vue';
import * as apiApis from '@/apis/api.js'
import tableParams from './components/table-params.vue';
import tablePageParams from './components/table-page-params.vue';

const props = defineProps(["dag", "editing", "inputParams"])
const tables = ref([])
const pageData = computed({
    get() {
        let arr = []
        arr.push(model.value.pageNo || {key: 'pageNo', type: 'request', value: 'pageNo', desc: '页码，从1开始'})
        arr.push(model.value.pageSize || {key: 'pageSize', type: 'request', value: 'pageSize', desc: '每页记录数'})
        return arr
    },
    set(val) {
        model.value.pageNo = val[0]
        model.value.pageSize = val[1]
    }
})
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