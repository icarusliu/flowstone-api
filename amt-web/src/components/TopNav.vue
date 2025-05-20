<template>
    <div class="top-nav">
        <div class="content pl-4 space-between pr-4">
            <div class="v-center">
                <el-icon @click="reverseMenuFold" class="cursor-pointer icon mr-2">
                    <Fold v-if="!menuFolded" />
                    <Expand v-else />
                </el-icon>
                <label>{{ title }}</label>
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
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import { useSysStore } from '../store'
    import { useRouter } from 'vue-router'
    import https from '@/utils/https'
    import { ElMessage } from 'element-plus'

    const menuFolded = computed(() => {
        return useSysStore().getMenuFolded()
    })
    const userInfo = computed(() => useSysStore().getUserInfo())

    const router = useRouter()
    const title = computed(() => {
        let meta = router.currentRoute.value.meta
        return meta?.title || '流石数据管理'
    })
    const fields = [
        { label: '原密码', prop: 'oldPassword', inputType: 'password', required: true },
        { label: '新密码', prop: 'password', inputType: 'password', required: true },
        { label: '重复新密码', prop: 'rePassword', inputType: 'password', required: true },
    ]
    const visible = ref(false)
    const form = ref({})
    const formRef = ref()

    function reverseMenuFold() {
        useSysStore().reverseMenuFold()
    }

    function logout() {
        router.push('/login')
    }

    function showUpdatePwd() {
        visible.value = true
    }

    function updatePassword() {
        formRef.value.validate(res => {
            if (!res) {
                return
            }

            https.post('/sys/user/update-pwd', form.value).then(() => {
                ElMessage.success('密码更新成功')
                visible.value = false
            })
        })
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
</style>
