<template>
    <div class="page-title">用户管理</div>
    <entity-manager apiPrefix="/base/user" :fields="fields" ref="entityManagerRef">
        <template #rowButtons="{row}">
            <el-link type="primary" v-show="!row.isSuperAdmin" @click="goEdit(row)" class="mr-2">编辑</el-link>
            <el-link type="danger" v-show="!row.isSuperAdmin" @click="doDelete(row)" class="mr-2">删除</el-link>
        </template>

        <template #newRemark>
            <div class="remark-panel">
                新增用户默认密码与用户名一致
            </div>
        </template>
    </entity-manager>
</template>

<script setup>
import EntityManager from '@/components/entity-manager.vue'

const fields = [
    { label: '用户名', prop: 'username', required: true },
    { label: '昵称', prop: 'nickname', required: true },
    { label: '手机号', prop: 'phone', width: '200px' },
    { label: '创建时间', prop: 'createTime', width: '200px', needNew: false },
]
const entityManagerRef = ref()

function goEdit(row) {
    entityManagerRef.value.goEdit(row)
}

function doDelete(row) {
    entityManagerRef.value.doDelete(row)
}
</script>