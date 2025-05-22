<template>
    <div class="h-100">
        <entity-manager
            apiPrefix="/sys/menu"
            :fields="fields"
            ref="entityManagerRef"
            :tree="true"
            operationsWidth="220px"
            :params="{ withHide: true, withButtons: true }"
            :tableHeight="tableHeight"
            class="h-100"
        >
            <template #prefixButtons="{ row }">
                <el-link type="primary" @click="editButtons(row)" class="mr-2" v-perm="'buttons'" icon="Operation">按钮</el-link>
            </template>
        </entity-manager>

        <!-- 按钮配置 -->
        <el-dialog v-model="dialogVisible" title="按钮配置" width="600px">
            <edit-table :fields="buttonFields" v-model="buttons">
                <template #appendButtons>
                    <el-link type="primary" @click="importBaseButtons" class="ml-2">导入基础按钮</el-link>
                </template>
            </edit-table>

            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveButtons">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup name="sysMenu">
import EditTable from "@/components/edit-table/index.vue";
import * as entityApis from "@/apis/entity";
import * as _ from "lodash";
import { ElMessage } from "element-plus";
import * as sysApis from "@/apis/sys";
import { computed } from "vue";
import { useSysStore } from "../../store/index";

const fields = [
    { label: "名称", prop: "name", required: true },
    { label: "路径", prop: "path", width: "200px" },
    { label: "图标", prop: "icon", width: "80px", type: "iconSelector", align: "center" },
    { label: "排序", prop: "sort", width: "80px", default: 0, align: "center" },
    {
        label: "是否隐藏",
        prop: "hide",
        type: "checkbox",
        width: "100px",
        align: "center",
        changeInTable: (val, row) => {
            entityApis
                .save("/sys/menu", {
                    id: row.id,
                    hide: val,
                })
                .then((resp) => {
                    ElMessage.success("操作成功");
                    row.hide = val;
                });
        },
    },
    { label: "创建时间", prop: "createTime", width: "160px", needNew: false },
];
const entityManagerRef = ref();
const sysStore = useSysStore();
const buttonFields = ref([
    { label: "编码", prop: "code", required: true },
    { label: "名称", prop: "name", required: true },
]);
const buttons = ref([]);
let editingRow = null;
const dialogVisible = ref(false);
const tableHeight = computed(() => {
    if (sysStore.tagTabsVisible()) {
        return "calc(100vh - 190px)";
    } else {
        return "calc(100vh - 150px)";
    }
});

function editButtons(row) {
    buttons.value = _.cloneDeep(row.buttons || []);
    editingRow = row;
    dialogVisible.value = true;
}

function saveButtons() {
    sysApis
        .saveMenuButtons({
            menuId: editingRow.id,
            buttons: buttons.value,
        })
        .then(() => {
            ElMessage.success("操作成功");
            dialogVisible.value = false;
            entityManagerRef.value.reload();
        });
}

// 导入基础的CRUD按钮
function importBaseButtons() {
    buttons.value.push({ code: "new", name: "新增" }, { code: "delete", name: "删除" }, { code: "update", name: "修改" });
}
</script>
