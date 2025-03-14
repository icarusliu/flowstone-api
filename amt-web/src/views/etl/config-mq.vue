<template>
    <el-form ref="formRef" :model="model">
        <el-row :gutter="16">
            <el-col :span="24">
                <el-form-item label="数据类型" required class="field" prop="type">
                    <el-radio-group v-model="model.type" :disabled="disabled">
                        <el-radio-button value="mqtt">MQTT</el-radio-button>
                        <el-radio-button value="kafka">Kafka</el-radio-button>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="连接串" required class="field" prop="url">
                    <el-input v-model="model.url" placeholder="请输入连接信息"  :disabled="disabled"/>
                </el-form-item>
            </el-col>

            <el-col :span="12">
                <el-form-item label="主题" required class="field" prop="topic">
                    <el-input v-model="model.topic" placeholder="请输入主题"  :disabled="disabled"/>
                </el-form-item>
            </el-col>
        </el-row>


        <el-row :gutter="16">
            <el-col :span="6">
                <el-form-item label="数据预加工" class="field">
                    <el-radio-group v-model="model.scriptType" @change="changeScriptType" :disabled="disabled">
                        <el-radio-button value="groovy">Groovy</el-radio-button>
                        <el-radio-button value="javascript">Js</el-radio-button>
                        <!-- <el-radio-button value="flink">Flink</el-radio-button> -->
                    </el-radio-group>

                    <el-link type="primary" class="ml-4" @click="showScript">{{ disabled ? '查看' : '编辑' }}</el-link>
                </el-form-item>
            </el-col>

            <el-col :span="6">
                <el-form-item label="数据计算" class="field">
                    <el-radio-group v-model="model.computeScriptType" @change="changeComputeScriptType" :disabled="disabled">
                        <el-radio-button value="groovy">Groovy</el-radio-button>
                        <el-radio-button value="javascript">Js</el-radio-button>
                        <!-- <el-radio-button value="flink">Flink</el-radio-button> -->
                    </el-radio-group>

                    <el-link type="primary" class="ml-4" @click="showComputeScriptType">{{ disabled ? '查看' : '编辑' }}</el-link>
                </el-form-item>
            </el-col>

            <el-col :span="12">
                <el-form-item>
                    <template #label>
                        <span class="v-center">
                            <el-popover width="500px">
                                <template #reference>
                                    <el-icon color="blue" class="mr-1">
                                        <Warning />
                                    </el-icon>
                                </template>
                                <ul>
                                    <li>用于根据上一条记录及当前记录进行某种计算时使用，一般用于记录两次记录某个字段的差值，如算设备运行时长等；</li>
                                    <li>启用缓存将会消耗更多的服务器资源及引起吞吐率下降</li>
                                    <li>启用缓存后，默认按队列缓存上一条记录；如果想要以其它方式比如按设备缓存，需要指定缓存关键字段，比如deviceId</li>
                                </ul>
                            </el-popover>
                            <span>缓存上一条记录</span>
                        </span>
                    </template>
                    <el-switch v-model="model.cacheLast"  :disabled="disabled"/>

                    <span class="cache-key d-flex" v-if="model.cacheLast">
                        <label>缓存关键字段</label>
                        <el-input v-model="model.cacheKey" placeholder="请输入缓存关键字段，比如设备id" :disabled="disabled" />
                    </span>
                </el-form-item>
            </el-col>

            <el-col :span="24">
                <el-form-item required prop="destSql">
                    <template #label>
                        存储脚本（SQL插入语句）
                        <el-link type="primary" @click="initDestSql" :disabled="disabled">添加示例</el-link>
                    </template>
                    <monacoEditor height="47vh" v-model="model.destSql" language="sql" :editorOptions="{ readOnly: disabled }" />
                </el-form-item>
            </el-col>
        </el-row>

        <ScriptDialog v-model:visible="visible" v-model="model.script" :lang="model.scriptType" :readonly="disabled">
            <template #header>
                <el-link type="primary" @click="initScript" :disabled="disabled">添加示例脚本</el-link>
            </template>
        </ScriptDialog>

        <ScriptDialog v-model:visible="computeScriptVisible" v-model="model.computeScript" :lang="model.computeScriptType" :readonly="disabled">
            <template #header>
                <el-link type="primary" @click="initComputeScript" :disabled="disabled">添加示例脚本</el-link>
            </template>
        </ScriptDialog>
    </el-form>
</template>
<script setup>
import monacoEditor from '@/components/monaco-editor.vue'
import ScriptDialog from '@/components/script-dialog.vue';
import { onMounted } from 'vue';

