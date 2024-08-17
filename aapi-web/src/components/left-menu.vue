<template>
    <el-menu router :default-openeds="opened" class="left-menu h-100" :collapse="menuFolded" :collapse-transition="false"
        :default-active="active" unique-opened>
        <div class="p-4 font-bold title text-center">
            <i class="iconfont icon-liushuixian-liushuixianx" />
            <span class="ml-4" v-if="!menuFolded">流石接口管理工具</span>
        </div>
        <el-sub-menu v-for="menu in menus" :key="menu.path" :index="menu.path">
            <template #title>
                <el-icon>
                    <component :is="menu.icon" />
                </el-icon>
                <template v-if="!menuFolded">{{ menu.name }}</template>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.path" :index="child.path">{{ child.name
            }}</el-menu-item>
        </el-sub-menu>
    </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue';
import allMenus from '../data/menus';
import { useSysStore } from '../store';
import router from '../router';

const menuFolded = computed(() => {
    return useSysStore().getMenuFolded()
})
const menus = ref(allMenus);
const opened = computed(() => {
    return allMenus.map(item => item.path)
})
const active = computed(() => {
    return router.currentRoute.value.fullPath
})

</script>

<style lang="scss" scoped>
.left-menu {
    --el-menu-bg-color: #343e5e;
    --el-menu-text-color: #bfcbd9;
    --el-menu-active-color: #409eff;
    --el-menu-hover-bg-color: #242e4e;

    border-right: none;
}

.left-menu:not(.el-menu--collapse) {
    width: 240px;

}

.title {
    color: #efefef;
}
</style>