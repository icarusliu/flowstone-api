<template>
    <!-- 实体新增或者编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="formModel.id ? '编辑' : '新增'" :width="width">
        <base-form :fields="fields" v-model="formModel" ref="formRef"></base-form>

        <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="doSave">保存</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { ElMessage } from 'element-plus'

const dialogVisible = defineModel("visible")
const formModel = defineModel()
const props = defineProps(["apiPrefix", "width", "fields"])
const formRef = ref()
const emits = defineEmits(["save"])

function doSave() {
    formRef.value.validate(result => {
        if (!result) {
            return;
        }

        entityApis.save(props.apiPrefix, formModel.value).then(() => {
            ElMessage.success('操作成功')
            dialogVisible.value = false
            emits('save', formModel.value)
        })
    })
}
</script>