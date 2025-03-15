<template>
    <div>
        <search-form :fields="queryFields" @query="reload" v-model="queryParams"/>
        <div class="mb-2">
            <el-button type="primary" @click="newRow" v-if="listConfig.withNew != false" icon="plus">新增记录</el-button>
        </div>
        <base-table :fields="fields" :dataSupplier="loadData" ref="tableRef" :params="queryParams"/>

        <el-drawer v-model="visible" title="新增记录">
            <base-form :fields="newFields" v-model="form" labelPosition="top" ref="formRef" />

            <template #footer>
                <el-button type="primary" @click="save">保存</el-button>
                <el-button @click="visible = false">取消</el-button>
            </template>
        </el-drawer>
    </div>
</template>
<script setup>
import baseTable from '@/components/base-table/index.vue'
import searchForm from '@/components/search-form.vue'
import https from '@/utils/https'
import { onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import baseForm from '@/components/base-form/index.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const fields = ref([])
const tableRef = ref()
const visible = ref(false)
const innerFields = ['id', 'createTime', 'createUser', 'updateTime', 'updateUser']
const newFields = ref([])
const queryFields = ref([])
const form = ref({})
const formRef = ref()
let primaryField
const modelInfo = ref({})
const queryParams = ref({})

const modelId = computed(() => {
    return router.currentRoute.value.query.modelId;
})
const listConfig = computed(() => {
    return modelInfo.value.listConfig || {}
})

onMounted(() => {
    https.get('/dua/model/detail/' + modelId.value).then(resp => {
        const model = resp
        modelInfo.value = resp

        for (var i in model.fields) {
            const item = model.fields[i]
            if (item.primaryKey) {
                primaryField = item.code
                break;
            }
        }
        if (!primaryField) {
            primaryField = 'id'
        }

        let sourceFields = model.listFields || model.fields;
        sourceFields.forEach(field => {
            // 处理列表字段
            !field.hide && fields.value.push({
                label: field.name,
                prop: field.code,
                width: field.width
            })

            // 处理查询字段
            if (field.asQuery) {
                let item = {
                    label: field.name,
                    prop: field.code,
                    placeholder: '请输入...'
                }

                // 查询类型
                let queryCmp = field.queryCmp 
                if (queryCmp == 'datePicker') {
                    item.type = 'datePicker'
                } else if (queryCmp == 'dateRange') {
                    item.type = 'datePicker'
                    item.dateType = 'daterange'
                }

                queryFields.value.push(item)
            }

            const code = field.code
            if (!innerFields.includes(code)) {
                // 添加到新增记录中
                let dataType = field.dataType
                let type
                let format
                let dateType
                if (dataType == 'date') {
                    type = 'datePicker'
                    format = 'YYYY-MM-DD'
                    dateType = 'date'
                } else if (dataType == 'datetime') {
                    type = 'datePicker'
                    format = 'YYYY-MM-DD HH:mm:ss'
                    dateType = 'datetime'
                } else if (dataType == 'time') {
                    type = 'timePicker'
                    format = 'HH:mm:ss'
                }

                newFields.value.push({
                    label: field.name,
                    prop: code,
                    type: type,
                    format: format,
                    dateType: dateType,
                    required: !field.nullable
                })
            }
        })

        // 增加操作按钮
        if (listConfig.value.withDelete != false) {
            fields.value.push({
                label: '操作',
                type: 'operations',
                width: '100px',
                buttons: [
                    { label: '删除', type: 'danger', action: deleteRow }
                ]
            })
        }
    })
})

function reload() {
    tableRef.value.reload()
}

function loadData(params) {
    return https.post(`/dua/model/data/${modelId.value}/page-query`, params).then(resp => {
        if (!resp.total) {
            ElMessage.success('查询成功，数据为空');
        }

        return resp;
    })
}

function newRow() {
    form.value = {}
    visible.value = true
}

function save() {
    formRef.value.validate(resp => {
        if (!resp) {
            return
        }

        https.post(`/dua/model/data/${modelId.value}/save`, form.value).then(resp => {
            visible.value = false
            ElMessage.success('操作成功');
            reload()
        })
    })
}

function deleteRow(row) {
    let primaryCode = primaryField.value || 'id'
    ElMessageBox.confirm('确定删除当前记录？').then(() => {
        https.del(`/dua/model/data/${modelId.value}/delete?${primaryCode}=${row[primaryCode]}`).then(() => {
            ElMessage.success('删除成功')
            tableRef.value.reload()
        })
    })
}
</script>

<style lang='scss' scoped></style>