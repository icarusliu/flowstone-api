<template>
    <el-menu router :default-openeds="opened">
        <el-sub-menu v-for="menu in menus" :key="menu.path" :index="menu.path">
            <template #title>
                {{ menu.name }}
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.path" :index="child.path">{{ child.name }}</el-menu-item>
        </el-sub-menu>
    </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue';
import allMenus from '../data/menus';

const menus = ref(allMenus);

const opened = computed(() => {
    return allMenus.map(item => item.path)
})
</script>
