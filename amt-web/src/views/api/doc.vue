<template>
    <div class="bg-white p-4 br-1">
        <el-page-header content="接口说明" @back="goBack"></el-page-header>
        <api-doc :apiId="apiId" class="mb-8 mt-4" />
    </div>
</template>

<script setup>
import apiDoc from "./components/api-doc.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const apiId = computed(() => {
    return router.currentRoute.value.query.id;
});

function goBack() {
    router.back();
}
</script>
