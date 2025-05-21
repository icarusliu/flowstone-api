<template>
    <div class="top-nav">
        <div class="content pl-4 space-between pr-4">
            <div class="v-center">
                <el-icon @click="reverseMenuFold" class="cursor-pointer icon mr-2">
                    <Fold v-if="!menuFolded" />
                    <Expand v-else />
                </el-icon>

                <!-- 不展示顶部菜单时，展示当前路由的标题 -->
                <label v-if="!showTopMenus">{{ title }}</label>
                <div v-else class="d-flex">
                    <!-- 否则展示顶部菜单 -->
                    <div
                        v-for="menu in menus"
                        :key="menu.id"
                        class="top-menu-item v-center mx-2 px-2 cursor-pointer"
                        :class="{ current: currentTopMenu.id == menu.id }"
                        @click="selectTopMenu(menu)"
                    >
                        <ElIcon class="mr-1">
                            <component :is="menu.icon" />
                        </ElIcon>
                        <span>{{ menu.name }}</span>
                    </div>
                </div>
            </div>

            <div class="v-center mr-4">
                <el-avatar class="avatar" :icon="userInfo.avatar || 'User'"></el-avatar>

                <el-dropdown>
                    <label class="cursor-pointer">
                        <span class="ml-2">{{ userInfo.nickname }}</span>
                        <el-icon class="el-icon--right">
                            <arrow-down />
                        </el-icon>
                    </label>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="showUpdatePwd">
                                <el-icon>
                                    <Setting />
                                </el-icon>
                                <label>修改密码</label>
                            </el-dropdown-item>
                            <el-dropdown-item @click="showConfigDialog">
                                <el-icon>
                                    <Operation />
                                </el-icon>
                                <label>布局配置</label>
                            </el-dropdown-item>
                            <el-dropdown-item @click="logout" divided>
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

        <el-dialog v-model="visible" title="修改密码" width="500px">
            <base-form :fields="fields" v-model="form" ref="formRef" />

            <template #footer>
                <el-button type="primary" @click="updatePassword">确定</el-button>
                <el-button @click="visible = false">取消</el-button>
            </template>
        </el-dialog>

        <el-drawer v-model="configVisible" title="布局配置" size="400px">
            <base-form :fields="configFields" v-model="userConfig" ref="configFormRef" />
            <template #footer>
                <el-button type="primary" @click="updateConfig">确定</el-button>
                <el-button @click="configVisible = false">取消</el-button>
            </template>
        </el-drawer>
    </div>
</template>

<script setup>
import { ref, watchEffect } from "vue";
import { useSysStore } from "../store";
import { useRouter } from "vue-router";
import https from "@/utils/https";
import { ElMessage } from "element-plus";
import * as _ from "lodash";

const sysStore = useSysStore();
const userConfig = ref({});
const menuFolded = computed(sysStore.getMenuFolded);
const userInfo = computed(sysStore.getUserInfo);
const configInfo = computed(sysStore.getSysConfig);
const menus = computed(() => sysStore.topMenus);
const router = useRouter();
const title = computed(() => {
    let meta = router.currentRoute.value.meta;
    return meta?.title || "流石数据管理";
});
const fields = [
    { label: "原密码", prop: "oldPassword", inputType: "password", required: true },
    { label: "新密码", prop: "password", inputType: "password", required: true },
    { label: "重复新密码", prop: "rePassword", inputType: "password", required: true },
];
const visible = ref(false);
const form = ref({});
const formRef = ref();
const currentTopMenu = ref({});
const configVisible = ref(false);
const configFields = [
    { label: "显示顶部菜单", prop: "showTopMenus", type: "switch" },
    { label: "多标签模式", prop: "showTagsTab", type: "switch" },
];
const configFormRef = ref();
const showTopMenus = computed(() => {
    let userShow = userInfo.value.metadata?.showTopMenus
    if (!userShow && userShow != false) {
        return configInfo.value.showTopMenus
    }

    return userShow
})

onMounted(() => {
    if (!showTopMenus.value) {
        return;
    }

    // 获取当前选中的顶部菜单
    let path = router.currentRoute.value.path;
    for (var i in menus.value) {
        let menu = menus.value[i];
        if (menu.path == path) {
            currentTopMenu.value = menu;
            sysStore.setMenuTree(menu.children);
            return;
        }

        if (menu.children) {
            for (var j in menu.children) {
                let subMenu = menu.children[j];
                if (subMenu.path == path) {
                    currentTopMenu.value = menu;
                    sysStore.setMenuTree(menu.children);
                    return;
                }
            }
        }
    }
});

function reverseMenuFold() {
    useSysStore().reverseMenuFold();
}

function logout() {
    router.push("/login");
}

function showUpdatePwd() {
    visible.value = true;
}

function updatePassword() {
    formRef.value.validate((res) => {
        if (!res) {
            return;
        }

        https.post("/sys/user/update-pwd", form.value).then(() => {
            ElMessage.success("密码更新成功");
            visible.value = false;
        });
    });
}

function selectTopMenu(menu) {
    currentTopMenu.value = menu;
    sysStore.setMenuTree(menu.children);
}

function showConfigDialog() {
    userConfig.value = _.cloneDeep(userInfo.value.metadata || {});
    if (!userConfig.value.showTopMenus && userConfig.value.showTopMenus != false) {
        userConfig.value.showTopMenus = configInfo.value.showTopMenus;
    }

    if (!userConfig.value.showTagsTab && userConfig.value.showTagsTab != false) {
        userConfig.value.showTagsTab = configInfo.value.showTagsTab;
    }

    configVisible.value = true;
}

function updateConfig() {
    app.https
        .post("/sys/user/update-mine", {
            metadata: userConfig.value,
        })
        .then(() => {
            sysStore.setUserInfo({
                ...userInfo.value,
                metadata: userConfig.value,
            });

            // 要刷新右侧菜单
            if (userConfig.value.showTopMenus) {
                // 显示顶部菜单
                sysStore.setMenuTree(currentTopMenu.value.children)
            } else {
                sysStore.setMenuTree(menus.value)
            }

            configVisible.value = false;
        });
}
</script>

<style lang="scss" scoped>
.top-nav {
    --height: 48px;

    height: var(--height);
    line-height: var(--height);
    background-color: #fff;
    color: var(--main_font_color);
    z-index: 4;
    position: relative;

    .content {
        box-shadow: 0 0 4px #ccc;
    }

    .icon {
        font-size: 20px;
        color: #555;
    }

    .avatar {
        font-size: 18px;
        width: 30px;
        height: 30px;
    }
}

:deep() {
    .el-tooltip__trigger {
        outline: unset;
    }
}

.top-menu-item {
    color: #777;
    box-sizing: border-box;
    border-bottom: 2px solid transparent;
    &.current {
        border-color: var(--primary_color);
    }
}
</style>
