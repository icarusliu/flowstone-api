<template>
    <!-- 多标签导航 -->
    <el-scrollbar class="tags-tab">
        <router-link
            v-for="(tab, index) in tabs"
            :to="tab.path"
            class="tab-item v-center"
            :class="{ active: tab.path == route.path }"
            @contextmenu.prevent="showMenu($event, tab, index)"
        >
            <span>{{ tab.label }}</span>
            <el-icon class="ml-1 close" @click.prevent.stop="closeTab(tab, index)">
                <Close />
            </el-icon>
        </router-link>

        <contextMenu ref="menuRef" :menus="menus" />
    </el-scrollbar>
</template>
<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import contextMenu from "./base/context-menu.vue";

const tabs = ref([]);
const route = useRoute();
const router = useRouter();
const menus = [
    { name: "关闭当前", icon: "Remove", action: closeTab },
    { name: "关闭其它", icon: "Close", action: closeOtherTabs },
];
const menuRef = ref();
const cachedRoutes = defineModel();

watch(
    route,
    () => {
        let exists = tabs.value.find((item) => item.path == route.path);
        if (!exists) {
            tabs.value.push({
                label: route.meta.title,
                path: route.path,
                fullPath: route.fullPath,
                name: route.name,
            });

            cachedRoutes.value.push(route.name);
        }
    },
    {
        immediate: true,
    }
);

function showMenu(e, tab, idx) {
    e.stopPropagation();
    menuRef.value.show(e, tab, idx);
}

// 关闭其它标签
function closeOtherTabs(tab) {
    tabs.value = tabs.value.filter((item) => {
        if (item == tab) {
            return true;
        }

        removeRoute(item.name);

        return false;
    });
}

// 删除缓存的route
function removeRoute(name) {
    let idx = cachedRoutes.value.indexOf(name);
    if (-1 != idx) {
        cachedRoutes.value.splice(idx, 1);
    }
}

function closeTab(tab, idx) {
    // 如果是当前页签，需要切换到上一页签
    let item = tabs.value.splice(idx, 1);
    removeRoute(item.name);
    if (tab.path == route.path) {
        if (tabs.value.length > 0) {
            const path = tabs.value[tabs.value.length - 1].fullPath;
            router.push(path);
        }
    }
}
</script>

<style lang="scss" scoped>
.tags-tab {
    height: auto;
    padding: 8px 10px 4px;
    background: #fff;
    box-shadow: 0 0 5px #aaa;
    z-index: 1;

    .tab-item {
        float: left;
        height: 26px;
        background: #fff;
        padding: 0px 6px;
        box-sizing: border-box;
        border: 1px solid #eee;
        margin-right: 6px;
        text-decoration: none;
        color: #666;
        font-size: 13px;

        &.active {
            background: var(--sub_color);
            color: #fff;

            &::before {
                content: "";
                width: 6px;
                height: 6px;
                background: #fff;
                border-radius: 5px;
                margin-right: 4px;
                top: 8px;
            }
        }

        .close:hover {
            background: #aaa;
            border-radius: 50%;
            color: #fff;
        }
    }
}
</style>
