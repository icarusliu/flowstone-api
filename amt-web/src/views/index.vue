<template>
    <div class="d-flex h-100">
        <div class="left-nav h-100">
            <LeftNav class="left-menu" :type="type"></LeftNav>
        </div>

        <div class="right-panel flex-auto h-100">
            <TopNav />
            <div class="right-content b-box br-1 flex-auto d-flex-col">
                <TagsTab v-if="showTagsTab"/>
                <div class="main-content p-4 flex-auto" :class="{tags: showTagsTab}">
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
import LeftNav from "../components/left-menu.vue";
import TopNav from "../components/TopNav.vue";
import { useRouter } from "vue-router";
import { computed } from "vue";
import TagsTab from "../components/tags-tab.vue";
import { useSysStore } from "../store";

const router = useRouter();
const sysStore = useSysStore();
const type = computed(() => {
    return router.currentRoute.value.params.type;
});
const cachedRoutes = computed(sysStore.getCachedRoutes);
const showTagsTab = ref(false)

watchEffect(() => {
  let metadata = sysStore.userInfo.metadata || {}
  if (metadata.showTagsTab == true || metadata.showTagsTab == false) {
    // 存在用户配置，以用户配置为准
    showTagsTab.value = metadata.showTagsTab
  } else {
    // 否则以系统配置为准
    showTagsTab.value = sysStore.sysConfig.showTagsTab 
  }
})
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
