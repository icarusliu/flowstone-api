<template>
    <app-frame :pad="pad" :menus="menus" :title="title" :mainTitle="mainTitle">
        <template #logo>
            <i class="iconfont icon-liushuixian-liushuixianx" />
        </template>
    </app-frame>
</template>

<script setup>
import appFrame from "@/components/frame/app-frame.vue";
import { useRouter } from "vue-router";
import { useSysStore } from "@/store";

const router = useRouter();
const sysStore = useSysStore();
const menus = computed(sysStore.getMenuTree);
const title = computed(() => {
    let meta = router.currentRoute.value.meta;
    return meta?.title || document.title;
});
const mainTitle = document.title;
const pad = computed(() => {
    return router.currentRoute.value.meta.pad != false;
});
</script>
