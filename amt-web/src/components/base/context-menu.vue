<template>
    <!-- 右键菜单 -->
    <Teleport to="body">
        <div v-if="visible" class="context-menu bg-white br-1" :style="{ top: top + 'px', left: left + 'px' }">
            <div v-for="menu in menus" :key="menu.name" @click="clickMenu(menu)" class="item cursor-pointer v-center br-1">
                <el-icon class="mr-1"><component :is="menu.icon" /></el-icon>
                <span>{{ menu.name }}</span>
            </div>
        </div>
    </Teleport>
</template>
<script setup>
import { onMounted, Teleport } from "vue";

const { menus } = defineProps({
    menus: { type: Array },
});
const visible = ref(false);
const top = ref(0);
const left = ref(0);

onMounted(() => {
    window.addEventListener("click", () => {
        visible.value = false;
    });
});

let currentItem, currentIdx;
function show(e, item, idx) {
    left.value = e.clientX;
    top.value = e.clientY + 10;
    currentItem = item;
    currentIdx = idx;
    visible.value = true;
}

function hide() {}

function clickMenu(menu) {
    menu.action && menu.action(currentItem, currentIdx);
}

defineExpose({
    show,
    hide,
});
</script>

<style lang="scss" scoped>
.context-menu {
    position: fixed;
    z-index: 10;
    box-shadow: 0 0 5px #bbb;

    .item {
        padding: 6px 20px;

        &:hover {
            background: var(--primary_color);
            color: #eee;
        }
    }
}
</style>
