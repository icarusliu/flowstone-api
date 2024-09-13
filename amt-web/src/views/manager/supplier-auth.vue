<template>
    <el-page-header class="mb-4" :content="'接入方鉴权配置——' + supplier?.name" @back="back">
    </el-page-header>

    <div class="buttons mb-2">
        <el-button type="primary" @click="showNew">新增</el-button>
    </div>

    <base-table :fields="fields" :dataSupplier="getAuths" :pageable="false" ref="tableRef">
    </base-table>

    <div class="main-panel bg-white h-100 m-4" v-if="newVisible">
        <title-bar :title="isNew ? '新增鉴权' : '编辑鉴权'" />
        <base-form :fields="formFields" v-model="formData" ref="formRef"></base-form>
        <div class="buttons text-center">
            <el-button type="primary" @click="doSave">保存</el-button>
            <el-button @click="newVisible = false">取消</el-button>
        </div>
        <div class="remark-panel mt-4 mx-10" v-if="formData.type == 'static'">
            参数说明 <br/>
            以下调用我们所配置接口的叫请求方，我们真实调用的接口叫提供方
            <br/>
            <ul>
                <li>参数名：在调用接口时传输给提供方的参数名</li>
                <li>参数来源：如何解析参数实际值，可以指定参数来源是直接值，或者从前端请求我们接口时的请求头、请求体或者Cookie中获取参数</li>
                <li>
                    参数配置：配置参数值的获取方式，根据参数来源代表的含义不同
                    <ul>
                        <li>直接值：参数配置中填写直接的字符串或者数字</li>
                        <li>Cookie：参数配置中填写请求方请求过来的Cookie参数名称</li>
                        <li>请求头：参数配置中填写请求方请求过来的头部参数名称</li>
                        <li>请求参数：参数配置中填写请求方请求过来的参数（Body或者查询参数）名称</li>
                        <li>JS: 参数配置中填写JS脚本内容</li>
                        <li>Groovy: 参数配置中填写Groovy脚本内容</li>
                    </ul>
                </li>
                <li>目标方：配置解析到的参数，在调用接口提供方接口时，参数以何种形式传输过去，比如设置于Cookie中，或者置于请求头或者请求体中</li>
            </ul>
        </div>
        <div class="remark-panel mt-4 mx-10" v-else>
            目标参数说明 <br/>
            以下调用我们所配置接口的叫请求方，我们真实调用的接口叫提供方
            <br/>
            <ul>
                <li>参数名：在调用接口时传输给提供方的参数名</li>
                <li>参数来源：如何解析参数实际值，可以指定参数来源是直接值，或者从调用认证接口后返回的Cookie、Body或者Headers中解析，也可以通过js或者groovy脚本解析</li>
                <li>
                    参数配置：配置参数值的获取方式，根据参数来源代表的含义不同
                    <ul>
                        <li>直接值：参数配置中填写直接的字符串或者数字</li>
                        <li>Cookie：参数配置中填写认证结果中的Cookie参数名称</li>
                        <li>返回台头：参数配置中填写认证结果中返回的头部参数名称</li>
                        <li>返回体：参数配置中填写认证结果中返回的参数名称</li>
                        <li>JS: 参数配置中填写JS脚本内容</li>
                        <li>Groovy: 参数配置中填写Groovy脚本内容</li>
                    </ul>
                </li>
                <li>目标方：配置解析到的参数，在调用接口提供方接口时，参数以何种形式传输过去，比如设置于Cookie中，或者置于请求头或者请求体中</li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import * as entityApis from '@/apis/entity.js'
import { useRouter } from 'vue-router'
import { getAuthTableFields, authFormFields } from './fields'
import { ElMessageBox } from 'element-plus'
import * as _ from 'lodash'

const router = useRouter()
const fields = ref(getAuthTableFields({
    editAction: goEdit,
    deleteAction: doDelete
}))
const supplier = ref()
const authConfig = ref([])
const id = computed(() => {
    return router.currentRoute.value.params.id
})
const newVisible = ref(false)
const formRef = ref()
const tableRef = ref()

// 表单字段
const formFields = ref(authFormFields)
const formData = ref({
    params: [],
    type: 'static',
    dynamicType: 'get',
    dynamicParams: [],
    targetParams: []
})
let isNew = false;
let editIndex = 0

onMounted(() => {
    entityApis.findById('/base/supplier', id.value).then(resp => {
        supplier.value = resp
        authConfig.value = JSON.parse(resp.authConfig || '[]')
        tableRef.value.reload()
    })
})

function getAuths() {
    return authConfig.value
}

function showNew() {
    isNew = true
    newVisible.value = true
    formData.value = {
        params: [],
        type: 'static',
        dynamicType: 'get',
        dynamicParams: [],
        targetParams: []
    }
}

function doSave() {
    formRef.value.validate((result) => {
        if (!result) {
            return
        }

        if (isNew) {
            authConfig.value.push(formData.value)
        } else {
            authConfig.value.splice(editIndex, 1, formData.value)
        }

        supplier.value.authConfig = JSON.stringify(authConfig.value)
        entityApis.save('/base/supplier', supplier.value).then(() => {
            newVisible.value = false
            tableRef.value.reload()
        })
    })
}

function goEdit(row, index) {
    isNew = false
    editIndex = index
    formData.value = _.cloneDeep(row)
    newVisible.value = true
}

function doDelete(row, index) {
    ElMessageBox.confirm("确定删除当前记录？").then(() => {
        authConfig.value.splice(index, 1)
        supplier.value.authConfig = JSON.stringify(authConfig.value)
        entityApis.save('/base/supplier', supplier.value)
    })
}

function back() {
    router.back()
}
</script>

<style lang="scss" scoped>
.main-panel {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
}
</style>