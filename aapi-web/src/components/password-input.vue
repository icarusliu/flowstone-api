<template>
    <!-- 密码输入框 -->
    <el-input :type="showPassword ? 'input' : 'password'" placeholder="请输入密码" v-model="model" :class="{'line': line}" name="password" auto-complete>
        <template #suffix>
            <el-icon class="icon" v-if="!showPassword" @click="showPassword = true">
                <View />
            </el-icon>
            <el-icon class="icon" v-else @click="showPassword = false">
                <Hide />
            </el-icon>
        </template>
    </el-input>

    <password-level :password="model" v-if="withLevel"></password-level>
</template>

<script setup>
import { Hide, View } from '@element-plus/icons-vue'
import { ref } from 'vue'
import passwordLevel from './password-level.vue';

const showPassword = ref(false)
const model = defineModel()
defineProps(["withLevel", "line"])
</script>

<style lang="scss" scoped>
.icon {
    width: 30px;
    height: 30px;
    cursor: pointer;
}
</style>