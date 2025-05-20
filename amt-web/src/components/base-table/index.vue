<template>
    <!-- 基础数据表格 -->
    <div>
        <el-table
            :data="rows"
            row-key="id"
            :default-expand-all="defaultExpandAll != false"
            :border="border != false"
            @rowClick="onRowClick"
            :highlight-current-row="true"
            ref="tableRef"
            @selectionChange="selectionChange"
            :height="height"
        >
            <el-table-column v-if="showSelection" type="selection" align="center" width="40px" />
            <el-table-column width="52px" label="序号" type="index" fixed v-else-if="showIndex != false" align="center" />

            <template v-for="field in fields">
                <el-table-column
                    :prop="field.prop"
                    :key="field.prop"
                    :label="field.label"
                    :fixed="field.fixed"
                    :width="field.width"
                    :type="field.type == 'expand' || field.type == 'selection' ? field.type : 'default'"
                    :min-width="field.minWidth"
                    v-if="field.show != false && field.showInTable != false"
                    :align="field.align"
                    :headerAlign="field.headerAlign || field.align"
                >
                    <template v-if="field.type != 'selection'" #default="{ row, $index }">
                        <base-table-column :row="row" :field="field" :index="$index" />
                    </template>
                </el-table-column>
            </template>

            <slot name="append"></slot>
        </el-table>

        <el-pagination
            v-if="pageable != false"
            :total="total"
            :pageNo="pageNo"
            :pageSize="pageSize"
            :layout="pageSimple ? 'prev, next' : 'prev, pager, next, total'"
            @change="pageChanged"
            background
            size="small"
            class="mt-2"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import baseTableColumn from "./base-table-column.vue";

const props = defineProps([
    "dataSupplier",
    "fields",
    "params",
    "pageable",
    "pageSimple",
    "defaultExpandAll",
    "showIndex",
    "initLoad",
    "pageSize",
    "showSelection",
    "height",
    "border"
]);
const total = ref(0);
const rows = ref([]);
const pageNo = ref(1);
const pageSize = ref(15);
const emits = defineEmits(["rowClick"]);
const tableRef = ref();
const selection = defineModel("selection", {
    default: [],
});

onMounted(() => {
    props.initLoad != false && loadData();
    pageSize.value = props.pageSize || 15;
});

function pageChanged(currentPage, size) {
    pageNo.value = currentPage;
    pageSize.value = size;

    loadData();
}

// 加载数据
function loadData() {
    if (!props.dataSupplier) {
        return;
    }

    if (typeof props.dataSupplier != "function") {
        // 直接数据
        rows.value = props.dataSupplier;
        total.value = rows.value.length;
        return;
    }

    const queryParams = {};
    if (props.params) {
        for (let k in props.params) {
            const v = props.params[k];
            if (!v) {
                continue;
            }

            queryParams[k] = v;
        }
    }

    queryParams.pageNo = pageNo.value;
    queryParams.pageSize = pageSize.value;

    let result = props.dataSupplier(queryParams);
    if (!result) {
        return Promise.resolve([]);
    }

    if (!result.then) {
        result = Promise.resolve(result);
    }

    result.then((resp) => {
        if (resp.records) {
            rows.value = resp.records;
            total.value = resp.total;
        } else {
            // 如果是简易分页时，没有总记录数，但又需要能点击下一页；
            if (props.pageSimple) {
                if (!resp.length || resp.length < pageSize.value) {
                    // 说明没有记录了；
                    total.value = (pageNo.value - 1) * pageSize.value + resp.length;
                } else {
                    // 还有记录，需要能取下一页，因此总记录数要比当前记录数要多；
                    total.value = (pageNo.value + 1) * pageSize.value;
                }
            } else {
                total.value = resp?.length || 0;
            }

            rows.value = resp;
        }
    });
}

function reload(queryReset = true) {
    if (queryReset) {
        pageNo.value = 1;
    }
    total.value = 0;

    loadData();
}

function onRowClick(params) {
    emits("rowClick", params);
}

function toggleRowExpansion(row, expanded) {
    tableRef.value.toggleRowExpansion(row, expanded);
}

function getSelectionRows() {
    return tableRef.value.getSelectionRows();
}

function selectionChange() {
    selection.value = getSelectionRows();
}

function getTotal() {
    return total.value 
}

defineExpose({
    reload,
    toggleRowExpansion,
    getSelectionRows,
    getTotal
});
</script>
