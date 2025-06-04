<template>
    <el-menu
        router
        :default-openeds="opened"
        class="left-menu h-100"
        :collapse="folded"
        :collapse-transition="false"
        :default-active="active"
        unique-opened
        v-if="menus.length"
    >
        <div class="p-4 font-bold title text-center">
            <slot name="logo">
                <i class="iconfont icon-liushuixian-liushuixianx" />
            </slot>
            <span class="ml-2" v-if="!folded">{{ title }}</span>
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
                    <template v-if="!folded">{{ menu.name }}</template>
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
import { useRouter } from "vue-router";

const props = defineProps({
    menus: { type: Array, default: [] },
    folded: { type: Boolean, default: false },
    current: { type: Object },
    title: { type: String },
});
const opened = computed(() => {
    let menus = props.menus;
    if (!menus.value || !menus.value.length) {
        return [];
    }

    return [menus.value[0].path];
});
const router = useRouter();
const active = computed(() => {
    return router.currentRoute.value.fullPath;
});
</script>

<style lang="scss" scoped>
.left-menu {
    border-right: none;
    box-shadow: 0 0 5px var(--el-menu-box-shadow-color);
    z-index: 6;

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
    width: 220px;
}

.title {
    color: var(--main_title_color);
}
</style>
