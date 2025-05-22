<template>
    <div>
        <div class="mb-2">
            <el-button type="primary" icon="plus" @click="showDialog">添加</el-button>
            <el-button type="danger" icon="Delete" :disabled="!selectedRows.length" @click="deleteSelection">删除</el-button>
        </div>
        <data-table
            :fields="fields"
            v-model="users"
            v-model:selection="selectedRows"
            :show-index="false"
            ref="tableRef"
            :showSelection="true"
            maxHeight="calc(100%)"
        />

        <el-dialog title="添加用户" v-model="visible" destroy-on-close>
            <div class="d-flex">
                <base-tree apiPrefix="/sys/dept" title="组织机构" class="tree" @currentChange="deptChange" />
                <div class="flex-auto">
                    <div class="d-flex mb-2">
                        <el-input placeholder="请输入用户名或昵称进行查询" class="mr-2" v-model="params.key" clearable />
                        <el-button type="primary" @click="doQuery">搜索</el-button>
                        <el-button @click="reset">重置</el-button>
                    </div>
                    <base-table
                        :fields="newFields"
                        :params="params"
                        :dataSupplier="loadNoRoleUsers"
                        v-model:selection="newSelected"
                        :showSelection="true"
                        ref="newRef"
                    />
                </div>
            </div>
            <template #footer>
                <el-button type="primary" @click="save">添加</el-button>
                <el-button @click="visible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>
<script setup>
import * as sysApis from "@/apis/sys";
import baseTree from "@/components/base-tree.vue";
import dataTable from "@/components/base-table/data-table.vue";
import * as _ from "lodash";
import { ElMessage, ElCheckbox, ElMessageBox, ElButton } from "element-plus";

const props = defineProps({
    role: Object,
});
const newFields = ref([
    { label: "用户名", prop: "username" },
    { label: "昵称", prop: "nickname" },
    { label: "机构", prop: "deptName" },
    { label: "手机号", prop: "phone" },
    { label: "邮箱", prop: "email" },
]);
const fields = ref([
    { label: "用户名", prop: "username" },
    { label: "昵称", prop: "nickname" },
    { label: "机构", prop: "deptName" },
    { label: "手机号", prop: "phone" },
    { label: "邮箱", prop: "email" },
    {
        label: "操作",
        type: "operations",
        width: "200px",
        buttons: [{ label: "删除", type: "danger", action: doRemove, icon: "Delete" }],
    },
]);
const tableRef = ref();

// 展示配置界面
const users = ref([]);
const selectedRows = ref([]);
const visible = ref(false);

const newSelected = ref([]);
const params = ref({});
const newRef = ref();

watch(
    () => props.role.id,
    () => {
        loadData();
    },
    {
        immediate: true,
        deep: true,
    }
);

// 删除所选
function deleteSelection() {
    ElMessageBox.confirm("确定移除当前所选用户角色？").then(() => {
        sysApis
            .removeRoleUsers({
                userIds: selectedRows.value.map((item) => item.id),
                roleId: props.role.id,
            })
            .then(() => {
                ElMessage.success("操作成功");
                loadData();
            });
    });
}

function showDialog() {
    visible.value = true;
}

function doQuery() {
    newRef.value.reload();
}

function reset() {
    params.value.key = null;
    doQuery();
}

function loadData() {
    let roleId = props.role?.id;
    if (!roleId) {
        return;
    }
    sysApis.getRoleUsers({ roleId }).then((resp) => {
        users.value = resp;
        tableRef.value && tableRef.value.clearSelection();
    });
}

function doRemove(row) {
    ElMessageBox.confirm("确定移除当前用户角色？").then(() => {
        sysApis
            .removeUserRoles({
                userId: row.id,
                roleIds: props.role.id,
            })
            .then(() => {
                ElMessage.success("操作成功");
                loadData();
            });
    });
}

function loadNoRoleUsers(params) {
    params.roleId = props.role.id;
    return sysApis.queryNoRoleUsers(params);
}

function deptChange(val) {
    if (!val?.id) {
        params.value.deptIds = null;
    } else {
        params.value.deptIds = [val.id];
    }

    newRef.value.reload();
}

function save() {
    // 保存角色关联用户
    sysApis
        .saveRoleUsers({
            roleId: props.role.id,
            userIds: newSelected.value.map((item) => item.id),
        })
        .then((resp) => {
            ElMessage.success("操作成功");
            newRef.value.reload();
            loadData();
        });
}
</script>

<style lang="scss" scoped>
:deep() {
    .el-input {
        width: 400px;
    }
}

.tree {
    min-width: 260px;
    width: 260px;
}
</style>
