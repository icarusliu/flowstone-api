<template>
    <!-- 接口测试 -->
    <el-form labelWidth="120px">
        <el-form-item label="请求类型">
            <el-radio-group v-model="formModel.method">
                <el-radio label="GET" value="get" />
                <el-radio label="POST" value="post" />
            </el-radio-group>
        </el-form-item>

        <el-form-item label="查询参数">
            <MonacoEditor height="200px" v-model="formModel.queryParams" language="json"/>
        </el-form-item>

        <el-form-item label="请求体" v-if="formModel.method == 'post'">
            <MonacoEditor height="200px" v-model="formModel.body" language="json"/>
        </el-form-item>

        <el-form-item label="请求头">
            <MonacoEditor height="200px" v-model="formModel.headers" language="json"/>
        </el-form-item>

        <div class="text-center mb-4">
            <el-button type="primary" @click="doTest">测试</el-button>
        </div>

        <template v-if="testResult">
            <el-form-item label="测试结果">
                <div v-if="testFailed">失败</div>
                <div v-else>成功</div>
            </el-form-item>

            <el-form-item label="处理结果">
                <MonacoEditor height="600px" v-model="testResult" language="json"/>
            </el-form-item>
        </template>
    </el-form>
</template>

<script setup>
import * as _ from 'lodash'
import * as apiApis from '@/apis/api'
import { ElMessage } from 'element-plus';
import MonacoEditor from '@/components/monaco-editor.vue'

const initModel = {
    method: 'get',
    queryParams: {},
    body: {},
    headers: {}
}
const formModel = ref(initModel)
const props = defineProps(["apiInfo", "editing"])
const testResult = ref()
const testFailed = ref(false)

// 解析保存的测试数据
watch(() => props.apiInfo.content, (val) => {
    if (!val) {
        return
    }

    const content = JSON.parse(props.apiInfo.content)
    formModel.value = content.testData || _.cloneDeep(initModel)
}, {
    immediate: true
})

// 进行接口测试
function doTest() {
    let params = formModel.value.queryParams
    if (formModel.value.method == 'post') {
        params = formModel.value.body
    }
    apiApis.testApi(formModel.value.method, props.apiInfo.path, params).then(resp => {
        ElMessage.success('测试成功')
        testResult.value = JSON.stringify(resp)
        testFailed.value = false
    }).catch(err => {
        testFailed.value = true
        testResult.value = JSON.stringify(err)
    })
}
</script>