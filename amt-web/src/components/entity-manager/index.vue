<template>
    <!-- 实体类管理 -->
    <div>
        <search-form class="bg-white mb-4 p-4 br-1" v-if="queryFields && queryFields.length" v-model="searchParams" :fields="queryFields"
            @query="reload" />

        <div class="bg-white p-4 br-1">
            <!-- 按钮 -->
            <div class="mb-2">
                <el-button type="primary" @click="newItem()" v-perm="'new'" icon="plus" v-if="withNew != false">新增</el-button>
                <slot name="buttons" />
            </div>

            <!-- 表格 -->
            <base-table :fields="fields" :dataSupplier="dataSupplier" :pageable="pageable != false && !tree" :showIndex="true"
                ref="tableRef" @rowClick="rowClick">
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
        </div>

        <!-- 新增或编辑界面 -->
        <el-drawer v-model="visible" :title="formModel.id ? '编辑' : '新增'" :close-on-click-modal="false" :close-on-press-escape="false">
            <base-form :fields="newFields" v-model="formModel" labelPosition="top" ref="formRef" />
            <slot name="newRemark" v-if="!formModel.id"></slot>
            <template #footer>
                <div class="text-right">
                    <el-link type="primary" class="mr-2" @click="visible = false">取消</el-link>
                    <el-button type="primary" @click="doSave">保存</el-button>
                    <slot name="formButtons" :model="formModel"></slot>
                </div>
            </template>
        </el-drawer>
    </div>
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, onMounted, reactive } from 'vue'
import * as _ from 'lodash'
import EntityManagerRowButtons from './entity-manager-row-buttons.vue'
import SearchForm from '@/components/search-form.vue'

const props = defineProps(["queryFields", "fields", "formFields", "apiPrefix", "withDelete", "withEdit", "withNew",
    "tree", "operationsWidth", "params", "pageable", "dataSupplier", "queryParamConverter"])
const visible = ref(false)
const newFields = reactive([])
const formModel = ref({})
const formRef = ref()
const tableRef = ref()
const defModel = ref({})
const emits = defineEmits(["rowClick", "startEdit"])
const searchParams = ref({})

onMounted(() => {
    if (props.formFields) {
        newFields.push(...props.formFields)
        return
    }

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

function loadDefModel() {
    props.fields.forEach(field => {
        if (field.default || field.default == 0) {
            defModel.value[field.prop] = field.default
        }
    })
}

// 添加子元素
function addSub(row) {
    loadDefModel()
    const model = _.cloneDeep(defModel.value)
    model.parentId = row.id
    formModel.value = model
    visible.value = true
}

// 打开编辑界面
function goEdit(row) {
    formModel.value = _.cloneDeep(row)
    emits('startEdit', row)
    visible.value = true
}

// 删除
function doDelete(row) {
    if (row.children && row.children.length) {
        ElMessage.error("存在子元素，请删除子元素后再删除当前记录")
        return
    }

    ElMessageBox.confirm('确认删除当前数据么？数据删除后无法恢复！').then(() => {
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
    let finalParams = params

    if (props.params) {
        finalParams = {
            ...props.params,
            ...params,
        }
    }

    if (searchParams.value) {
        finalParams = {
            ...finalParams,
            ...searchParams.value
        }
    }

    if (props.queryParamConverter) {
        finalParams = props.queryParamConverter(finalParams)
    }

    if (props.dataSupplier) {
        return props.dataSupplier(finalParams);
    }

    if (props.tree) {
        return entityApis.tree(props.apiPrefix, finalParams)
    } else {
        return entityApis.load(props.apiPrefix, finalParams);
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

function rowClick() {
    emits('rowClick', ...arguments)
}

function reload() {
    tableRef.value.reload()
}

defineExpose({
    goEdit,
    doDelete,
    reload
})
</script>