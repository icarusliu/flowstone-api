<template>
    <!-- 接口浏览 -->
    <div class="container bg-white pt-1">
        <div class="content" :style="{ height }">
            <base-tree class="tree" :showTitle="true" title="分类" apiPrefix="/base/api-type" @currentChange="selectType" />
        </div>
        <div class="content apis" :style="{ height }">
            <div class="title">接口</div>
            <div class="items">
                <div v-for="api in apis" :key="api.id" class="cursor-pointer api" :class="{ current: currentApi.id == api.id }" @click="selectApi(api)">
                    {{ api.name }}
                </div>
            </div>
        </div>
        <div class="content" :style="{ height }">
            <ApiDoc :apiId="currentApi.id" />
        </div>
    </div>
</template>
<script setup>
import { onMounted } from "vue";
import ApiDoc from "./components/api-doc.vue";
import { useSysStore } from "@/store/index";

const apis = ref([]);
const currentType = ref({});
const currentApi = ref({});
const sysStore = useSysStore();
const height = computed(() => {
    let showTags = sysStore.tagTabsVisible();
    if (showTags) {
        return "calc(100vh - 100px)";
    } else {
        return "calc(100vh - 50px)";
    }
});

function selectType(type) {
    console.log(type);
    currentType.value = type;
    loadApis();
}

onMounted(() => {
    loadApis();
});

function selectApi(api) {
    currentApi.value = api;
}

function loadApis() {
    app.https
        .post("/base/api/query", {
            typeId: currentType.value.id,
        })
        .then((resp) => {
            apis.value = resp;
        });
}
</script>

<style lang="scss" scoped>
.container {
    display: grid;
    height: 100%;
    overflow-y: hidden;
    grid-template-columns: 300px 300px 1fr;
    box-sizing: border-box;

    .apis {
        border-right: 1px solid #eee;

        .title {
            font-weight: bold;
            line-height: 36px;
            border-bottom: 1px solid #eee;
            padding-left: 12px;
        }

        .items {
            line-height: 34px;

            .api {
                padding-left: 12px;

                &.current {
                    background: var(--primary_color);
                    color: #fff;
                }
            }
        }
    }
}

:deep() {
    .tree-panel {
        margin-right: 0;
    }
}

.content {
    overflow-y: auto;
    box-sizing: border-box;
}
</style>
