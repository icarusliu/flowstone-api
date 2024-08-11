<!-- 列表参数配置 -->
<template>
    <title-bar :title="title">
        <template #right>
            <el-link type="primary" @click="addRow" :disabled="readonly">新增</el-link>
        </template>
    </title-bar>

    <div id="listParams">
        <edit-table v-model="data" :fields="fields" :defRow="defRow" :readonly="readonly" :showNew="false" ref="tableRef" />
    </div>
</template>

<script setup>
import editTable from '../../../../components/edit-table.vue';
import * as apiApis from '@/apis/api.js'

const data = defineModel()
const props = defineProps(["ds", "table", "readonly", "title", "dag"])
const fields = ref([
    {
        label: '字段', prop: 'key', width: '120px',
        type: 'select',
        change: (val, row) => {
            if (!val || row.value) {
                return
            }

            row.value = val
        },
        options: () => {
            return apiApis.listTableFields(props.ds, props.table).then(resp => {
                return resp.map(item => {
                    return {
                        label: item,
                        value: item
                    }
                })
            })
        }
    },
    {
        label: '操作符', prop: 'op', width: '100px', type: 'select', options: [
            { label: '为空', value: 'null' },
            { label: '不为空', value: 'notNull' },
            { label: '等于', value: 'eq' },
            { label: '模糊匹配', value: 'like' },
            { label: '不等于', value: 'neq' },
            { label: '小于', value: 'lt' },
            { label: '小于等于', value: 'le' },
            { label: '大于', value: 'gt' },
            { label: '大于等于', value: 'gte' },
            { label: '范围', value: 'in' },
            { label: '两者之间', value: 'between' },
        ]
    },
    {
        label: '参数来源', prop: 'type', width: '108px', type: 'select', options: [
            { label: '常量', value: 'const' },
            { label: '请求参数', value: 'request' },
            { label: '节点输出', value: 'node' },
            { label: 'JS', value: 'js' },
            { label: 'Groovy', value: 'groovy' }
        ], disabled: (val, row) => {
            let op = row.op
            return op == 'null' || op == 'notNull'
        }
    },

    {
        label: '节点', prop: 'nodeCode', width: '108px', type: 'select', 
        options: () => {
            return props.dag.getCurrentUpNodes().map(node => {
                return {
                    label: node.options.name,
                    value: node.options.code
                }
            })
        },
        disabled: (val, row) => {
            return row.type != 'node' || row.op == 'null' || row.op == 'notNull'
        }
    },
    {
        label: '参数值', prop: 'value', disabled: (val, row) => {
            return row.op == 'null' || row.op == 'notNull'
        },
        typeSupplier: (row) => {
            if (row.type == 'js' || row.type == 'groovy') {
                return row.type
            }

            return ''
        }
    },
])
const defRow = reactive({
    type: 'request',
    op: 'eq'
})
const tableRef = ref(0)

function addRow() {
    tableRef.value.newRow()
}

</script>

<style lang="scss" scoped>
:deep() {
    .drawer {
        right: 434px;
        top: 176px;
        height: calc(100% - 190px);
    }
}

.item {
    >div {
        margin-right: 8px;
    }
}</style>
