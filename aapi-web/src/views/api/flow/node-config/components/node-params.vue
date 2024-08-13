<!-- 节点输入通用参数配置 -->
<template>
    <div>
        <title-bar :title="title" :remark="remark">
            <template #right>
                <el-link type="primary" @click="newRow" :disabled="readonly" v-if="showAdd != false">新增</el-link>
            </template>
        </title-bar>
        <edit-table v-model="data" :fields="fields" :defRow="defRow" :readonly="readonly" :showNew="false" rowKey="id"
            :showOperations="showOperations"
            operationWidth="60px" ref="tableRef">
        </edit-table>
    </div>
</template>

<script setup>
import editTable from '@/components/edit-table.vue';

const props = defineProps(['readonly', "dag", "title", "remark", "showAdd", "showOperations"])
const data = defineModel()
const defRow = ref({
    type: 'request'
})
const tableRef = ref()
const fields = ref([
    { label: '参数', prop: 'key', disabled: () => props.showOperations == false },
    {
        label: '参数来源', prop: 'type', width: '120px', type: 'select', options: [
            { label: '常量', value: 'const' },
            { label: '请求参数', value: 'request' },
            { label: '节点输出', value: 'node' },
            { label: 'JS', value: 'js' },
            { label: 'Groovy', value: 'groovy' }
        ], disabled: (val, row) => {
            let op = row.op
            return op == 'null' || op == 'notNull'
        },
    },

    {
        label: '参数值', prop: 'nodeCode', type: 'select', width: "400px",
        placeholder: '请选择节点',
        class: 'v-center',
        mainClass: 'wf-1 mr-2',
        subClass: 'flex-auto',
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
        },
        sub: {
            label: '参数值', prop: 'value', disabled: (val, row) => {
                return row.op == 'null' || row.op == 'notNull'
            },

            script: (row) => {
                if (row.type == 'js' || row.type == 'groovy') {
                    return row.type
                }

                return ''
            },
        }

    },
])

function newRow() {
    tableRef.value.newRow()
}

</script>
