<template>
    <!-- HTTP节点配置 -->
    <el-descriptions :column="1" border>
        <el-descriptions-item label="接入方">
            <el-select v-model="model.supplier" :disabled="readonly" placeholder="请选择接入方">
                <el-option v-for="option in suppliers" :label="option.name" :value="option.id"></el-option>
            </el-select>
        </el-descriptions-item>

        
        <el-descriptions-item label="认证规则" v-if="model.supplier">
            <el-select v-model="model.auth" :disabled="readonly" placeholder="请选择认证规则" value-key="name">
                <el-option v-for="option in authConfigs" :label="option.name" :value="option"></el-option>
            </el-select>
        </el-descriptions-item>

        <el-descriptions-item label="接口路径">
            <el-input v-model="model.path" :disabled="readonly" @change="pathChanged" placeholder="如包含路径参数，请使用：{...}进行处理"
                clearable />
        </el-descriptions-item>

        <el-descriptions-item label="接口类型">
            <el-radio-group v-model="model.method" :disabled="readonly" @change="refreshParams">
                <el-radio-button value="get" label="get"></el-radio-button>
                <el-radio-button value="delete" label="delete"></el-radio-button>
                <el-radio-button value="post" label="post"></el-radio-button>
                <el-radio-button value="put" label="put"></el-radio-button>
            </el-radio-group>
        </el-descriptions-item>

        <!-- <el-descriptions-item label="请求体" v-if="model.method == 'post' || model.method == 'put'">
            <el-link type="primary" @click="visible = true">编辑</el-link>
        </el-descriptions-item> -->
        <el-descriptions-item label="启用批量处理">
            <el-checkbox v-model="model.batch" :disabled="readonly" @change="refreshParams" />
            <div class="color-remark text-normal">
                默认情况下不启用批量处理，只会进行一次接口调用； <br />如果启用批量处理，那么节点接收的参数需要是一个<span class="color-red">列表类型</span>的参数，否则无法处理；
            </div>
        </el-descriptions-item>
    </el-descriptions>

    <div class="p-4">
        <node-params v-if="hasPathVariables" title="路径参数" v-model="model.pathVariables" :readonly="readonly"
            :inputParams="inputParams" :show-add="false" :show-operations="false" class="mb-4" :dag="dag"></node-params>

        <node-params v-if="model.method == 'get' || model.method == 'delete'" title="查询参数" v-model="model.params"
            :inputParams="inputParams" class="mb-4" :readonly="readonly" :dag="dag"
            :show-operations="!model.batch"></node-params>

        <node-params v-if="model.method == 'post' || model.method == 'put'" title="请求体" v-model="model.body" class="mb-4"
            :inputParams="inputParams" remark="参数编码如果为*，会将当前行解析的参数当成整个请求体" :show-operations="!model.batch"
            :readonly="readonly" :dag="dag"></node-params>

        <node-params title="请求头" v-model="model.headers" :readonly="readonly" :dag="dag"
            :inputParams="inputParams"></node-params>
    </div>

    <el-descriptions :column="1" border>
        <el-descriptions-item label="返回数据转换">
            <div class="v-center">
                <el-radio-group v-model="model.result.type" :disabled="readonly">
                    <el-radio-button label="原始" value="" />
                    <el-radio-button label="js" value="js" />
                    <el-radio-button label="groovy" value="groovy" />
                </el-radio-group>
                <el-link type="primary" v-if="model.result.type" @click="resultVisible = true" class="ml-4"
                    :disabled="readonly">编辑</el-link>
            </div>

        </el-descriptions-item>
    </el-descriptions>

    <div class="p-4 text-center">
        <el-button type="primary">测试</el-button>
    </div>

    <!-- 请求体配置 -->
    <!-- <ScriptDialog lang="javascript" v-model:visible="visible" v-model="model.js" :readonly="readonly"></ScriptDialog> -->

    <!-- 返回结果脚本配置 -->
    <ScriptDialog :lang="model.result.type == 'js' ? 'javascript' : 'groovy'" v-model:visible="resultVisible"
        v-model="model.result.converter" :readonly="readonly"></ScriptDialog>
</template>

<script setup>
import ScriptDialog from '@/components/script-dialog.vue';
import nodeParams from './components/node-params.vue';
import * as apiApis from '@/apis/api'

defineProps(["readonly", "dag", "inputParams"])

const reg = new RegExp(/.*\{.*\}/)
const hasPathVariables = computed(() => {
    if (!model.value.path) {
        return false
    }

    if (reg.test(model.value.path)) {
        return true
    }

    return false
})

const model = defineModel()
const resultVisible = ref(false)
const suppliers = ref([])

onMounted(() => {
    apiApis.getSuppliers().then(resp => {
        suppliers.value = resp || []
    })
})

const authConfigs = computed(() => {
    if (!model.value) {
        return []
    }
    let supplier = suppliers.value.filter(item => item.id == model.value.supplier)
    if (!supplier || !supplier.length) {
        return []
    }

    let authConfig = supplier[0].authConfig
    return JSON.parse(authConfig || '[]')
})

// 路径修改时，检查路径参数
function pathChanged(val) {
    if (!reg.test(val)) {
        return
    }

    let params = val.match(/(?<=\{)[^\}]*(?=\})/g)
    let pathVariables = []
    for (var i in params) {
        let param = params[i]
        pathVariables.push({
            key: param,
            type: 'request',
        })
    }

    model.value.pathVariables = pathVariables
}

// 修改是否批量标志
let saveParams = null
function refreshParams() {
    const method = model.value.method
    let key = (method == 'get' || method == 'delete') ? 'params' : 'body'
    const val = model.value.batch

    // 当设置成批量状态时，参数中需要增加一条key为*的记录
    if (!val) {
        // 设置成非批量，尝试恢复备份的数据
        model.value[key] = saveParams || model.value[key]
    } else {
        // 设置成批量
        saveParams = model.value[key]

        model.value[key] = [{
            key: '*',
            type: 'request'
        }]
    }
}
</script>