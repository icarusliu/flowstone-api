<template>
    <base-table :fields="fields" :dataSupplier="loadHistory" />
</template>
<script setup>
import { useRouter } from "vue-router";

const props = defineProps({
    apiId: String,
});

const fields = [
    { label: "发布时间", prop: "createTime" },
    { label: "操作人", prop: "createUser" },
    {
        label: "操作",
        prop: "operations",
        type: "operations",
        width: "80px",
        buttons: [{ label: "详情", action: showHistoryDetail, type: "primary", icon: "memo" }],
    },
];
const router = useRouter();

function loadHistory(params) {
    return app.https.post("/base/api-history/page-query", { ...params, apiId: props.apiId });
}

function showHistoryDetail(row) {
    window.open(`/apis/editor?id=${row.apiId}&historyId=${row.id}`, "_blank");
}
</script>

<style lang="scss" scoped></style>
