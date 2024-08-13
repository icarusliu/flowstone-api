<template>
    <!-- 实体类管理 -->
    <div class="mb-2" v-if="withNew != false">
        <el-button type="primary" @click="newItem()">新增</el-button>
    </div>
    <base-table :fields="fields" :dataSupplier="dataSupplier" :pageable="!tree" ref="tableRef"/>

    <!-- 新增或编辑界面 -->
    <el-drawer v-model="visible" :title="title" :close-on-click-modal="false" :close-on-press-escape="false">
        <base-form :fields="newFields" v-model="formModel" labelPosition="top" ref="formRef" />

        <template #footer>
            <div class="text-right">
                <el-link type="primary" class="mr-2" @click="visible = false">取消</el-link>
                <el-button type="primary" @click="doSave">保存</el-button>
            </div>
        </template>
    </el-drawer>
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, onMounted, reactive } from 'vue'
import * as _ from 'lodash'

const props = defineProps(["fields", "apiPrefix", "withDelete", "withEdit", "withNew", "tree", "operationsWidth"])
const visible = ref(false)
const newFields = reactive([])
const title = ref("新增")
const formModel = ref({})
const formRef = ref()
const tableRef = ref()
const defModel = ref({})
let hasOperation = false;

onMounted(() => {
    if (!props.fields) {
        return
    }
    props.fields.forEach(field => {
        if (field.needNew != false && field.type != 'operations' && !field.system) {
            // field.system表示是否是系统字段
            newFields.push(field);
        } else if (field.type == 'operations') {
            hasOperation = true
        }

        // 取默认值
        if (field.default || field.default == 0) {
            defModel.value[field.prop] = field.default
        }
    })

    if (!hasOperation) {
        const buttons = []
        if (props.tree) {
            buttons.push({ label: '添加', type: 'primary', action: addSub })
        }
        if (props.withEdit != false) {
            buttons.push({ label: '编辑', type: 'primary', action: goEdit })
        }
        if (props.withDelete != false) {
            buttons.push({ label: '删除', type: 'danger', action: doDelete })
        }

        // 补充按钮 
        props.fields.push({
            label: '操作',
            type: 'operations',
            buttons,
            width: props.operationsWidth || '120px',
            fixed: 'right'
        })
        hasOperation = true
    }
})

// 添加子元素
function addSub(row) {
    const model = _.cloneDeep(defModel.value)
    model.parentId = row.id
    formModel.value = model
    visible.value = true
}

// 打开编辑界面
function goEdit(row) {
    formModel.value = _.cloneDeep(row)
    visible.value = true
}

// 删除
function doDelete(row) {
    ElMessageBox.confirm('确认删除当前记录？').then(() => {
        entityApis.remove(props.apiPrefix, row.id).then(() => {
            ElMessage.success('删除成功')
            tableRef.value.reload()
        })
    })
}

// 打开新增界面
function newItem() {
    formModel.value = _.cloneDeep(defModel.value)
    visible.value = true
}

// 数据查询
function dataSupplier(params) {
    if (props.tree) {
        return entityApis.tree(props.apiPrefix)
    } else {
        return entityApis.load(props.apiPrefix, params);
    }
}

// 保存
function doSave() {
    formRef.value.validate(result => {
        if (!result) {
            return;
        }

        entityApis.save(props.apiPrefix, formModel.value).then(() => {
            ElMessage.success('操作成功');
            tableRef.value.reload()
            visible.value = false
        })
    })
}
</script>