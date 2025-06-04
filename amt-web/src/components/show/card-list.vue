<template>
    <div class="card-list">
        <div class="items" :style="{ 'grid-template-columns': 'repeat(auto-fit, ' + width + ')' }">
            <div v-for="item in list" :key="item.id" class="bg-white br-1">
                <slot :item="item">
                    {{ item.name }}
                </slot>
            </div>
        </div>

        <el-pagination
            :total="total"
            :pageNo="pageNo"
            :pageSize="pageSize"
            layout="prev, pager, next"
            @change="pageChanged"
            background
            class="mt-2 pagination"
        ></el-pagination>
    </div>
</template>
<script setup>
import { onMounted } from "vue";

const props = defineProps({
    dataSupplier: {
        type: Function,
        required: true,
    },

    pageSize: {
        type: Number,
        default: 10,
    },
    width: {
        type: String,
        default: "400px",
    },
});

const list = ref([]);
const total = ref(0);
let pageNo = 1;

onMounted(loadData);

function loadData() {
    props.dataSupplier({ pageNo, pageSize: props.pageSize }).then((resp) => {
        list.value = resp.records;
        total.value = resp.total;
    });
}

function pageChanged(e) {
    pageNo = e;
    loadData();
}

function reload() {
    pageNo = 1;
    loadData();
}

defineExpose({
    reload,
});
</script>

<style lang="scss" scoped>
.items {
    display: grid;
    grid-row-gap: 16px;
    grid-column-gap: 16px;
}
</style>
