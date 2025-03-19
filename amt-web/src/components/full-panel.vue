<template>
    <div class="page-content bg-white p-4">
        <div class="top-bar d-flex">
            <div class="return cursor-pointer" @click="close">
                <el-icon>
                    <ArrowLeft />
                </el-icon>
                <span>返回</span>
            </div>
            <div class="mx-2">|</div>
            <div class="title">{{ title }}</div>
        </div>

        <div class="content mt-2">
            <slot></slot>
        </div>
    </div>
</template>
<script setup>
const props = defineProps({
    title: { type: String }
})
const emits = defineEmits(['close'])

function close() {
    emits('close')
}
</script>

<style lang='scss' scoped>
.top-bar {
    align-items: center;
    border-bottom: 1px solid #eee;
    padding-bottom: 8px;

    .return {
        display: flex;
        align-items: center;
    }

    .title {
        font-size: 14px;
        font-weight: bold;
    }
}
</style>