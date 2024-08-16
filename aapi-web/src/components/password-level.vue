<!-- 密码安全性 -->
<template>
    <div class="security w-100 d-flex space-between">
        <span class="info">
            <span :class="{ active: passwordLevel >= 1 }"></span>
            <span :class="{ active: passwordLevel >= 2 }"></span>
            <span :class="{ active: passwordLevel >= 3 }"></span>
        </span>
        <span>
            密码安全性：
            <span v-if="passwordLevel <= 1" class="level1">一般</span>
            <span v-else-if="passwordLevel <= 2" class="level2">较强</span>
            <span v-else="passwordLevel <= 3" class="level3">非常强</span>
        </span>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps(["password"])
const passwordLevel = computed(() => {
    let password = props.password
    let length = 0;
    if (password) {
        length = password.length
    }
    if (!length) {
        return 0;
    } else if (length > 8) {
        return 3;
    } else if (length > 5) {
        return 2;
    } else {
        return 1;
    }
})
</script>

<style lang="scss" scoped>
.security {
    .info {
        >span {
            display: inline-block;
            height: 6px;
            width: 50px;
            border-radius: 4px;
            background-color: #dadbdc;
            margin-right: 4px;

            &.active {
                background-color: var(--primary_color);
            }
        }
    }

    .level1 {
        color: red;
    }

    .level2 {
        color: blue;
    }

    .level3 {
        color: green;
    }
}
</style>