<template>
    <div>
        <el-form :model="form" labelWidth="160px" class="mt-5" ref="formRef">
            <el-form-item label="模型分类" required prop="typeId">
                <treeSelect :options="loadTypes" v-model="form.typeId" :disabled="!editing" />
            </el-form-item>
            <el-form-item label="模型名称" required prop="name">
                <el-input v-model="form.name" :disabled="!editing" />
            </el-form-item>
            <el-form-item label="模型编码" required prop="code">
                <el-input v-model="form.code" :disabled="!editing" />
            </el-form-item>
            <el-form-item label="模型状态" v-if="form.id">
                {{ form.status == 0 ? '草稿' : form.status == 1 ? '已应用' : form.status == 2 ? '修改中' : '已下线' }}
            </el-form-item>
            <el-form-item label="模型字段" required prop="fields">
                <edit-table :fields="fieldFields" v-model="form.fields" :showSelection="true" :border="true" height="64vh"
                    :defRow="{ dataType: 'varchar', dataConfig: '(255)', nullable: true }" :readonly="!editing" :newIdx="-4"
                    :showIndex="true" ref="tableRef" @selectionChange="selectionChange" :selectable="selectable">
                    <template #appendButtons>
                        <el-button type="danger" plain :disabled="!editing || !rowSelected" @click="batchDelete">删除</el-button>
                        <el-button :disabled="!editing" @click="toTop">移至顶部</el-button>
                        <el-button :disabled="!editing" @click="up">上移</el-button>
                        <el-button :disabled="!editing" @click="down">下移</el-button>
                        <el-button :disabled="!editing" @click="toBottom">移至底部</el-button>
                    </template>
                </edit-table>
            </el-form-item>
        </el-form>

        <div class="text-center">
            <span v-if="!editing">
                <el-button type="primary" @click="startEdit">编辑</el-button>
                <el-button type="success" v-if="form.status != 1" @click="apply">应用</el-button>
                <el-button class="ml-2" @click="close">返回</el-button>
            </span>
            <span v-else>
                <el-button type="primary" @click="save">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
            </span>
        </div>
    </div>
</template>
<script setup>
import treeSelect from '@/components/base-tree-select.vue'
import EditTable from '@/components/edit-table/index.vue'
import { ElMessage } from 'element-plus'
import https from '@/utils/https'
import * as _ from 'lodash'
import { nextTick } from 'vue'

const form = defineModel()
const editing = defineModel("editing")
const emits = defineEmits(['close'])
const dataTypes = [
    { label: '字符串', value: 'varchar', config: '(255)', required: true },
    { label: '整数', value: 'bigint', config: '(32)', default: 0 },
    { label: '小数', value: 'numeric', config: '(24, 4)', default: 0 },
    { label: '日期', value: 'date', config: '', default: 'current_date' },
    { label: '日期时间', value: 'datetime', config: '', default: 'current_datetime' },
    { label: '时间', value: 'time', config: '', default: 'current_time' }
]
const fieldFields = ref([
    { label: '字段编码', prop: 'code' },
    { label: '字段名称', prop: 'name' },
    {
        label: '数据类型', prop: 'dataType', type: 'select', options: dataTypes, change: (val, form) => {
            for (var i in dataTypes) {
                let item = dataTypes[i]
                if (item.value == val) {
                    form.dataConfig = item.config
                    form.defaultValue = item.default
                    return
                }
            }
        }
    },
    { label: '字段配置', prop: 'dataConfig' },
    { label: '是否可为空', prop: 'nullable', type: 'checkbox', align: 'center', width: '100px' },
    { label: '是否主键', prop: 'primaryKey', type: 'checkbox', disabled: true, align: 'center', width: '100px' },
    { label: '默认值', prop: 'defaultValue' }
])
const formRef = ref()
const tableRef = ref()
const rowSelected = ref(false)
const innerFields = ['id', 'createTime', 'createUser', 'updateTime', 'updateUser']

