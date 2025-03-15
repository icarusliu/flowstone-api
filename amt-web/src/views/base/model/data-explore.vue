<template>
    <div>
        <div class="mb-2">
            <!-- <el-button type="primary" @click="newRow">新增记录</el-button> -->
            <el-button type="primary" @click="reload">加载</el-button>
        </div>
        <base-table :fields="fields" :dataSupplier="loadData" ref="tableRef" v-if="model.id" :initLoad="false"/>

        <el-drawer v-model="visible" title="新增记录">
            <base-form :fields="newFields" v-model="form" labelPosition="top" ref="formRef"/>

            <template #footer>
                <el-button type="primary" @click="save">保存</el-button>
                <el-button @click="visible.value = false">取消</el-button>
            </template>
        </el-drawer>
    </div>
</template>
<script setup>
import baseTable from '@/components/base-table/index.vue'
import https from '@/utils/https'
import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import baseForm from '@/components/base-form/index.vue'

const props = defineProps({
    model: {type: Object, required: true}
})
const fields = ref([])
const tableRef = ref()
const visible= ref(false)
const innerFields = ['id', 'createTime', 'createUser', 'updateTime', 'updateUser']
const newFields = ref([])
const form = ref({})
const formRef = ref()

onMounted(() => {
    props.model.fields && props.model.fields.forEach(field => {
        fields.value.push({
            label: field.name,
            prop: field.code
        })

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
})

function reload() {
    tableRef.value.reload()
}

function loadData(params) {
    return https.post('/dua/model/data/page-query/' + props.model.id, params).then(resp => {
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

        https.post('/dua/model/data/save', form.value).then(resp => {
            console.log(resp);
            visible.value = false
            ElMessage.success('操作成功');
        })
    })
}
</script>

<style lang='scss' scoped>

</style>