<template>
    <div class="d-flex h-100">
        <div class="left-nav h-100">
            <left-menu class="left-menu" :menus="finalMenus" :folded="menuFolded" :title="mainTitle">
                <template #logo>
                    <slot name="logo" />
                </template>
            </left-menu>
        </div>

        <div class="right-panel flex-auto h-100">
            <top-nav v-model:menuFolded="menuFolded" v-model:currentMenu="currentTopMenu" :menus="menus" :title="title" :mainTitle="mainTitle">
                <template #logo>
                    <slot name="logo" />
                </template>
            </top-nav>
            <div class="right-content b-box br-1 flex-auto d-flex-col">
                <tags-tab v-if="showTagTabs" v-model="cachedRoutes" />
                <div class="main-content flex-auto relative" :class="{ tags: showTagTabs, 'p-4': pad }">
                    <router-view v-slot="{ Component, route }">
                        <keep-alive :include="cachedRoutes">
                            <component :is="Component" :key="route.path" />
                        </keep-alive>
                    </router-view>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import leftMenu from "../left-menu.vue";
import TopNav from "../TopNav.vue";
import { computed } from "vue";
import TagsTab from "../tags-tab.vue";
import { useSysStore } from "@/store";

const props = defineProps({
    pad: { type: Boolean, default: true },
    menus: { type: Array, default: [] },
    title: { type: String },
    mainTitle: { type: String },
});
const sysStore = useSysStore();
const showTagTabs = computed(sysStore.tagTabsVisible);
const showTopMenus = computed(sysStore.topMenusVisible);
const cachedRoutes = ref([]);
const finalMenus = computed(() => {
    if (showTopMenus.value) {
        return currentTopMenu.value.children || [];
    } else {
        return props.menus || [];
    }
});
const menuFolded = ref(false);
const currentTopMenu = ref({});
</script>

<style scoped lang="scss">
.right-panel {
    overflow: hidden;
    background-color: #f2f3f5;

    .right-content {
        height: calc(100% - 48px);

        .main-content {
            height: calc(100% - 96px);
            overflow-y: auto;
            box-sizing: border-box;

            &.tags {
                height: calc(100% - 154px);
            }
        }
    }
}
</style>
