<template>
    <el-dialog v-model="visible" title="新增/编辑" width="600px" destroy-on-close>
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

const props = defineProps({
    parent: {type: Object},
    fields: {type: Array},
    apiPrefix: {type: String, required: true}
})
const visible = defineModel("visible")
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

        let url = props.apiPrefix
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