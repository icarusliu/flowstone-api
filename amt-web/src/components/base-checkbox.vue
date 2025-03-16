<template>
    <el-checkbox v-if="!options" v-model="finalModel" :disabled="disabled" @change="onFieldChange">
    </el-checkbox>
    <el-checkbox-group v-else v-model="finalModel" :disabled="disabled" @change="onFieldChange">
        <el-checkbox v-for="option in options" :value="option.value" :label="option.label" />
    </el-checkbox-group>
</template>
<script setup>
import { computed } from 'vue'
import * as _ from 'lodash'

const props = defineProps({
    options: Array,
    disabled: {type: Boolean, default: false}
})
const emits = defineEmits(['change'])
const model = defineModel()
const finalModel = computed({
    get() {
        if (!model.value) {
            if (model.value == 0) {
                if (props.options) {
                    return [model.value]
                }
            }

            return model.value
        }

        if (props.options) {
            return model.value.split(',')
        } else {
            return model.value
        }
    },

    set(val) {
        if (!val) {
            model.value = val
        } else if (_.isArray(val)) {
            model.value = val.join(',')
        } else {
            model.value = val
        }

        return model.value
    }
})

function onFieldChange() {
    emits('change', model.value)
}
</script>

<style lang='scss' scoped></style>