<template>
    <el-form labelWidth="140px" :model="model.listConfig">
        <el-form-item label="列表功能配置">
            <el-checkbox v-model="model.listConfig.pagination" :disabled="!editing">启用分页</el-checkbox>
            <el-checkbox v-model="model.listConfig.withNew" :disabled="!editing">启用新增</el-checkbox>
            <el-checkbox v-model="model.listConfig.withEdit" :disabled="!editing">启用编辑</el-checkbox>
            <el-checkbox v-model="model.listConfig.withDelete" :disabled="!editing">启用删除</el-checkbox>
            <el-checkbox v-model="model.listConfig.withImport" :disabled="!editing">启用导入</el-checkbox>
            <el-checkbox v-model="model.listConfig.withExport" :disabled="!editing">启用导出</el-checkbox>
        </el-form-item>
        <el-form-item label="每页记录数">
            <el-input placeholder="每页记录数" type="number" v-model="model.listConfig.pageSize" :disabled="!editing" />
        </el-form-item>
        <el-form-item label="列表字段配置">
            <edit-table v-model="model.listFields" :fields="fields" :showNew="false" :withDelete="false" :border="true" :readonly="!editing"
                :showOperations="false">
                <template #appendButtons>
                    <el-button type="primary" plain :disabled="!editing" @click="syncFields">从模型同步</el-button>
                </template>
            </edit-table>
        </el-form-item>
    </el-form>
</template>
<script setup>
import editTable from '@/components/edit-table/index.vue'
import * as _ from 'lodash'
import https from '@/utils/https'

const props = defineProps({
    model: { type: Object, required: true },
    editing: { tupe: Boolean, default: false }
})

const fields = ref([
    { label: '名称', prop: 'name', },
    {
        label: '数据类型', prop: 'dataType', width: '100px', type: 'text', align: 'center', converter: val => {
            switch (val) {
                case "varchar": return "字符串";
                case "bigint": return "整数";
                case "numeric": return "小数";
                case "date": return "日期";
                case "datetime": return "日期时间";
                case "time": return "时间";
                default: return "字符串"
            }
        }
    },
    { label: '展示宽度', prop: 'width', width: '100px' },
    { label: '是否隐藏', prop: 'hide', type: 'checkbox', width: '100px', align: 'center' },
    { label: '是否查询', prop: 'asQuery', type: 'checkbox', width: '100px', align: 'center' },
    {
        label: '查询组件', prop: 'queryCmp', type: 'select', options: [
            { label: '文本框', value: 'input' },
            { label: '下拉', value: 'select' },
            { label: '日期选择', value: 'datePicker' },
            { label: '日期区间', value: 'dateRange' },
        ]
    },
    {
        label: '关联字典', prop: 'dictCode', type: 'select', options: () => {
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
    {
        label: '查询类型', prop: 'queryType', type: 'select', options: [
            { label: '精确匹配', value: 'eq' },
            { label: '模糊匹配', value: 'like' }
        ]
    },
    { label: '查询默认值', prop: 'queryDefValue' },
    { label: '默认排序', prop: 'sort', type: 'checkbox', width: '100px', align: 'center' },
    { label: '排序类型', prop: 'sortType', type: 'select', options: [{ label: '升序', value: 'asc' }, { label: '降序', value: 'desc' }], width: '100px' },
    // { label: '渲染函数', prop: 'render', type: 'checkbox', width: '100px', align: 'center' },
    { label: '是否导入', prop: 'importable', type: 'checkbox', width: '100px', align: 'center' },
    { label: '是否导出', prop: 'exportable', type: 'checkbox', width: '100px', align: 'center' }
])
const innerFields = ['id', 'createTime', 'createUser', 'updateTime', 'updateUser']

function syncFields() {
    // 已存在的配置，如果字段配置已存在，以已存在的为准
    const existFields = props.model.listFields || []
    const fieldMap = {}
    existFields.forEach(field => fieldMap[field.code] = field)

    props.model.listFields = props.model.fields.map(item => {
        const code = item.code
        if (fieldMap[code]) {
            return fieldMap[code]
        }

        let newItem = {}
        newItem.name = item.name
        newItem.code = item.code
        newItem.dataType = item.dataType
        newItem.hide = item.code == 'id'
        newItem.asQuery = false
        newItem.dictCode = item.dictCode

        if (newItem.code == 'updateTime') {
            newItem.sort = true
            newItem.sortType = 'desc'
        } else {
            newItem.sortType = 'asc'
        }

        if (innerFields.includes(item.code)) {
            if (item.code == 'id') {
                newItem.width = '240px'
            } else {
                newItem.width = '200px'
            }
        }
        newItem.queryCmp = item.dataType == 'date' || item.dataType == 'datetime' ? 'datePicker' : 'input'
        newItem.queryType = 'eq'
        newItem.exportable = true
        newItem.importable = !innerFields.includes(item.code)

        return newItem;
    })
}
</script>

<style lang='scss' scoped></style>