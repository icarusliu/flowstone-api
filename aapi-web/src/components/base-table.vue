<template>
    <!-- 基础数据表格 -->
    <el-table :data="rows" row-key="id" :default-expand-all="defaultExpandAll != false" border @rowClick="onRowClick"
        ref="tableRef">
        <el-table-column width="52px" label="序号" type="index" fixed />

        <el-table-column v-for="field in fields" :prop="field.prop" :key="field.prop" :label="field.label"
            :fixed="field.fixed" :width="field.width" :type="field.type == 'expand' ? 'expand' : 'default'"
            :min-width="field.minWidth">

            <template #default="{ row }">
                <base-table-column :row="row" :field="field" />
            </template>
        </el-table-column>

        <slot name="append"></slot>
    </el-table>

    <el-pagination v-if="pageable != false" :total="total" :pageNo="pageNo" layout="prev, pager, next" @change="pageChanged"
        background size="small" class="mt-2" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import baseTableColumn from './base-table-column.vue';

const props = defineProps(["dataSupplier", "fields", "params", "pageable", "defaultExpandAll"])
const total = ref(0)
const rows = ref([])
const pageNo = ref(0)
const pageSize = ref(10)
const emits = defineEmits(["rowClick"])
const tableRef = ref()

onMounted(() => {
    loadData()
})

function pageChanged(currentPage, size) {
    pageNo.value = currentPage
    pageSize.value = size

    loadData()
}

// 加载数据
function loadData() {
    if (!props.dataSupplier) {
        return;
    }

    if (typeof props.dataSupplier != 'function') {
        // 直接数据
        rows.value = props.dataSupplier
        total.value = row.value.length
        return
    }

    const queryParams = {}
    if (props.params) {
        for (let k in props.params) {
            const v = props.params[k]
            if (!v) {
                continue
            }

            queryParams[k] = v
        }
    }

    queryParams.pageNo = pageNo.value
    queryParams.pageSize = pageSize.value

    let result = props.dataSupplier(queryParams)
    if (!result) {
        return Promise.resolve([])
    }

    if (!result.then) {
        result = Promise.resolve(result)
    }

    result.then(resp => {
        if (resp.records) {
            rows.value = resp.records
            total.value = resp.total
        } else {
            rows.value = resp
            total.value = resp?.length || 0
        }
    })
}

function reload() {
    pageNo.value = 0
    loadData()
}

function onRowClick(params) {
    emits('rowClick', params)
}

function toggleRowExpansion(row, expanded) {
    tableRef.value.toggleRowExpansion(row, expanded)
}

defineExpose({
    reload,
    toggleRowExpansion
})
</script>