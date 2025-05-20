<template>
    <!-- 字典详情 -->
    <fullpanel title="字典详情" @close="close" v-model="visible">
        <base-form :fields="fields" v-model="model" :readonly="!editing" ref="formRef">
            <template #default>
                <el-form-item label="字典项清单" v-if="model.type != 'sql'" :rules="itemsRules" required prop="items">
                    <edit-table :fields="itemFields" v-model="model.items" :showSortButtons="true" :readonly="!editing" />
                </el-form-item>
                <el-col :span="24" v-else>
                    <el-form-item label="SQL配置" prop="sql" :rules="[{required: true, message: 'SQL配置不能为空'}]">
                        <monaco-editor lang="javascript" height="30vh" v-model="model.metadata.sql" />
                        <div class="remark">
                            SQL语句需返回name/value两个字段，其中name表示字典项名称，value表示其对应的值；
                        </div>
                    </el-form-item>
                </el-col>
            </template>
        </base-form>

        <div class="text-center">
            <span v-if="!editing">
                <el-button type="primary" @click="edit">编辑</el-button>
                <el-button @click="close">返回</el-button>
            </span>
            <span v-else>
                <el-button type="primary" @click="save">保存</el-button>
                <el-button @click="cancel">取消</el-button>
            </span>
        </div>
    </fullpanel>
</template>
<script setup>
import fullpanel from '@/components/full-panel.vue'
import editTable from '@/components/edit-table/index.vue'
import monacoEditor from '@/components/monaco-editor.vue'
import https from '@/utils/https'
import { onMounted } from 'vue'
import * as _ from 'lodash'
import { ElEmpty, ElMessage } from 'element-plus'

const fields = [
    { label: '字典编码', prop: 'code', required: true, validation: {
        trigger: 'blur',
        validator: (val, rule, callback) => {
            if (!val) {
                return callback(new Error('字典编码不能为空'))
            }

            https.post('/sys/dict/query', {code: model.value.code}).then(resp => {
                if (!resp?.length) {
                    return callback()
                }

                if (resp[0].id == model.value.id) {
                    return callback()
                }

                return callback(new Error('字典编码已存在'))
            })
        }
    } },
    { label: '字典名称', prop: 'name', required: true },
    { label: '字典备注', prop: 'remark', type: 'textarea' },
    {
        label: '字典类型', prop: 'type', type: 'radioGroup', options: [
            { label: '标准字典', value: 'items' },
            { label: 'SQL字典', value: 'sql' }
        ]
    }
]
const model = ref({
    type: 'items',
    items: [],
    metadata: {}
})
const emits = defineEmits(['close', 'reload'])
const props = defineProps({
    item: Object
})
const editing = ref(false)
const itemFields = [
    { label: '字典项名称', prop: 'name', width: '300px' },
    { label: '字典项值', prop: 'value', width: '300px' },
    { label: '字典项备注', prop: 'remark' }
]
const itemsRules = [{
    validator: (val, rule, callback) => {
        let form = model.value
        if (!form.items || !form.items.length) {
            return callback(new Error('字典项不能为空'))
        } 

        for (var i in form.items) {
            let item = form.items[i]
            if (!item.name || (!item.value && item.value != 0)) {
                return callback(new Error('字典项名称或值不能为空'))
            }
        }

        callback()
    }
}]
const visible = defineModel()

onMounted(() => {
    if (!props.item) {
        editing.value = true
    } else {
        let item = _.cloneDeep(props.item)
        if (!item.metadata) {
            item.metadata = {}
        }
        if (!item.items) {
            item.items = []
        }
        model.value = item
    }
})

function close() {
    visible.value = false
    emits('close')
}

function cancel() {
    if (!model.id) {
        close()
    }
    editing.value = false
}

const formRef = ref()
function save() {
    formRef.value.validate(val => {
        if (!val) {
            return
        }

        // 检查字典项是否有值
        let type = model.value.type 
        if (type == 'sql') {
            
        }

        if (model.value.id) {
            https.put('/sys/dict/update', model.value).then(resp => {
                ElMessage.success('操作成功')
                emits('reload')
                editing.value = false
            })
        } else {
            https.post('/sys/dict/add', model.value).then(resp => {
                model.value.id = resp.id
                ElMessage.success('操作成功')
                emits('reload')
                editing.value = false
            })
        }
    })

}

let bakModel
function edit() {
    bakModel = _.cloneDeep(model.value)
    editing.value = true
}
</script>

<style lang='scss' scoped></style>