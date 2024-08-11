<!-- 标题栏 -->
<template>
    <div class="space-between v-center mb-2">
        <div class="page-title">{{ title }}</div>
        <slot name="right"></slot>
    </div>
</template>

<script setup>
defineProps(["title"])
</script>

<style lang="scss" scoped>
.page-title {
    margin-bottom: 0px;
}
</style>