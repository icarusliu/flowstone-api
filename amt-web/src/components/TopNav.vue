<template>
    <div class="top-nav pl-4 space-between pr-4">
        <div class="v-center">
            <el-icon @click="reverseMenuFold" class="cursor-pointer icon mr-2">
                <Fold v-if="!menuFolded" />
                <Expand v-else />
            </el-icon>
            <label>{{ title }}</label>
        </div>

        <div class="v-center mr-4">
            <el-avatar class="avatar" :icon="userInfo.avatar"></el-avatar>

            <el-dropdown>
                <label class="cursor-pointer">
                    <span class="ml-2">{{ userInfo.nickname }}</span>
                    <el-icon class="el-icon--right">
                        <arrow-down />
                    </el-icon>
                </label>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="logout">
                            <el-icon>
                                <SwitchButton />
                            </el-icon>
                            <label>退出登录</label>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup>
import { useSysStore } from '../store'
import { useRouter } from 'vue-router'

const menuFolded = computed(() => {
    return useSysStore().getMenuFolded()
})
const userInfo = computed(() => useSysStore().getUserInfo())

const router = useRouter()
const title = computed(() => {
    let meta = router.currentRoute.value.meta
    return meta?.title || '流石'
})

function reverseMenuFold() {
    useSysStore().reverseMenuFold()
}

function logout() {
    router.push('/login')
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

    .avatar {
        font-size: 12px;
        width: 30px;
        height: 30px;
    }
}

:deep() {
    .el-tooltip__trigger {
        outline: unset;
    }
}
</style>
