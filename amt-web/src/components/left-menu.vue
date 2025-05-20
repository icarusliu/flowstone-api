<template>
    <el-menu
        router
        :default-openeds="opened"
        class="left-menu h-100"
        :collapse="menuFolded"
        :collapse-transition="false"
        :default-active="active"
        unique-opened
    >
        <div class="p-4 font-bold title text-center">
            <i class="iconfont icon-liushuixian-liushuixianx" />
            <span class="ml-2" v-if="!menuFolded">航空后台管理系统</span>
        </div>
        <template v-for="menu in menus">
            <el-menu-item v-if="!menu.children?.length" :key="menu.path" :index="menu.path">
                <el-icon>
                    <component :is="menu.icon" />
                </el-icon>
                <span>{{ menu.name }}</span>
            </el-menu-item>
            <el-sub-menu v-else :key="menu.id" :index="menu.path">
                <template #title>
                    <el-icon>
                        <component :is="menu.icon" />
                    </el-icon>
                    <template v-if="!menuFolded">{{ menu.name }}</template>
                </template>
                <el-menu-item v-for="child in menu.children" :key="child.path" :index="child.path">
                    {{ child.name }}
                </el-menu-item>
            </el-sub-menu>
        </template>
    </el-menu>
</template>

<script setup>
import { ref, computed } from "vue";
import { useSysStore } from "../store";
import { useRouter } from "vue-router";

const menuFolded = computed(() => {
    return useSysStore().getMenuFolded();
});
const opened = computed(() => {
    if (!menus.value || !menus.value.length) {
        return [];
    }

    return [menus.value[0].path];
});
const router = useRouter();

const active = computed(() => {
    return router.currentRoute.value.fullPath;
});

const menus = computed(() => {
    return useSysStore().getMenuTree();
});
</script>

<style lang="scss" scoped>
.left-menu {
    border-right: none;
    box-shadow: 0 0 5px var(--el-menu-box-shadow-color);
    z-index: 5;

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
