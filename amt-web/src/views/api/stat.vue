<template>
    <div class="stat-items mb-4 d-grid-col">
        <div class="stat-item shadow br-1 cursor-pointer" v-for="item in statInfo" :key="item.name" @click="goList">
            <div>{{ item.name }}</div>
            <div :style="{ color: item.color }">{{ item.value }}</div>
        </div>
    </div>

    <el-row :gutter="16">
        <el-col :span="24" class="mb-4">
            <div class="content-panel shadow">
                <div class="page-title">最近失败接口</div>
                <el-table :data="failedList" stripe>
                    <el-table-column type="index" label="序号" width="60px"></el-table-column>
                    <el-table-column label="名称" prop="apiName" width="200px">
                        <template #default="{ row }">
                            <el-link :href="'/apis/editor?id=' + row.apiId">{{ row.apiName }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column label="路径" prop="apiPath" width="200px" />
                    <el-table-column label="执行时间" prop="createTime" width="160px" />
                    <el-table-column label="失败信息" width="220px" prop="errorMsg"> </el-table-column>
                    <el-table-column label="异常详情" prop="result">
                        <template #default="{ row }">
                            <el-popover width="800px" effect="dark" trigger="click">
                                <template #reference>
                                    <div>
                                        <span class="ellipsis-1 cursor-pointer">{{ row.result }}</span>
                                    </div>
                                </template>
                                <div class="detail">{{ row.result }}</div>
                            </el-popover>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-col>

        <el-col :span="8">
            <div class="content-panel shadow">
                <div class="page-title">接口调用Top10</div>
                <el-table :data="topCalled" stripe>
                    <el-table-column type="index" label="序号" width="60px"></el-table-column>
                    <el-table-column label="名称" prop="name">
                        <template #default="{ row }">
                            <el-link :href="'/apis/editor?id=' + row.id">{{ row.name }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column label="路径" prop="path" />
                    <el-table-column label="调用次数" width="100px">
                        <template #default="{ row }"> {{ row.successCount + row.failedCount }}次 </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-col>

        <el-col :span="8">
            <div class="content-panel shadow">
                <div class="page-title">接口耗时Top10</div>
                <el-table :data="topSpentTime" stripe>
                    <el-table-column type="index" label="序号" width="60px"></el-table-column>
                    <el-table-column label="名称" prop="apiName">
                        <template #default="{ row }">
                            <el-link :href="'/apis/editor?id=' + row.apiId">{{ row.apiName }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column label="路径" prop="apiPath" />
                    <el-table-column label="耗时" width="120px">
                        <template #default="{ row }"> {{ row.spentTime }}ms </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-col>

        <el-col :span="8">
            <div class="content-panel shadow">
                <div class="page-title">接口失败率Top10</div>
                <el-table :data="topFailed" stripe>
                    <el-table-column type="index" label="序号" width="60px"></el-table-column>
                    <el-table-column label="名称" prop="name">
                        <template #default="{ row }">
                            <el-link :href="'/apis/editor?id=' + row.id">{{ row.name }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column label="路径" prop="path" />
                    <el-table-column label="失败率" width="120px" prop="failRatio"> </el-table-column>
                </el-table>
            </div>
        </el-col>
    </el-row>
</template>
<script setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";

const topCalled = ref([]);
const topSpentTime = ref([]);
const topFailed = ref([]);
const statInfo = reactive([
    { name: "全部接口", value: 0 },
    { name: "草稿", value: 0, color: "red" },
    { name: "已发布", value: 0, color: "green" },
    { name: "修改中", value: 0, color: "#cdcd34" },
    { name: "已下线", value: 0, color: "#aaa" },
]);
const failedList = ref([]);

onMounted(() => {
    app.https.get("/base/api-draft/top-called").then((resp) => {
        topCalled.value = resp;
    });

    app.https.get("/base/api-log/top-spent-time").then((resp) => {
        topSpentTime.value = resp;
    });

    app.https.get("/base/api-draft/top-failed").then((resp) => {
        topFailed.value = resp
            .filter((item) => !!(item.successCount + item.failedCount))
            .map((item) => {
                item.failRatio = ((100 * item.failedCount) / (item.successCount + item.failedCount)).toFixed(2) + "%";
                return item;
            });
    });

    app.https.get("/base/api-draft/stat-by-status").then((resp) => {
        let t = 0;
        resp.forEach(({ c, status }) => {
            statInfo[0].value += c;
            statInfo[status + 1].value += c;
        });
    });

    app.https.post("/base/api-log/query", { pageNo: 1, pageSize: 10, orderBys: [{ asc: false, column: "createTime" }], status: 1 }).then((resp) => {
        failedList.value = resp;
    });
});

const router = useRouter()
function goList() {
    router.push('/apis/list')
}
</script>

<style lang="scss" scoped>
.list-item {
    line-height: 36px;
    .seq {
        margin-right: 8px;
        width: 20px;
        text-align: right;
        display: inline-block;
    }

    > span {
    }
}

.stat-items {
}

.stat-item {
    background: #fff;
    padding: 20px;
    text-align: center;

    > div:first-child {
        font-size: 20px;
        line-height: 50px;
    }

    > div:last-child {
        font-size: 40px;
    }
}
</style>
