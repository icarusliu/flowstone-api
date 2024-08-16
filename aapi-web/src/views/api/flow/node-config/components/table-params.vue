<!-- 表查询参数配置 -->
<template>
    <title-bar :title="title">
        <template #right>
            <el-link type="primary" @click="addRow" :disabled="!ds || !table || readonly">新增</el-link>
        </template>
    </title-bar>

    <div id="listParams">
        <edit-table v-model="data" :fields="fields" :defRow="defRow" :readonly="!ds || !table || readonly" :showNew="false"
            ref="tableRef" />
    </div>
</template>

<script setup>
import editTable from '@/components/edit-table.vue';
import * as apiApis from '@/apis/api.js'

const data = defineModel()
const props = defineProps(["ds", "table", "readonly", "title", "dag", "inputParams"])
const tableFields = ref([])
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
        options: tableFields
    },
    {
        label: '操作符', prop: 'op', width: '110px', type: 'select', options: [
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
        label: '参数来源', prop: 'type', width: '220px', type: 'select', options: [
            { label: '常量', value: 'const' },
            { label: '请求参数', value: 'request' },
            { label: '节点输出', value: 'node' },
            { label: 'JS', value: 'js' },
            { label: 'Groovy', value: 'groovy' }
        ], disabled: (val, row) => {
            let op = row.op
            return op == 'null' || op == 'notNull'
        },
        class: 'd-flex',
        mainClass: 'flex-auto mr-1',
        subClass: 'flex-auto',
        sub: {
            label: '节点', prop: 'nodeCode', type: 'select',
            placeholder: '请选择节点',
            options: () => {
                return props.dag.getCurrentUpNodes().map(node => {
                    return {
                        label: node.options.name,
                        value: node.options.code
                    }
                })
            },
            show: (row) => {
                return row.type == 'node' && row.op != 'null' && row.op != 'notNull'
            }
        },
    },


    {
        label: '参数值', prop: 'value', disabled: (val, row) => {
            return row.op == 'null' || row.op == 'notNull'
        },
        class: 'd-flex',
        mainClass: 'flex-auto mr-1',
        subClass: 'flex-auto',
        placeholder: '参数一',
        type: (row) => {
            // 如果类型是输入参数，并且配置了输入参数，那么需要从输入参数中选择
            return (row.type == 'request' && props.inputParams && props.inputParams.length) ? 'select' : 'input'
        },
        options: () => (props.inputParams || []).map(item => {
            return {
                label: item.name,
                value: item.code
            }
        }),
        script: (row) => {
            if (row.type == 'js' || row.type == 'groovy') {
                return row.type
            }

            return ''
        },
        sub: {
            placeholder: '参数二',
            prop: 'value1', disabled: (val, row) => row.op != 'between',
            script: row => row.type == 'js' || row.type == 'groovy' ? row.type : '',
            show: (row) => row.op == 'between',
            type: (row) => {
                // 如果类型是输入参数，并且配置了输入参数，那么需要从输入参数中选择
                return (row.type == 'request' && props.inputParams && props.inputParams.length) ? 'select' : 'input'
            },
            options: () => (props.inputParams || []).map(item => {
                return {
                    label: item.name,
                    value: item.code
                }
            }),
        }
    },
])
const defRow = reactive({
    type: 'request',
    op: 'eq'
})
const tableRef = ref(0)

// 表名发生变化时，需要重新加载列名
watch(() => props.table, (val, oldVal) => {
    if (val == oldVal) {
        return
    }

    if (oldVal) {
        data.value = []
    }

    if (!props.ds || !props.table) {
        return
    }

    apiApis.listTableFields(props.ds, props.table).then(resp => {
        tableFields.value = resp.map(item => {
            return {
                label: item,
                value: item
            }
        })
    })
}, {
    immediate: true
})

function addRow() {
    tableRef.value.newRow()
}

</script>

<style lang="scss" scoped>
.item {
    >div {
        margin-right: 8px;
    }
}
</style>
