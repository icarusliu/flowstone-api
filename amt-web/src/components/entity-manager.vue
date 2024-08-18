<template>
    <!-- 实体类管理 -->
    <div class="mb-2" v-if="withNew != false">
        <el-button type="primary" @click="newItem()">新增</el-button>
    </div>
    <base-table :fields="fields" :dataSupplier="dataSupplier" :pageable="!tree" ref="tableRef">
        <template #append>
            <el-table-column label="操作" :width="operationsWidth || '130px'">
                <template #default="{ row, $index }">
                    <slot name="prefixButtons" :row="row" :index="$index">
                        <!-- 附加按钮 -->
                    </slot>

                    <!-- 操作按钮 -->
                    <slot name="rowButtons" :row="row" :index="$index">
                        <entity-manager-row-buttons :row="row" :index="$index" :tree="tree" :withEdit="withEdit"
                            :withDelete="withDelete" @addSub="addSub" @goEdit="goEdit" @doDelete="doDelete">
                        </entity-manager-row-buttons>
                    </slot>
                    <slot name="appendButtons" :row="row" :index="$index">
                        <!-- 附加按钮 -->
                    </slot>
                </template>
            </el-table-column>
        </template>
    </base-table>

    <!-- 新增或编辑界面 -->
    <el-drawer v-model="visible" :title="title" :close-on-click-modal="false" :close-on-press-escape="false">
        <base-form :fields="newFields" v-model="formModel" labelPosition="top" ref="formRef" />
        <slot name="newRemark" v-if="!formModel.id"></slot>
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
import EntityManagerRowButtons from './entity-manager-row-buttons.vue'

const props = defineProps(["fields", "apiPrefix", "withDelete", "withEdit", "withNew", "tree", "operationsWidth"])
const visible = ref(false)
const newFields = reactive([])
const title = ref("新增")
const formModel = ref({})
const formRef = ref()
const tableRef = ref()
const defModel = ref({})

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

defineExpose({
    goEdit,
    doDelete
})
</script>