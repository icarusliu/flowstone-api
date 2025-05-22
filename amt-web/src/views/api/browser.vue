<template>
    <div class="container">
        <div class="search d-flex">
            <el-input placeholder="请输入关键字进行搜索..." class="input mr-2" v-model="key" />
            <el-button type="primary" icon="search" class="button" @click="doSearch">搜索</el-button>
        </div>

        <div class="types px-4 v-center">
            <div>分类：</div>
            <div class="type-item br-1 cursor-pointer" @click="selectType({})" :class="{ current: !currentType.id }">全部</div>
            <div
                v-for="type in types"
                :key="type.id"
                class="type-item br-1 cursor-pointer"
                :class="{ current: currentType.id == type.id }"
                @click="selectType(type)"
            >
                {{ type.name }}
            </div>
        </div>
        <div v-if="!list.length" class="empty">暂无记录</div>
        <div class="items p-4" v-else>
            <div v-for="item in list" :key="item.id" class="item bg-white br-1 p-4 pt-2 cursor-pointer shadow" @click="showApi(item)">
                <div class="title font-bold mb-2 text-big">{{ item.name }}</div>
                <div class="path">
                    <el-tag class="mr-2">{{ item.method }}</el-tag>
                    <span>/dua/{{ item.path }}</span>
                </div>
                <div class="info mt-4 font-sm color-gray space-between">
                    <span class="time">{{ item.createTime }}</span>
                    <span>累计调用：{{ item.totalCount }}次</span>
                </div>
            </div>
        </div>
        <el-pagination
            :total="total"
            :pageNo="pageNo"
            :pageSize="pageSize"
            layout="prev, pager, next"
            @change="pageChanged"
            background
            class="mt-2 pagination"
        ></el-pagination>

        <el-drawer v-model="visible" title="接口详情" size="60%">
            <ApiDoc :apiId="currentApi.id" />

            <template #footer>
                <el-button @click="visible = false">关闭</el-button>
            </template>
        </el-drawer>
    </div>
</template>
<script setup>
import { onMounted, ref } from "vue";
import ApiDoc from "./components/api-doc.vue";

const list = ref([]);
const currentApi = ref({});
const visible = ref(false);
const types = ref([]);
const currentType = ref({});
const total = ref(0);
const pageNo = ref(1);
const pageSize = ref(20);
const key = ref("");

onMounted(() => {
    loadApis();

    app.https.post("/base/api-type/query", {}).then((resp) => {
        types.value = resp;
    });
});

function loadApis() {
    app.https
        .post("/base/api/page-query", { pageNo: pageNo.value, pageSize: pageSize.value, typeId: currentType.value.id, keyLike: key.value })
        .then((resp) => {
            list.value = resp.records;
            total.value = resp.total;
        });
}

function showApi(api) {
    currentApi.value = api;
    visible.value = true;
}

function selectType(type) {
    currentType.value = type;
    loadApis();
}

function pageChanged(e) {
    pageNo.value = e;
    loadApis();
}

function doSearch() {
    pageNo.value = 1;
    loadApis();
}
</script>

<style lang="scss" scoped>
.container {
    width: 1400px;
    margin: 0 auto;
}

.search {
    width: 50%;
    margin: 100px auto;
    align-items: center;

    .input {
        height: 40px;
    }

    .button {
        height: 38px;
    }
}

.types {
    .type-item {
        padding: 4px 10px;

        &.current {
            background: var(--primary_color);
            color: #eee;
        }
    }
}

.items {
    display: grid;
    grid-template-columns: repeat(auto-fill, 260px);
    grid-column-gap: 12px;
    grid-row-gap: 12px;

    .item {
        .title {
            line-height: 40px;
        }
    }
}

.empty {
    margin: 100px auto;
    text-align: center;
    color: gray;
}
</style>
