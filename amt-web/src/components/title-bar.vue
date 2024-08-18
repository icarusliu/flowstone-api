<!-- 标题栏 -->
<template>
    <div class="space-between v-center mb-2">
        <div class="page-title">
            <span>{{ title }}</span>
            <span class="color-remark font-sm ml-4 font-normal">
                <slot name="remark">
                    {{ remark }}
                </slot>
            </span>
        </div>
        <slot name="right"></slot>
    </div>
</template>

<script setup>
defineProps(["title", "remark"])
</script>

<style lang="scss" scoped>
.page-title {
    margin-bottom: 0px;
}
</style>