<!-- 表节点类型分页参数配置 -->
<template>
    <div>
        <title-bar title="分页参数">
        </title-bar>
        <edit-table v-model="data" :fields="fields" :readonly="readonly" :showNew="false" rowKey="id" :showOperations="false"
            ref="tableRef">
        </edit-table>
    </div>
</template>

<script setup>
import editTable from '@/components/edit-table/index.vue';

const props = defineProps(['readonly', "dag"])
const data = defineModel()
const fields = ref([
    { label: '参数', prop: 'key', type: 'text', width: '100px' },
    { label: '参数说明', prop: 'desc', type: 'text', width: '140px' },
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
        label: '参数值', prop: 'nodeCode', type: 'select',
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

            type: (row) => {
                // 如果类型是输入参数，并且配置了输入参数，那么需要从输入参数中选择
                return (row.type == 'request' && props.inputParams && props.inputParams.length) ? 'auto-complete' : 'input'
            },
            options: () => (props.inputParams || []).map(item => item.code),

            script: (row) => {
                if (row.type == 'js' || row.type == 'groovy') {
                    return row.type
                }

                return ''
            },
        }

    },
])

</script>