const props = defineProps({
    dses: { type: Array, required: true },
    disabled: { type: Boolean }
})
const model = defineModel()
const visible = ref(false)

onMounted(() => {
    if (!model.value.scriptType) {
        model.value.scriptType = 'groovy'
    }

    if (!model.value.type) {
        model.value.type = 'mqtt'
    }

    if (!model.value.computeScriptType) {
        model.value.computeScriptType = "groovy"
    }
})

function showScript() {
    visible.value = true
}

function changeScriptType() {
    model.value.script = ""
}

function initScript() {
    let scriptType = model.value.scriptType
    if (scriptType == 'javascript') {
        model.value.script = `// 数据转换函数，将接收到的消息内容转换成可插入数据库的数据; val即为接收到的消息内容
// 注意返回数据只能是数组或者对象；如果是数组时，将会将数组内的每一个对象传给最终的存储SQL执行；如果是对象，会将对象直接传给存储SQL执行；如果定义了数据计算函数，相应数据将会拆成对象后传给数据计算函数执行再插入数据库
function func(val) {
    // 假设内容是{a: 1, b: 2}，需要将其转换成[{field: 'a', value: 1}, {field: 'b', value: 2}]然后存储到数据库或者传给计算函数进行计算
    if (!val) {
        // 返回null时，将会抛弃结果
        return null;
    }

    const result = []
    for (var i in val) {
        result.push({
            field: i,
            value: val[i]
        })
    }

    return result
}
`
    } else if (scriptType == 'groovy') {
        model.value.script = `// 数据转换函数，将接收到的消息内容转换成可插入数据库的数据; val即为接收到的消息内容
// 注意返回数据只能是数组或者对象；如果是数组时，将会将数组内的每一个对象传给最终的存储SQL执行；如果是对象，会将对象直接传给存储SQL执行；如果定义了数据计算函数，相应数据将会拆成对象后传给数据计算函数执行再插入数据库
def func(val) {
    // 假设内容是{a: 1, b: 2}，需要将其转换成[{field: 'a', value: 1}, {field: 'b', value: 2}]然后存储到数据库或者传给计算函数进行计算
    if (val == null || val.isEmpty()) {
        return []
    }
    def result = []
    val.each { key, value ->
        result << [field: key, value: value]
    }
    return result
}
 `
    } else if (scriptType == 'flink') {
        model.value.script = `
select 
    
`
    }
}

function initDestSql() {
    model.value.destSql = `-- 假设写入表output_table字段为field, value；数据转换后结果为[{field: 'a', value: 1}, {field: 'b', value: 2}]
insert into output_table(field, value, create_time) values(#{field}, #{value}, current_timestamp())
`
}

const formRef = ref()
function validate(callback) {
    formRef.value.validate(callback)
}

const computeScriptVisible = ref(false)

function showComputeScriptType() {
    computeScriptVisible.value = true
}

function changeComputeScriptType() {
    model.value.computeScript = ""
}

function initComputeScript() {
    let type = model.value.computeScriptType
    if (type == 'javascript') {
        model.value.computeScript = `// 数据计算函数，主要用于需要使用上一次记录进行数据计算的情况； val为本次处理的记录, lastVal为上次收到的记录，注意lastVal可能为空；
// 注意接收参数及返回数据都是且只能是对象，将会传给插入语句直接执行
function func(val, lastVal) {
    // 假设内容是{field: 'a', value: 1, status: 1}，需要增加一个额外的字段，来转换状态名称，可以处理如下
    if (!val) {
        // 返回null时，将会抛弃结果
        return null;
    }

    let status = val.status 
    val.statusName = status == 0 ? '失败' : '成功'

    return val
}
`
    } else if (type == 'groovy') {
        model.value.computeScript = `// 数据计算函数，主要用于需要使用上一次记录进行数据计算的情况； val为本次处理的记录, lastVal为上次收到的记录，注意lastVal可能为空；
// 注意接收参数及返回数据都是且只能是对象，将会传给插入语句直接执行
def func(val, lastVal) {
    // 假设内容是{field: 'a', value: 1, status: 1}，需要增加一个额外的字段，来转换状态名称，可以处理如下
    if (val == null || val.isEmpty()) {
        return val
    }
    if (val.hasProperty('status')) {
        val.statusName = val.status == 0? '成功' : '失败'
    }
    return val
}`
    }
}

defineExpose({
    validate
})
</script>

<style lang='scss' scoped>
.cache-key {
    margin-left: 12px;

    label {
        width: 120px;
    }
}
</style>