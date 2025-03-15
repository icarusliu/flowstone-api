<template>
    <span class="buttons">
        <el-link v-for="button in disButtons" :type="button.type || 'primary'" @click="button.action(data)" class="button"
            :disabled="button.finalDisabled">
            {{ button.label }}
        </el-link>
        <el-dropdown v-if="hideButtons.length">
            <el-icon class="cursor-pointer">
                <MoreFilled />
            </el-icon>
            <template #dropdown>
                <el-dropdown-item v-for="button in hideButtons">
                    <el-link :type="button.type || 'primary'" @click="button.action(data)" class="button" :disabled="button.finalDisabled">
                        {{ button.label }}
                    </el-link>
                </el-dropdown-item>
            </template>
        </el-dropdown>
    </span>
</template>
<script setup>
import { computed } from 'vue';
import * as _ from 'lodash'

const props = defineProps({
    buttons: { type: Array, required: true },
    data: {},
    showCount: { type: Number, default: 3 }
})

const finalButtons = computed(() => {
    const buttons = _.cloneDeep(props.buttons)

    return buttons.filter(button => {
        if (button.display == false) {
            return false
        } else if (!button.display) {
            return true
        }
        return button.display(props.data)
    }).map(button => {
        if (button.disabled == true) {
            button.finalDisabled = true
        } else if (!button.disabled) {
            button.finalDisabled = false
        } else {
            button.finalDisabled = button.disabled(props.data)
        }
        return button
    });
})

const disButtons = computed(() => {
    let buttons = finalButtons.value

    const len = buttons.length
    if (len < props.showCount) {
        return buttons
    }

    return buttons.slice(0, props.showCount);
})

const hideButtons = computed(() => {
    const buttons = finalButtons.value
    const len = buttons.length
    if (len < props.showCount) {
        return []
    }

    return buttons.slice(props.showCount);
})
</script>

<style lang='scss' scoped>
.buttons {
    display: flex;
    align-items: center;

    .button:not(:last-child) {
        margin-right: 8px;
    }
}
</style>