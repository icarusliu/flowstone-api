<template>
    <div class="h-100 d-flex">
        <base-tree class="tree" title="分类" apiPrefix="/base/api-type" @select="selectType" :newFields="newTypeFields"> </base-tree>
        <div class="content f-left bg-white p-4 br-1">
            <div class="mb-4 space-between">
                <div>
                    <el-input class="wf-3 mr-4" placeholder="请输入关键字进行查询" v-model="params.keyLike" clearable></el-input>
                    <el-button type="primary" @click="doQuery" icon="Search">查询</el-button>
                </div>
                <el-button type="primary" @click="showNewApi" icon="Plus">新增</el-button>
            </div>
            <base-table :fields="fields" :dataSupplier="dataSupplier" :params="params" ref="tableRef"></base-table>
        </div>
    </div>
</template>

<script setup>
import { ref, h } from "vue";
import * as entityApis from "@/apis/entity.js";
import * as apiApis from "@/apis/api.js";
import { ElMessageBox, ElMessage } from "element-plus";
import * as _ from "lodash";
import { useRouter } from "vue-router";
import baseTree from "@/components/edit-tree/index.vue";
import * as utils from "@/utils/utils";

const fields = ref([
    { label: "分类", prop: "typeName", width: "120px" },
    { label: "名称", prop: "name" },
    { label: "路径", prop: "path", converter: (val) => "/dua/" + val },
    {
        label: "状态",
        prop: "status",
        width: "100px",
        tagType: (val) => {
            switch (val) {
                case 0:
                    return {
                        type: "danger",
                        text: "未发布",
                    };
                case 1:
                    return {
                        type: "success",
                        text: "已发布",
                    };
                case 2:
                    return {
                        type: "warning",
                        text: "已修改",
                    };
                case 3:
                    return {
                        type: "info",
                        text: "已下线",
                    };
                default:
                    return {
                        type: "info",
                        text: "未知",
                    };
            }
        },
    },
    { label: "调用次数", prop: "successCount", converter: (val, row) => val + row.failedCount, width: "80px", align: "center" },
    {
        label: "成功率",
        prop: "failedCount",
        render: (val, row) => {
            let total = val + row.successCount;
            if (total == 0) {
                return h("span", "--");
            }

            let ratio = (100 * row.successCount) / total;

            let result = ratio.toFixed(2) + "%";
            if (ratio >= 95) {
                return h("span", { class: "color-success" }, result);
            } else if (ratio >= 80) {
                return h("span", { class: "color-warning" }, result);
            } else {
                return h("span", { class: "color-error" }, result);
            }
        },
        width: "100px",
    },
    { label: "添加时间", prop: "createTime", width: "150px", system: true },
    { label: "修改时间", prop: "updateTime", width: "150px", system: true },
    {
        label: "操作",
        type: "operations",
        width: "220px",
        buttons: [
            { label: "详情", type: "primary", action: goEdit, icon: "Memo" },
            { label: "文档", type: "primary", action: goDoc, icon: "document" },
            { label: "下线", type: "danger", action: doOffline, visible: (row) => row.status == 1 || row.status == 2, icon: "Remove" },
            { label: "发布", type: "success", action: doPublish, visible: (row) => row.status != 1, icon: "Check" },
        ],
        fixed: "right",
    },
]);
const newTypeFields = [{ label: "分类名称", prop: "name", required: true }];
const prefix = "/base/api-draft";
const tableRef = ref();
const router = useRouter();
const selectedType = ref();
const params = reactive({});

function dataSupplier(params) {
    if (selectedType.value) {
        params.typeIds = selectedType.value;
    } else {
        delete params.typeIds;
    }

    return entityApis.load(prefix, params);
}

// 选择分类
function selectType(type) {
    // 需要将所有子分类的id全部拼上进行查询
    const ids = [];
    utils.loop(type, (item) => {
        item.id && ids.push(item.id);
    });

    selectedType.value = ids;
    tableRef.value.reload();
}

// 新增接口
function showNewApi() {
    router.push("/apis/editor");
}

// 进行编辑
function goEdit(row) {
    router.push("/apis/editor?id=" + row.id);
}

// 接口下线
function doOffline(row) {
    ElMessageBox.confirm("确定下线当前接口？").then(() => {
        apiApis.offline(row.id).then(() => {
            row.status = 3;
            ElMessage.success("下线成功");
        });
    });
}

// 接口发布
function doPublish(row) {
    apiApis.publish(row.id).then(() => {
        row.status = 1;
        ElMessage.success("发布成功");
    });
}

// 查询
function doQuery() {
    tableRef.value.reload();
}

function goDoc(row) {
    router.push("/apis/doc?id=" + row.id);
}
</script>

<style lang="scss" scoped>
.tree {
    min-width: 240px;
    width: 240px;
}

.content {
    width: calc(100% - 258px);
}
</style>
