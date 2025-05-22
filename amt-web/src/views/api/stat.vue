<template>
    <div class="stat-items mb-4 d-grid-col">
        <div class="stat-item br-1 shadow">
            <div>接口总数</div>
            <div>{{ total }}</div>
        </div>

        <div class="stat-item shadow br-1" v-for="item in statInfo" :key="item.status">
            <div>{{ item.name }}</div>
            <div :style="{ color: item.color }">{{ item.c }}</div>
        </div>
    </div>

    <el-row :gutter="16">
        <el-col :span="8">
            <div class="content-panel shadow">
                <div class="page-title">接口调用Top10</div>
                <el-table :data="topCalled" stripe>
                    <el-table-column type="index" label="序号" width="60px"></el-table-column>
                    <el-table-column label="名称" prop="name"></el-table-column>
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
                    <el-table-column label="名称" prop="apiName"></el-table-column>
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
                    <el-table-column label="名称" prop="name"></el-table-column>
                    <el-table-column label="路径" prop="path" />
                    <el-table-column label="失败率" width="120px" prop="failRatio"> </el-table-column>
                </el-table>
            </div>
        </el-col>
    </el-row>
</template>
<script setup>
import { onMounted } from "vue";

const topCalled = ref([]);
const topSpentTime = ref([]);
const topFailed = ref([]);
const statInfo = ref({});
const total = ref(0);

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
        statInfo.value = resp.map((item) => {
            t += item.c;

            let status = item.status;
            item.name = status == 0 ? "未发布" : status == 1 ? "已发布" : status == 2 ? "修改中" : "已下线";
            item.color = status == 0 ? "#aaa" : status == 1 ? "green" : status == 2 ? "#cdcd34" : "gray";

            return item;
        });
        total.value = t;
    });
});
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
