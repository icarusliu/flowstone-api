<template>
    <full-panel :title="model.id ? (editing ? '编辑任务' : '查看详情') : '新增任务'" @close="close">
        <el-form :model="model" label-position="top" ref="formRef" :rules="rules">
            <el-tabs v-model="currentTab">
                <el-tab-pane label="基本信息" name="base">
                    <el-form-item label="任务分类" required prop="typeId">
                        <tree-select v-model="model.typeId" :options="loadTypes" :disabled="!editing" />
                    </el-form-item>
                    <el-form-item label="任务编码" required prop="code">
                        <el-input v-model="model.code" :disabled="!editing" @change="changeCode"/>
                    </el-form-item>
                    <el-form-item label="任务名称" required prop="name">
                        <el-input v-model="model.name" :disabled="!editing" />
                    </el-form-item>

                    <el-form-item label="任务类型" required prop="type">
                        <el-radio-group v-model="model.type" :disabled="!editing" @change="typeChanged">
                            <el-radio-button value="sync">数据抽取</el-radio-button>
                            <el-radio-button value="sql">数据加工</el-radio-button>
                            <el-radio-button value="mq">实时数据</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="触发规则" required prop="autoTrigger">
                        <el-radio-group v-model="model.autoTrigger" :disabled="!editing || model.type == 'mq'">
                            <el-radio-button :value="true">依赖触发</el-radio-button>
                            <el-radio-button :value="false">定时触发</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="定时执行规则" required prop="cron" v-if="!model.autoTrigger">
                        <cron-editor v-model="model.cron" :disabled="!editing" class="cron-editor" />
                    </el-form-item>
                </el-tab-pane>
                <el-tab-pane label="运行信息" name="run">
                    <sync-config v-if="model.type == 'sync'" v-model="model.config" :dses="dses" ref="syncRef" :disabled="!editing">
                    </sync-config>
                    <sql-config v-else-if="model.type == 'sql'" v-model="model.config.nodes[0].config" :dses="dses" ref="sqlRef"
                        :disabled="!editing" />
                    <mq-config v-else v-model="model.config" :dses="dses" ref="mqRef" :disabled="!editing" />
                </el-tab-pane>
                <el-tab-pane label="任务测试" name="test" v-if="!editing && model.type != 'mq'">
                    <job-test :job="model" />
                </el-tab-pane>
            </el-tabs>

        </el-form>
        <div class="text-center" v-if="currentTab != 'test'">
            <span v-if="editing">
                <el-button type="primary" @click="doSave">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
            </span>
            <span v-else>
                <el-button type="primary" @click="startEdit">编辑</el-button>
                <el-button @click="close" class="ml-2">关闭</el-button>
            </span>
        </div>
    </full-panel>
</template>
<script setup>
import treeSelect from '@/components/base-tree-select.vue'
import https from '@/utils/https'
import syncConfig from './config-sync.vue'
import sqlConfig from './config-sql.vue'
import mqConfig from './config-mq.vue'
import * as uuid from 'uuid'
import { ElMessage } from 'element-plus'
import * as _ from 'lodash'
import fullPanel from '@/components/full-panel.vue'
import cronEditor from '@/components/cron-editor.vue'
import jobTest from './job-test.vue'

const editing = ref(true)
const emits = defineEmits(['close'])
const model = ref({
    autoTrigger: true,
    type: 'sql',
    version: 1,
    config: {
        nodes: [{
            // 暂时只处理一个节点的情况，考虑后续扩展
            id: uuid.v4(),
            type: 'sql',
            config: {
            }
        }]
    }
})
const props = defineProps({
    item: { type: Object },
    type: { type: Object }
})
const dses = ref([])
const currentTab = ref('base')
const rules = ref({
    code: {
        validator: (rule, val, callback) => {
            if (!val) {
                return callback(new Error('编码不能为空'))
            }

            // 校验编码是否重复
            https.post('/etl/job/query', { code: val }).then(resp => {
                if (!resp || !resp.length) {
                    return callback()
                }

                if (resp[0].id != model.value.id) {
                    return callback(new Error('编码已存在，请重新输入'))
                }

                return callback()
            })
        },
        trigger: 'blur'
    }, name: {
        validator: (rule, val, callback) => {
            if (!val) {
                return callback(new Error('名称不能为空'))
            }

            https.post('/etl/job/query', { name: val }).then(resp => {
                if (resp && resp.length && resp[0].id != model.value.id) {
                    return callback(new Error('名称已存在，请重新输入'))
                }

                return callback()
            })
        }
    },
    typeId: { required: true, message: '分类不能为空' },
})

onMounted(() => {
    if (props.type) {
        model.value.typeId = props.type.id
    }

    if (props.item) {
        model.value = props.item
        editing.value = false
    }

    https.post('/base/ds/query').then(resp => {
        dses.value = resp
    })
})

function changeCode() {
    if (!model.value.name) {
        model.value.name = model.value.code
    }
}

function loadTypes() {
    return https.get('/etl/job-type/tree');
}

function close() {
    emits('close')
}

function typeChanged() {
    if (model.value.type == 'sync') {
        // 如果是数据抽取，一定是要指定时间
        model.value.autoTrigger = false
    } else if (model.value.type == 'mq') {
        // 实时数据一定是依赖触发
        model.value.autoTrigger = true
    }
}

let bakModel
function startEdit() {
    bakModel = _.cloneDeep(model.value)
    editing.value = true
    if (currentTab.value == 'test') {
        currentTab.value = 'run'
    }
}

function cancelEdit() {
    if (!model.value.id) {
        return close()
    }

    model.value = bakModel
    editing.value = false
}

const formRef = ref()
const syncRef = ref()
const sqlRef = ref()
const mqRef = ref()
function doSave() {
    formRef.value.validate(res => {
        if (!res) {
            currentTab.value = 'base'
            return
        }

        let validate

        if (model.value.type == 'sync') {
            validate = syncRef.value.validate
        } else if (model.value.type == 'sql') {
            validate = sqlRef.value.validate
        } else if (model.value.type == 'mq') {
            validate = mqRef.value.validate
        }

        validate(res => {
            if (!res) {
                currentTab.value = 'run'
                return
            }

            save()
        })
    })
}

function save() {
    let params = model.value
    let id = params.id
    if (id) {
        https.put('/etl/job/update', params).then(() => {
            ElMessage.success('保存成功')
            bakModel = model.value
            editing.value = false
        })
    } else {
        https.post('/etl/job/add', params).then((resp) => {
            model.value.id = resp.id
            bakModel = model.value
            editing.value = false
            ElMessage.success('保存成功')
        }).catch(err => {
            console.error(err)
        })
    }

}
</script>

<style lang='scss' scoped>
.top-bar {
    align-items: center;
    border-bottom: 1px solid #eee;
    padding-bottom: 8px;

    .return {
        display: flex;
        align-items: center;
    }

    .title {
        font-size: 14px;
        font-weight: bold;
    }
}

.cron-editor {
    width: 500px;
}
</style>