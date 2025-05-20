<template>
    <div>
        <data-table
            :fields="menuFields"
            v-model="menuTree"
            :show-index="false"
            ref="dataTableRef"
            :rowSelectable="editing"
            :showSelection="true"
            height="calc(100vh - 250px)"
        />
        <div class="text-center mt-4">
            <el-button type="primary" v-if="!editing" @click="startEdit" v-perm="'update'"> 编辑 </el-button>
            <template v-else>
                <el-button type="primary" @click="saveRoleResources">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
            </template>
        </div>
    </div>
</template>
<script setup>
import * as sysApis from "@/apis/sys";
import { h } from "vue";
import * as utils from "@/utils/utils";
import dataTable from "@/components/base-table/data-table.vue";
import * as _ from "lodash";
import { ElMessage, ElCheckbox } from "element-plus";

const props = defineProps({
    role: Object,
});
const editing = ref(false);
const menuFields = ref([
    { label: "菜单名称", prop: "name" },
    {
        label: "按钮",
        prop: "buttons",
        render: (buttons, row) => {
            if (!buttons || !buttons.length) {
                return h("span", "--");
            }

            const buttonDoms = [];
            buttons.forEach((button) => {
                let buttonDom = h(
                    ElCheckbox,
                    {
                        modelValue: button.checked,
                        disabled: !editing.value,
                        "onUpdate:modelValue": (val) => {
                            button.checked = val;
                        },
                    },
                    () => button.name
                );

                buttonDoms.push(buttonDom);
            });

            return h("div", buttonDoms);
        },
    },
]);

// 展示配置界面
const menuTree = ref([]);
const bakMenuTree = ref([]);
const dataTableRef = ref();
watch(
    () => props.role.id,
    () => {
        let roleId = props.role?.id;
        if (!roleId) {
            return;
        }

        if (editing.value) {
            cancelEdit();
        }

        sysApis.getRoleResources(roleId).then((resp) => {
            menuTree.value = resp;
        });
    },
    {
        immediate: true,
        deep: true,
    }
);

function startEdit() {
    _.cloneDeep(menuTree.value, bakMenuTree.value);
    editing.value = true;
}

function cancelEdit() {
    _.cloneDeep(bakMenuTree.value, menuTree.value);
    editing.value = false;
}

// 保存角色资源
function saveRoleResources() {
    let selectedRows = dataTableRef.value.getSelectionRows();
    let menuIds = [];
    let func = (rows) => {
        if (!rows) {
            return;
        }

        rows.forEach((row) => {
            menuIds.push(row.id);

            func(row.children);
        });
    };
    func(selectedRows);

    console.log(selectedRows);

    // 遍历获取按钮的选中状态
    let buttonIds = [];
    utils.loop(menuTree.value, (item) => {
        if (!item.buttons) {
            return;
        }

        item.buttons.forEach((button) => {
            button.checked && buttonIds.push(button.id);
        });
    });

    sysApis
        .saveRoleResources({
            roleId: props.role.id,
            resourceIds: {
                menu: menuIds,
                button: buttonIds,
            },
        })
        .then((resp) => {
            ElMessage.success("操作成功");
            editing.value = false;
        });
}
</script>

<style lang="scss" scoped></style>
