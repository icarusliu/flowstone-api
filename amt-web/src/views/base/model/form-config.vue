<template>
    <el-form labelWidth="140px">
        <el-form-item label="新增展示类型">
            <el-select>
                <el-option value="drawer" label="抽屉"></el-option>
                <el-option value="dialog" label="弹窗"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="表单字段配置">
            <edit-table :fields="fields" :showNew="false" :withDelete="false" :border="true"/>
        </el-form-item>
    </el-form>
</template>
<script setup>
import editTable from '@/components/edit-table/index.vue'

const fields = ref([
    {label: '标签名称', prop: 'name'},
    {label: '是否隐藏', prop: 'hide'},
    {label: '数据类型', prop: 'dataType'},
    {label: '组件', prop: 'component'},
    {label: '选项', prop: 'options'},
    {label: '默认值', prop: 'defaultValue'},
    {label: '是否必填', prop: 'required'},
    {label: '最小长度/最小值', prop: 'min'},
    {label: '最大长度/最大值', prop: 'max'},
    
])
</script>

<style lang='scss' scoped>

</style>