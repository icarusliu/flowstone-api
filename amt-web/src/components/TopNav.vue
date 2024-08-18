<template>
    <div class="top-nav pl-4 v-center">
        <el-icon @click="reverseMenuFold" class="cursor-pointer icon mr-2">
            <Fold v-if="!menuFolded"/>
            <Expand v-else/>
        </el-icon>
        接口管理
    </div>
</template>

<script setup>
import { useSysStore } from '../store';

const menuFolded = computed(() => {
    return useSysStore().getMenuFolded()
})

function reverseMenuFold() {
    useSysStore().reverseMenuFold()
}
</script>

<style lang="scss" scoped>

.top-nav {
    --height: 48px;

    height: var(--height);
    line-height: var(--height);
    background-color: #fff;
    color: var(--main_font_color);

    .icon {
        font-size: 20px;
        color: #555;
    }
}
</style>
