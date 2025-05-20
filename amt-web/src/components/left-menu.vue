<template>
    <el-menu router class="left-menu h-100" :collapse="menuFolded" :collapse-transition="false" :default-active="active" unique-opened>
        <div class="p-4 font-bold title text-center">
            <i class="iconfont icon-liushuixian-liushuixianx" />
            <span class="ml-4" v-if="!menuFolded">流石数据管理工具</span>
        </div>
        <el-sub-menu v-for="menu in menus" :key="menu.path" :index="menu.path">
            <template #title>
                <el-icon>
                    <component :is="menu.icon" />
                </el-icon>
                <template v-if="!menuFolded">{{ menu.name }}</template>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.path" :index="child.path">{{ child.name }}</el-menu-item>
        </el-sub-menu>
    </el-menu>
</template>

<script setup>
import { ref, computed } from "vue";
import allMenus from "../data/menus";
import { useSysStore } from "../store";
import router from "../router";

const menuFolded = computed(() => {
    return useSysStore().getMenuFolded();
});
const menus = ref(allMenus);
const active = computed(() => {
    return router.currentRoute.value.fullPath;
});
</script>

<style lang="scss" scoped>
.left-menu {
    border-right: none;
    box-shadow: 0 0 5px var(--el-menu-box-shadow-color);
    z-index: 3;

    :deep() {
        .el-menu--inline {
            background-color: var(--el-menu-inline-bg-color);
        }

        .el-menu-item.is-active {
            font-weight: bold;
        }
    }
}

.left-menu:not(.el-menu--collapse) {
    width: 240px;
}

.title {
    color: var(--main_title_color);
}
</style>
