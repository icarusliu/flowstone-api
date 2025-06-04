<template>
    <div class="top-nav">
        <div class="content pl-4 space-between pr-4">
            <div class="v-center">
                <!-- 折叠按钮 -->
                <el-icon @click="reverseMenuFold" class="cursor-pointer icon mr-2" v-if="!showTopMenus || showLeftMenu">
                    <Fold v-if="!menuFolded" />
                    <Expand v-else />
                </el-icon>

                <!-- 不展示顶部菜单时，展示当前路由的标题 -->
                <label v-if="!showTopMenus">{{ title }}</label>
                <div v-else class="d-flex">
                    <!-- 否则展示顶部菜单 -->

                    <!-- 如果未展示左侧菜单，则需要展示LOGO -->
                    <div class="font-bold title text-center mx-4" v-if="!showLeftMenu">
                        <slot name="logo">
                            <i class="iconfont icon-liushuixian-liushuixianx" />
                        </slot>
                        <span class="ml-2" v-if="!menuFolded">{{ mainTitle }}</span>
                    </div>

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
import { ref } from "vue";
import { useSysStore } from "../store";
import { useRouter } from "vue-router";
import https from "@/utils/https";
import { ElMessage } from "element-plus";
import * as _ from "lodash";

const props = defineProps({
    menus: { type: Array, default: [] },
    title: { type: String },
    mainTitle: { type: String },
});
const sysStore = useSysStore();
const userConfig = ref({});
const menuFolded = defineModel("menuFolded", { default: false });
const currentTopMenu = defineModel("currentMenu", { default: {} });
const userInfo = computed(sysStore.getUserInfo);
const configInfo = computed(sysStore.getSysConfig);
const router = useRouter();
const fields = [
    { label: "原密码", prop: "oldPassword", inputType: "password", required: true },
    { label: "新密码", prop: "password", inputType: "password", required: true },
    { label: "重复新密码", prop: "rePassword", inputType: "password", required: true },
];
const visible = ref(false);
const form = ref({});
const formRef = ref();

const configVisible = ref(false);
const configFields = [
    { label: "显示顶部菜单", prop: "showTopMenus", type: "switch" },
    { label: "多标签模式", prop: "showTagTabs", type: "switch" },
];
const configFormRef = ref();
const showTopMenus = computed(sysStore.topMenusVisible);
const showLeftMenu = computed(() => {
    return currentTopMenu.value?.children?.length;
});

watch(
    () => props.menus,
    () => {
        if (!showTopMenus.value) {
            return;
        }

        // 获取当前选中的顶部菜单
        let path = router.currentRoute.value.path;

        // 有可能是二级界面未配置到菜单中，因此，如果未找到对应的菜单配置，需要找到其上级路由
        let backupMenu;

        let menus = props.menus;
        for (var i in menus) {
            let menu = menus[i];
            if (menu.path == path) {
                currentTopMenu.value = menu;
                return;
            }

            if (menu.children) {
                for (var j in menu.children) {
                    let subMenu = menu.children[j];
                    if (subMenu.path == path) {
                        currentTopMenu.value = menu;
                        return;
                    }
                }
            }

            if (path.startsWith(menu.path)) {
                backupMenu = menu;
            }
        }

        if (backupMenu) {
            currentTopMenu.value = backupMenu;
        } else if (menus.length) {
            // 没找到，选中第一个
            currentTopMenu.value = menus[0];
        }
    },
    { deep: true, immediate: true }
);

function reverseMenuFold() {
    menuFolded.value = !menuFolded.value;
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

    if (!menu.children.length && menu.path) {
        router.push(menu.path);
    } else if (menu.children.length) {
        router.push(menu.children[0].path);
    }
}

function showConfigDialog() {
    userConfig.value = _.cloneDeep(userInfo.value.metadata || {});
    if (!userConfig.value.showTopMenus && userConfig.value.showTopMenus != false) {
        userConfig.value.showTopMenus = configInfo.value.showTopMenus;
    }

    if (!userConfig.value.showTagTabs && userConfig.value.showTagTabs != false) {
        userConfig.value.showTagTabs = configInfo.value.showTagTabs;
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
        height: 100%;
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
