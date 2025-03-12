<template>
    <!-- 图标选择 -->
    <el-popover trigger="click" width="620px" ref="popRef">
        <template #reference>
            <span class="v-center">
                <el-icon>
                    <component :is="selectedIcon" />
                </el-icon>
                <el-link type="primary" class="ml-2" size="small">选择</el-link>
            </span>
        </template>

        <div class="icons">
            <el-icon v-for="is in icons" class="item cursor-pointer" :class="{ active: selectedIcon == is }"
                @click="doSelect(is)">
                <component :is="is" />
            </el-icon>
        </div>
    </el-popover>
</template>

<script setup>
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const icons = ref([])
for (const [key] of Object.entries(ElementPlusIconsVue)) {
    icons.value.push(key)
}
const selectedIcon = defineModel()
const popRef = ref()

function doSelect(icon) {
    selectedIcon.value = icon
    popRef.value.hide()
}
</script>

<style lang="scss" scoped>
.icons {
    .item {
        width: 28px;
        height: 28px;

        &:hover {
            background-color: var(--focus_bg_color);
        }

        &.active {
            background-color: var(--focus_bg_color);
        }
    }
}
</style>