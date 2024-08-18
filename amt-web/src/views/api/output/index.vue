<!-- 输入参数配置 -->
<template>
    <div class="v-center">
        <el-button type="primary" @click="addRow" :disabled="!editing">新增</el-button>
        <el-button type="primary" @click="showJson = true" :disabled="!editing" plain>从JSON加载</el-button>
        <div class="color-remark ml-4">输出配置主要用于作为接口文档使用，并不对实际的接口输出结果有影响</div>
    </div>

    <div id="listParams">
        <edit-table v-model="data" :fields="fields" :defRow="defRow" :readonly="!editing" :showNew="false" rowKey="id"
            operationWidth="100px" ref="tableRef">
            <template #buttons="{ row }">
                <el-link v-if="row.type == 'object'" type="primary" class="mr-2" @click="newSubField(row)">新增</el-link>
            </template>
        </edit-table>
    </div>

    <div class="mt-4 mb-8">
        <div class="page-title">
            返回示例
        </div>
        <monaco-editor v-model="apiInfo.content.outputExample" language="json" height="300px" :editorOptions="{readOnly: true}"/>
    </div>

    <el-dialog title="从JSON加载配置" v-model="showJson">
        <monaco-editor v-model="json" language="json" height="500px" />

        <template #footer>
            <el-button @click="showJson = false">取消</el-button>
            <el-button type="primary" @click="doLoad" :disabled="!json">加载</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import editTable from '@/components/edit-table.vue';
import monacoEditor from '../../../components/monaco-editor.vue';
import * as _ from 'lodash'
import * as uuid from 'uuid'
import * as utils from '@/utils/utils'
import { ElMessage } from 'element-plus'
import * as jsonUtils from '@/utils/json'

const data = defineModel()
const props = defineProps(["apiInfo", "editing"])
const fields = ref([
    { label: '参数编码', prop: 'code', disabled: (val, row) => val == '*', width: '400px' },
    {
        label: '参数名', prop: 'name',  width: '200px'
    },
    {
        label: '参数类型', prop: 'type', width: '120px', type: 'select', options: [
            { label: '无限制', value: 'unknown' },
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
    { label: '备注', prop: 'remark' }
])
const defRow = reactive({
    type: 'string',
    reuiqred: false,
})
const tableRef = ref(0)
const json = ref("")
const showJson = ref(false)

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
            ElMessage.error("输出配置中，存在为空的参数编码")
            callback(false)
            return true
        }

        if (item.type == 'object' && (!item.children || !item.children.length)) {
            ElMessage.error("输出配置中，对象类型的参数，必须包含有子元素")
            callback(false)
            return true
        }
    })

    if (b) {
        return callback(false)
    }

    callback(true)
}

function doLoad() {
    // 从JSON中加载数据
    try {
        let d = JSON.parse(json.value);
        let result = jsonUtils.loadJsonSchema(d)
        data.value = result || []
        showJson.value = false
    } catch (err) {
        console.error(err)
        ElMessage.error("JSON格式无效")
    }

}

defineExpose({ validate })
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
