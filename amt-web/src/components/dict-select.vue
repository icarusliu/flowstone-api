<template>
    <!-- 字典项选择器 -->
    <base-select v-if="items.length >= 0" v-model="finalModel" :options="items" @change="doChange" :disabled="disabled" />
    <base-radio-group v-else v-model="finalModel" :options="items" @change="doChange" :disabled="disabled" />
</template>
<script setup>
    import { useSysStore } from '../store';
    import baseSelect from './base-select.vue';
    import baseRadioGroup from './base-radio-group.vue';
    import { onMounted } from 'vue';

    const model = defineModel()
    const sysStore = useSysStore()
    const props = defineProps({
        dict: String,
        disabled: Boolean,
        withDefault: {
            type: Boolean,
            default: true
        },
        dataType: {type: String, default: 'string'}
    })
    const emits = defineEmits(['change'])
    const items = computed(() => {
        const dictInfo = sysStore.getDictInfo()
        return dictInfo[props.dict]?.items || []
    })

    const finalModel = computed({
        get() {
            if (model.value == undefined) {
                return model.value
            }
            return model.value + ''
        },
        set(val) {
            let dataType = props.dataType 
            if (dataType == 'int') {
                model.value = parseInt(val)
            } else {
                model.value = val
            }
        }
    })

    onMounted(() => {
        if (props.withDefault) {
            if (!model.value && model.value != 0 && items.value.length) {
                model.value = items.value[0].value
            }
        }
    })

    function doChange() {
        emits('change', model.value)
    }
</script>

<style lang='scss' scoped></style>