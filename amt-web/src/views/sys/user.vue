<template>
    <div class="d-flex">
        <base-tree apiPrefix="/sys/dept" class="tree mr-4 p-4" @currentChange="selectDept" title="组织机构"></base-tree>

        <div class="flex-auto">
            <entity-manager apiPrefix="/sys/user" :fields="fields" :queryFields="queryFields" ref="entityManagerRef" :params="params" operationsWidth="200px">
                <template #rowButtons="{ row }">
                    <el-link type="danger" v-show="row.status && !row.isSuperAdmin" class="mr-2" @click="updateStatus(row, 0)" v-perm="'update'" icon="Remove"
                        >停用</el-link
                    >
                    <el-link type="success" v-show="!row.status" class="mr-2" @click="updateStatus(row, 1)" v-perm="'update'" icon="Check">启用</el-link>
                    <el-link type="primary" v-show="!row.isSuperAdmin" @click="goEdit(row)" class="mr-2" v-perm="'update'" icon="Edit">编辑</el-link>
                    <el-link type="danger" v-show="!row.isSuperAdmin" @click="doDelete(row)" class="mr-2" v-perm="'delete'" icon="Delete">删除</el-link>
                </template>

                <template #newRemark>
                    <div class="remark-panel">新增用户默认密码与用户名一致</div>
                </template>
            </entity-manager>
        </div>
    </div>
</template>

<script setup name="sysUser">
import BaseTree from "@/components/base-tree.vue";
import * as sysApis from "@/apis/sys.js";
import { ElMessage } from "element-plus";

const fields = [
    { label: "用户名", prop: "username", required: true, width: "200px" },
    { label: "昵称", prop: "nickname", required: true, width: "200px" },
    { label: "机构", prop: "deptId", type: "tree-select", options: getDeptTree, converter: (val, row) => row.deptName },
    { label: "手机号", prop: "phone", width: "200px" },
    {
        label: "状态",
        prop: "status",
        width: "80px",
        tagType: (val) => {
            return {
                type: val ? "success" : "warning",
                text: val ? "启用" : "停用",
            };
        },
        needNew: false,
    },
    { label: "角色", prop: "roleIds", type: "select", multiple: true, options: sysApis.getRoles, showInTable: false },
    { label: "创建时间", prop: "createTime", width: "200px", needNew: false },
];
const entityManagerRef = ref();
const params = ref({});
const queryFields = [
    { label: "用户名", prop: "username" },
    { label: "手机号", prop: "phone" },
];

function goEdit(row) {
    entityManagerRef.value.goEdit(row);
}

function doDelete(row) {
    entityManagerRef.value.doDelete(row);
}

function updateStatus(row, status) {
    sysApis.updateUserStatus(row.id, status).then((resp) => {
        ElMessage.success("操作成功");
        row.status = status;
    });
}

function getDeptTree() {
    return sysApis.getDeptTree();
}

function selectDept(dept) {
    params.value.deptId = dept.id;
    console.log(params.value);
    entityManagerRef.value.reload();
}
</script>

<style lang="scss" scoped>
.tree {
    width: 260px;
    min-width: 260px;
    min-height: calc(100vh - 160px);
}
</style>
