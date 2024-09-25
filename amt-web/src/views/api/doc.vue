<template>
    <el-page-header content="接口说明" @back="goBack"></el-page-header>

    <el-descriptions border :column="2" class="mb-8 mt-4">
        <el-descriptions-item label="接口名称" :span=2>{{ apiInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="接口类型">{{ apiInfo.method }}</el-descriptions-item>
        <el-descriptions-item label="接口地址">{{ '/dua/' + apiInfo.path }}</el-descriptions-item>

        <el-descriptions-item label="输入参数" :span="2">
            <div v-if="!apiConfig.inputParams">未定义</div>
            <div class="p-4" v-else>
                <base-table :dataSupplier="() => apiConfig.inputParams" :fields="inputFields" :pageable="false">
                </base-table>
            </div>
        </el-descriptions-item>

        <el-descriptions-item label="输出参数" :span="2">
            <div v-if="!apiConfig.output">未定义</div>
            <div class="p-4" v-else>
                <base-table :dataSupplier="() => apiConfig.output" :fields="outputFields" :pageable="false">
                </base-table>
            </div>
        </el-descriptions-item>

        <el-descriptions-item label="调用示例" :span="2">
            <div class="p-4">
                <div>{{ curlTest }}</div>
                <div class="mt-2">实际调用中请替换token</div>
            </div>
        </el-descriptions-item>

        <el-descriptions-item label="输出示例" :span="2">
            <monacoEditor language="json" v-model="apiConfig.outputExample" :editorOptions="{ readOnly: true }"
                height="500px" />
        </el-descriptions-item>
    </el-descriptions>
</template>

<script setup>
import monacoEditor from '@/components/monaco-editor.vue';
import * as entityApis from '@/apis/entity'
import { useRouter } from 'vue-router'

const apiInfo = ref({})
const apiConfig = ref({})
const router = useRouter()

const id = computed(() => {
    return router.currentRoute.value.query.id
})

const host = computed(() => {
    let href = window.location.href.replace('http://', '')
    let idx = href.indexOf('/')
    href = href.substring(0, idx)
    return href
})
const curlTest = computed(() => {
    let {method, path} = apiInfo.value
    let {testData} = apiConfig.value

    let result = `curl -X ${method} http://${host.value}/api/dua/${path}`

    if (!testData) {
        return result
    }

    if (method == 'post') {
        if (testData.body) {
            result += ` -d '${testData.body}' -H 'Content-Type: application/json'`
        } 
    } else {
        if (testData.queryParams && testData.queryParams.length) {
            let str = testData.queryParams.map(({code, value}) => code + '=' + value)
                .reduce((s1, s2) => s1 + '&' + s2)
            result += '?' + str
        }
    }

    result += ` -H 'Authorization: token'`

    return result;
})

const outputFields = ref([
    { label: '参数编码', prop: 'code', disabled: (val, row) => val == '*', width: '400px' },
    {
        label: '参数名', prop: 'name', width: '200px'
    },
    {
        label: '参数类型', prop: 'type', width: '120px', type: 'select', options: [
            { label: '无限制', value: 'unknown' },
            { label: '字符串', value: 'string' },
            { label: '整数', value: 'int' },
            { label: '小数', value: 'float' },
            { label: '数组', value: 'array' },
            { label: '对象', value: 'object' }
        ], change: (val, row) => {
            // 非对象、数组类型时需要清空children
            if (val == 'array') {
                row.children = [{ code: '*', name: '存储对象类型', type: 'string', readonly: true }]
                return
            }

            delete row.children
        }
    },
    { label: '备注', prop: 'remark' }
])

const inputFields = ref([
    { label: '参数编码', prop: 'code', width: '400px' },
    {
        label: '参数名', prop: 'name', width: '200px'
    },
    { label: '参数类型', prop: 'type', width: '120px', type: 'select' },
    { label: '必需', prop: 'required', width: '60px', type: 'checkbox' },
    { label: '默认值', prop: 'default', width: '120px' },
    { label: '备注', prop: 'remark' }
])

onMounted(() => {
    getApiInfo()
})

// 获取接口详情
function getApiInfo() {
    entityApis.findById('/base/api', id.value).then(resp => {
        apiInfo.value = resp
        let content = resp.content
        apiConfig.value = JSON.parse(content)
    })
}

function goBack() {
    router.back()
}
</script>

<style lang="scss" scoped>
:deep() {
    .el-descriptions__label.el-descriptions__cell.is-bordered-label {
        width: 160px;
    }
}
</style>