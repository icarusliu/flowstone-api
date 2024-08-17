<template>
    <div class="main">
        <div class="login-box">
            <h1 class="color-primary">欢迎使用流石API管理工具</h1>
            <div class="content">
                <div class="sub-title">
                    <span class="title font-bold">
                        欢迎登录
                    </span>
                </div>
                <el-form class="form" :model="formData" :rules="rules" ref="formRef">
                    <el-form-item prop="username">
                        <el-input class="line" placeholder="请输入用户名或手机号" v-model="formData.username" name="username" />
                    </el-form-item>
                    <el-form-item prop="password">
                        <password-input :line="true" v-model="formData.password" />
                    </el-form-item>
                    <div class="buttons">
                        <el-button type="primary" class="button" @click="doLogin">登录</el-button>
                    </div>

                    <div v-if="errorInfo.restCount <= 3" class="color-red">
                        <span v-if="errorInfo.restCount <= 0">用户已锁定，请10分钟后再试</span>
                        <span v-else>剩余尝试次数：{{ errorInfo.restCount }}，如超出尝试次数用户将锁定10分钟</span>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { login } from '@/apis/login.js'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { setToken } from '../../utils/token'
import passwordInput from '../../components/password-input.vue'
import { useSysStore } from '../../store'

const router = useRouter()
const formData = ref({})
const rules = reactive({
    username: {
        required: true,
        message: '用户名或手机号不能为空'
    },
    password: {
        required: true,
        message: '密码不能为空'
    },
    code: {
        required: true,
        message: '验证码不能为空'
    }
})
const formRef = ref()
const errorInfo = ref({
    restCount: 10
})

onMounted(() => {
    // 如果用户登录过，那么跳转到对应页面或首页
    const sysStore = useSysStore()
    const userInfo = sysStore.getUserInfo()
    if (userInfo.userId) {
        // 登录过
        goNextPage()
    }
})

// 跳转页面
function goNextPage() {
    // 如果链接中带有next，则跳转next，否则跳转首页
    const next = router.currentRoute.value.query?.next
    if (!next) {
        router.push('/')
    } else {
        router.push(next)
    }
}

// 进行登录
function doLogin() {
    formRef.value.validate(resp => {
        if (!resp) {
            return
        }

        login(formData.value).then(resp => {
            setToken(resp.accessToken)

            const sysStore = useSysStore()
            sysStore.setUserInfo(resp.userInfo)

            ElMessage.success('登录成功')

            goNextPage()
        }).catch(err => {
            ElMessage.error(err.msg)
            errorInfo.value = err
        })
    })
}

</script>

<style lang="scss" scoped>
@media screen and (min-width: 600px) {
    .login-box {
        width: 500px;
        right: 10%;
        top: 15%;
    }
}

@media screen and (max-width: 600px) {
    .login-box {
        width: 100%;
        padding: 0 16px;
        top: 15%;
        box-sizing: border-box;
    }
}

.main {
    background-image: url('/login_bg.png');
    background-size: cover;
    width: 100%;
    height: 100%;
}

.login-box {
    position: absolute;

    h1 {
        text-align: center;
        margin-bottom: 60px;
        color: #fff;
    }

    .content {
        box-shadow: 0 0 5px #dfdfdf;
        padding: 48px;
        line-height: 58px;
        background-color: #fff;
        border-radius: 5px;

        .sub-title {
            font-size: 18px;
            text-align: center;
            margin-bottom: 32px;

            .title {
                padding-bottom: 12px;
                border-bottom: 3px solid var(--primary_color);
            }
        }

        .buttons {
            margin-top: 16px;

            .button {
                width: 100%;
                height: 40px;
            }
        }

        .form {
            :deep() {
                .el-input {
                    --el-input-height: 48px;
                }
            }
        }

        .bottom-buttons {
            line-height: 24px;
        }
    }
}
</style>