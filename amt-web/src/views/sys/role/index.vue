<template>
    <div>
        <div class="d-flex container">
            <!-- 角色列表 -->
            <base-list v-model="roles" v-model:current="selectedRole" title="角色列表" label-field="name" class="roles mr-4 bg-white p-2 br-1">
                <template #topButtons>
                    <div class="top-buttons text-right">
                        <el-icon class="top-icon cursor-pointer" @click="newRole" v-perm="'new'">
                            <Plus />
                        </el-icon>
                    </div>
                </template>

                <template #itemIcons="{ item }">
                    <el-icon class="mr-2" @click.stop="goEdit(item)" v-perm="'update'">
                        <Edit />
                    </el-icon>
                    <el-icon color="red" @click.stop="doDelete(item)" v-perm="'delete'">
                        <Delete />
                    </el-icon>
                </template>
            </base-list>

            <div class="flex-auto bg-white p-4 br-1" v-if="selectedRole">
                <el-tabs>
                    <el-tab-pane label="角色用户">
                        <users :role="selectedRole" />
                    </el-tab-pane>
                    <el-tab-pane label="菜单权限">
                        <menus :role="selectedRole" class="menus" />
                    </el-tab-pane>
                </el-tabs>
            </div>

            <div v-else class="flex-auto bg-white p-4 br-1 color-remark text-center">请选择角色</div>
        </div>

        <!-- 角色编辑 -->
        <EntityEditDialog
            v-model="editingRole"
            v-model:visible="dialogVisible"
            :fields="roleFields"
            api-prefix="/sys/role"
            width="600px"
            @save="loadRoles"
        ></EntityEditDialog>
    </div>
</template>

<script setup name="sysRole">
import baseList from "@/components/base-list.vue";
import * as sysApis from "@/apis/sys";
import { h, nextTick, onMounted, watchEffect } from "vue";
import * as _ from "lodash";
import { ElMessage, ElMessageBox, ElCheckbox } from "element-plus";
import EntityEditDialog from "@/components/entity-manager/entity-edit-dialog.vue";
import menus from "./menus.vue";
import users from "./users.vue";

const roleFields = ref([{ label: "角色名称", prop: "name", required: true }]);
const roles = ref([]);
const selectedRole = ref();

onMounted(() => {
    loadRoles();
});

function loadRoles() {
    sysApis.getRoles().then((resp) => {
        roles.value = resp;
        if (roles.value && roles.value.length) {
            selectedRole.value = roles.value[0];
        }
    });
}

const editingRole = ref({});
const dialogVisible = ref(false);
function newRole() {
    editingRole.value = {};
    dialogVisible.value = true;
}

function goEdit(row) {
    editingRole.value = _.cloneDeep(row);
    dialogVisible.value = true;
}

function doDelete(row) {
    ElMessageBox.confirm("确定删除当前记录？").then(() => {
        sysApis.deleteRole(row.id).then(() => {
            ElMessage.success("删除成功");
            loadRoles();
        });
    });
}
</script>

<style lang="scss" scoped>
.roles {
    width: 260px;
    min-width: 260px;

    .top-buttons {
        .top-icon {
            font-size: 16px;
            color: var(--primary_color);

            &:hover {
                color: var(--focus_color);
            }
        }
    }
}

.menus {
    max-height: calc(100vh - 200px);
}
</style>