function save() {
    formRef.value.validate(resp => {
        if (!resp) {
            return
        }

        let fields = form.value.fields
        if (fields) {
            for (var i in fields) {
                fields[i].sort = i.toString().padStart(3, '0')
            }
        }

        if (form.value.id) {
            https.put('/dua/model/update', form.value).then(resp => {
                ElMessage.success('操作成功')
                editing.value = false;
            })
        } else {
            https.post('/dua/model/add', form.value).then((resp) => {
                ElMessage.success('操作成功')
                form.value.id = resp.id
                editing.value = false;
            })
        }
    })
}

function loadTypes() {
    return https.get('/dua/model-type/table-tree');
}

let savedData
function startEdit() {
    savedData = _.cloneDeep(form.value)
    editing.value = true
}

function close() {
    emits('close')
}

function cancelEdit() {
    if (!form.value.id) {
        return emits('close')
    }

    form.value = savedData
    editing.value = false
}

function apply() {
    https.get('/dua/model/publish', { id: form.value.id }).then(() => {
        ElMessage.success('操作成功')
        form.value.status = 1
    })
}


function selectionChange() {
    let selectedRows = tableRef.value.getSelection()
    if (selectedRows && selectedRows.length) {
        rowSelected.value = true
    } else {
        rowSelected.value = false
    }
}

// 是否可选中，内置字段不能选中
function selectable(row) {
    return !innerFields.includes(row.code)
}

function batchDelete() {
    let selectedRows = tableRef.value.getSelection()
    if (!selectedRows || !selectedRows.length) {

    }
}

function toTop() {
    let rows = form.value.fields
    let selectedRows = tableRef.value.getSelection()
    
    // 从最后一个往上，都放第1个位置 
    selectedRows.reverse().forEach(row => {
        let idx = rows.indexOf(row)
        rows.splice(idx, 1)
        rows.splice(1, 0, row)
    })
}

function up() {
    let rows = form.value.fields
    let selectedRows = tableRef.value.getSelection()

    // 选择的行可能不连续，需要从第一个开始上移
    let lastMoved = false
    let lastIdx = 0
    
    selectedRows.forEach(row => {
        let idx = rows.indexOf(row)

        if (idx <= 1) {
            // 第一行放id，最多只能移到第二行
            lastMoved = false
            lastIdx = idx
            return;
        }

        // 如果上一个没移动，而且现在的这行与上一行是连续的，也不进行移动 
        if (lastIdx && !lastMoved && lastIdx == idx - 1) {
            lastIdx = idx
            return
        }
        lastIdx = idx
        lastMoved = true

        rows.splice(idx, 1)
        rows.splice(idx - 1, 0, row)
    })
}

function down() {
    let rows = form.value.fields
    let selectedRows = tableRef.value.getSelection()
    let length = rows.length

    // 选择的行可能不连续，需要从最后一个往上处理
    let lastMoved = false
    let lastIdx = 0

    selectedRows.reverse().forEach(row => {
        let idx = rows.indexOf(row)

        // 最后四行是内置字段
        if (idx == length - 5) {
            lastIdx = idx 
            lastMoved = false
            return;
        }
        if (lastIdx && !lastMoved && lastIdx == idx + 1) {
            lastIdx = idx 
            return
        }

        lastIdx = idx 
        lastMoved = true

        rows.splice(idx, 1)
        rows.splice(idx + 1, 0, row)
    })
}

function toBottom() {
    let rows = form.value.fields
    let selectedRows = tableRef.value.getSelection()
    let lastIdx = rows.length - 5

    // 从第一个往下，均放在最后一个 
    selectedRows.forEach(row => {
        let idx = rows.indexOf(row)
        rows.splice(idx, 1)
        rows.splice(lastIdx, 0, row)
    })
}
</script>

<style lang='scss' scoped></style>