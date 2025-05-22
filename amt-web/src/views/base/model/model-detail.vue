<template>
    <full-panel v-model="visible" :title="'模型详情_' + form.name" @close="close">
        <el-tabs v-model="currentTab">
            <el-tab-pane label="基本信息" name="base">
                <base-info v-model="form" :editing="editing" ref="baseRef" />
            </el-tab-pane>

            <el-tab-pane label="列表视图配置" name="list" v-if="form.listConfig">
                <list-config :model="form" :editing="editing" />
            </el-tab-pane>

            <el-tab-pane label="表单视图配置" name="form" v-if="form.formConfig">
                <form-config :model="form" :editing="editing" />
            </el-tab-pane>

            <!-- <el-tab-pane label="数据维护" v-if="!editing && form.id" name="data">
                <data-explore :model="form" />
            </el-tab-pane> -->
        </el-tabs>

        <div class="text-center" v-if="currentTab != 'data'">
            <span v-if="!editing">
                <el-button type="primary" @click="startEdit">编辑</el-button>
                <el-button type="success" v-if="form.status != 1" @click="apply">应用</el-button>
                <el-button class="ml-2" @click="close">返回</el-button>
            </span>
            <span v-else>
                <el-button type="primary" @click="save">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
            </span>
        </div>
    </full-panel>
</template>
<script setup>
import baseInfo from './base-info.vue'
import listConfig from './list-config.vue'
import formConfig from './form-config.vue'
import { ElMessage,ElLoading } from 'element-plus'
import https from '@/utils/https'

import * as _ from 'lodash'
import fullPanel from '@/components/full-panel.vue'
import { onMounted } from 'vue'

const props = defineProps({
    modelId: { type: String },
    type: { type: Object }
})
const emits = defineEmits(['close'])
const form = ref({})
const editing = ref(false)
const innerFields = ['createTime', 'createUser', 'updateTime', 'updateUser']
const currentTab = ref('base')
const baseRef = ref()
const visible = defineModel(false)

onMounted(() => {
    if (props.modelId) {
        let loading = ElLoading.service({text: '加载中'})
        https.get('/base/model/detail/' + props.modelId).then(resp => {
            const model = resp

            // 处理默认值
            if (_.isEmpty(model.listConfig)) {
                model.listConfig = {
                    pagination: true,
                    withNew: true,
                    withDelete: true,
                    withEdit: true,
                    withImport: false,
                    withExport: false,
                    pageSize: 15
                }
            }

            if (_.isEmpty(model.formConfig)) {
                model.formConfig = {
                    displayType: 'drawer'
                }
            }

            // 需要查找模型对应的字段
            https.post('/base/model-field/query', { modelId: model.id }).then(resp => {
                model.fields = resp.map(item => {
                    if (item.code == 'id') {
                        item.primaryKey = true
                        item.readonly = true
                    } else if (innerFields.includes(item.code)) {
                        item.readonly = true
                    }

                    return item
                })
                form.value = model
            })
        }).finally(loading.close)
    } else {
        form.value = {
            status: 0,
            fields: [
                { code: 'id', name: 'id', primaryKey: true, dataType: 'varchar', dataConfig: '(64)', nullable: false, readonly: true},
                { code: 'createTime', name: '创建时间', dataType: 'datetime', dataConfig: '', defaultValue: 'current_timestamp()', readonly: true, nullable: true, },
                { code: 'createUser', name: '创建用户', dataType: 'varchar', dataConfig: '(255)', readonly: true, nullable: true },
                { code: 'updateTime', name: '更新时间', dataType: 'datetime', dataConfig: '', defaultValue: 'current_timestamp()', readonly: true, nullable: true },
                { code: 'updateUser', name: '更新用户', dataType: 'varchar', dataConfig: '(255)', readonly: true, nullable: true, },
            ],
            listConfig: {
                pagination: true,
                withNew: true,
                withDelete: true,
                withEdit: true,
                withImport: false,
                withExport: false,
                pageSize: 15
            },
            formConfig: {
                displayType: 'drawer'
            },
            listFields: [],
            formFields: []
        }
        editing.value = true
    }
})

function save() {
    console.log(baseRef.value.validate)
    baseRef.value.validate(resp => {
        if (!resp) {
            console.warn("校验未通过")
            return
        }

        let fields = form.value.fields
        if (fields) {
            for (var i in fields) {
                fields[i].sort = i.toString().padStart(3, '0')
            }
        }

        if (form.value.id) {
            https.put('/base/model/update', form.value).then(resp => {
                ElMessage.success('操作成功')
                editing.value = false;
                form.value.status = 2
            })
        } else {
            https.post('/base/model/add', form.value).then((resp) => {
                ElMessage.success('操作成功')
                form.value.id = resp.id
                form.value.fields = resp.fields
                editing.value = false;
            })
        }
    })
}

let savedData
function startEdit() {
    savedData = _.cloneDeep(form.value)
    editing.value = true
}

function cancelEdit() {
    if (!form.value.id) {
        return emits('close')
    }

    form.value = savedData
    editing.value = false
}

function apply() {
    https.get('/base/model/publish', { id: form.value.id }).then(() => {
        ElMessage.success('操作成功')
        form.value.status = 1
    })
}

function close() {
    emits('close')
}
</script>

<style lang='scss' scoped></style>