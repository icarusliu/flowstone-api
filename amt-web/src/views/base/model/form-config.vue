<template>
    <el-form labelWidth="140px" :model="model.formConfig">
        <el-form-item label="新增展示类型">
            <el-select v-model="model.formConfig.displayType" :disabled="!editing">
                <el-option value="drawer" label="抽屉"></el-option>
                <el-option value="dialog" label="弹窗"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="表单字段配置">
            <edit-table v-model="model.formFields" :fields="fields" :showNew="false" :withDelete="false" :border="true"
                :readonly="!editing">
                <template #appendButtons>
                    <el-button type="primary" plain :disabled="!editing" @click="syncFields">从模型同步</el-button>
                </template>
            </edit-table>
        </el-form-item>
    </el-form>
</template>
<script setup>
import editTable from '@/components/edit-table/index.vue'
import https from '@/utils/https'

const props = defineProps({
    model: { type: Object, required: true },
    editing: { tupe: Boolean, default: false }
})

const fields = ref([
    { label: '标签名称', prop: 'name' },
    { label: '是否隐藏', prop: 'hide', type: 'checkbox', width: '100px', align: 'center' },
    {
        label: '数据类型', prop: 'dataType', width: '200px', converter: val => {
            if (val == 'date') {
                return '日期'
            } else if (val == 'datetime') {
                return '日期时间'
            } else if (val == 'time') {
                return '时间'
            } else if (val == 'bigint') {
                return '数字'
            } else if (val == 'numeric') {
                return '小数'
            }

            return '字符串'
        }
    },
    {
        label: '组件', prop: 'cmp', type: 'select', options: [
            { label: '文本输入', value: 'input' },
            { label: '数字', value: 'number' }, 
            { label: '下拉', value: 'select' },
            { label: '单选', value: 'radio' },
            { label: '复选', value: 'checkbox' },
            { label: '长文本', value: 'textarea' },
            { label: '开关', value: 'switch' },
            { label: '日期选择', value: 'datePicker' },
            { label: '日期时间', value: 'dateTime' },
            { label: '时间', value: 'time' }
        ]
    },
    {
        label: '选项字典', prop: 'dictCode', type: 'select', disabled: (val, model) => {
            let cmp = model.cmp
            return !['radio', 'select', 'checkbox'].includes(cmp)
        }, options: () => {
            return https.post('/base/dict/query').then(resp => {
                return resp.map(item => {
                    return {
                        label: item.name,
                        value: item.code
                    }
                })
            })
        }
    },
    { label: '默认值', prop: 'defaultValue' },
    { label: '是否必填', prop: 'required', type: 'checkbox', width: '100px', align: 'center' },
    { label: '最小长度/最小值', prop: 'min' },
    { label: '最大长度/最大值', prop: 'max' },

])
const innerFields = ['id', 'createTime', 'createUser', 'updateTime', 'updateUser']

function syncFields() {
    // 已存在的配置，如果字段配置已存在，以已存在的为准
    const existFields = props.model.formFields || []
    const fieldMap = {}
    existFields.forEach(field => fieldMap[field.code] = field)

    props.model.formFields = props.model.fields
        .filter(item => !innerFields.includes(item.code))
        .map(item => {
            const code = item.code
            if (fieldMap[code]) {
                return fieldMap[code]
            }

            let newItem = {}
            newItem.name = item.name
            newItem.code = item.code
            newItem.dataType = item.dataType
            newItem.hide = innerFields.includes(item.code)
            newItem.dictCode = item.dictCode

            // 编辑组件
            let dataType = item.dataType
            if (dataType == 'bigint') {
                newItem.cmp = 'number'
            } else if (dataType == 'date') {
                newItem.cmp = 'datePicker'
            } else if (dataType == 'datetime') {
                newItem.cmp = 'dateTime'
            } else {
                newItem.cmp = 'input'
            }
            newItem.defaultValue = item.defaultValue
            newItem.required = !item.NonNullable

            // 解析最大最小值
            let dataConfig = item.dataConfig
            console.log(item.dataConfig);
            if (dataConfig) {
                dataConfig = dataConfig.replace('(', '').replace(')', '')
                let arr = dataConfig.split(',')
                if (dataType == 'varchar') {
                    newItem.max = arr[0]
                }
            }

            return newItem;
        })
}
</script>

<style lang='scss' scoped></style>