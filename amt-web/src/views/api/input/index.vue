<!-- 输入参数配置 -->
<template>
    <div class="v-center">
        <el-button type="primary" @click="addRow" :disabled="!editing">新增</el-button>
        <div class="color-remark ml-4">输入参数可不配置，默认会取请求参数中的所有参数当成输入参数</div>
    </div>

    <div id="listParams">
        <edit-table v-model="data" :fields="fields" :defRow="defRow" :readonly="!editing" :showNew="false" rowKey="id"
            operationWidth="100px" ref="tableRef">
            <template #buttons="{ row }">
                <el-link v-if="row.type == 'object'" type="primary" class="mr-2" @click="newSubField(row)">新增</el-link>
            </template>
        </edit-table>
    </div>
</template>

<script setup>
import editTable from '@/components/edit-table.vue';
import * as _ from 'lodash'
import * as uuid from 'uuid'
import * as utils from '@/utils/utils'
import { ElMessage } from 'element-plus'

const data = defineModel()
const props = defineProps(["apiInfo", "editing"])
const fields = ref([
    { label: '参数编码', prop: 'code', disabled: (val, row) => val == '*', width: '400px' },
    {
        label: '参数名', prop: 'name', disabled: (val, row) => row.code == '*', width: '200px'
    },

    {
        label: '参数类型', prop: 'type', width: '120px', type: 'select', options: [
            { label: '字符串', value: 'string' },
            { label: '整数', value: 'int' },
            { label: '小数', value: 'float' },
            { label: '数组', value: 'array' },
            { label: '对象', value: 'object' }
        ], change: (val, row) => {
            // 非对象、数组类型时需要清空children
            if (val == 'array') {
                row.children = [{ code: '*', name: '存储对象类型', type: 'string', readonly: true }]
                return
            }

            delete row.children
        }
    },
    { label: '必需', prop: 'required', width: '60px', type: 'checkbox', disabled: (val, row) => row.code == '*' },
    { label: '默认值', prop: 'default', width: '120px' },
    { label: '备注', prop: 'remark' }
])
const defRow = reactive({
    type: 'string',
    reuiqred: false,
})
const tableRef = ref(0)

function addRow() {
    tableRef.value.newRow()
}

function newSubField(row) {
    if (!row.children) {
        row.children = []
    }
    let subRow = _.cloneDeep(defRow)
    subRow.id = uuid.v4()
    row.children.push(subRow)
}

// 参数校验
function validate(callback) {
    let rows = data.value
    if (!rows) {
        callback(true)
        return
    } 

    let b = utils.loop(rows, item => {
        if (!item.code) {
            ElMessage.error("输入配置中，存在为空的参数编码")
            callback(false)
            return true
        }

        if (item.type == 'object' && (!item.children || !item.children.length)) {
            ElMessage.error("输入配置中，对象类型的参数，必须包含有子元素")
            callback(false)
            return true
        }
    })

    if (b) {
        return callback(false)
    }

    callback(true)
}

defineExpose({validate})
</script>

<style lang="scss" scoped>
:deep() {
    .el-table__cell .cell {
        display: flex;
        align-items: center;

        .cell-content {
            flex: auto;
        }
    }

    .is-center .cell {
        justify-content: center;
    }
}
</style>
