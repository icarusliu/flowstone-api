<template>
    <entity-manager apiPrefix="/sys/config" :fields="fields" :queryFields="queryFields" ref="entityManagerRef" operationsWidth="200px">
        <template #prefixButtons="{row}">
            <el-link type="primary" class="mr-2" v-if="!row.enabled" @click="updateStatus(row, 1)" v-perm="'update'">启用</el-link>
            <el-link type="warning" class="mr-2" v-else @click="updateStatus(row, 0)" v-perm="'update'">停用</el-link>
        </template>
    </entity-manager>
</template>

<script setup>
import * as _ from 'lodash'
import * as sysApis from '@/apis/sys'
import { ElMessage } from 'element-plus';

const fields = [
    { label: '编码', prop: 'code', required: true, width: '200px' },
    { label: '名称', prop: 'name', required: true, width: '200px' },
    { label: '配置值', prop: 'value', required: true, type: 'textarea' },
    { label: '状态', prop: 'enabled', needNew: false, width: '100px', tagType: (val) => {
        return {
            type: val ? 'success' : 'warning',
            text: val ? '启用' : '停用',
        }
    } },
    { label: '说明', prop: 'remark', type: 'textarea' },
    { label: '创建时间', prop: 'createTime', width: '160px', needNew: false },
]
const entityManagerRef = ref()
const queryFields = ref([
    { label: '编码', prop: 'code' },
    { label: '名称', prop: 'name' }
])

// 修改记录状态
function updateStatus(row, status) {
    sysApis.updateConfig({
        id: row.id,
        enabled: status
    }).then(() => {
        ElMessage.success('操作成功')
        row.enabled = status
    })
}
</script>