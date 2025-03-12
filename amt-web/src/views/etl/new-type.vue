<template>
    <el-dialog v-model="visible" title="新增/编辑分类" width="600px" destroy-on-close>
        <base-form :fields="fields" v-model="form" ref="formRef" />
        <template #footer>
            <el-button type="primary" @click="doSave">确定</el-button>
            <el-button @click="visible = false">取消</el-button>
        </template>
    </el-dialog>
</template>
<script setup>
import https from '@/utils/https'
import { ElMessage } from 'element-plus'

const props = defineProps(["parent"])
const visible = defineModel("visible")
const fields = [
    {
        label: '分类编码', prop: 'code', required: true, autofocus: true, change: (val, form) => {
            if (!form.name) {
                form.name = val
            }
        }
    },
    { label: '分类名称', prop: 'name', required: true }
]
const form = defineModel({
    default: () => {
        return {}
    }
})
const formRef = ref()
const emits = defineEmits(["change"])

function doSave() {
    formRef.value.validate(resp => {
        if (!resp) {
            return
        }

        if (!form.value.id) {
            form.value.parentId = props.parent?.id || null
        }

        let url = '/etl/job-type/'
        let func
        if (form.value.id) {
            url += '/update'
            func = https.put(url, form.value)
        } else {
            url += '/add'
            func = https.post(url, form.value)
        }
        func.then(() => {
            emits('change')
            ElMessage.success('操作成功')
            form.value = {}
            visible.value = false
        })

    })
}
</script>

<style lang='scss' scoped></style>