<template>
    <el-select v-model="model" @change="doChanged" :value-key="valueKey">
        <el-option v-for="option in finalOptions" 
            :value="option.value || option.id" 
            :label="option.label || option.name" 
            ></el-option>
    </el-select>
</template>

<script setup>
import { defineProps, defineEmits, ref, onMounted } from 'vue'

const props = defineProps(["options"])
const model = defineModel()
const emits = defineEmits(["change"])
const finalOptions = ref([])
const valueKey = ref()

onMounted(() => {
    loadOptions().then(resp => {
        finalOptions.value = resp;
    })
})

function loadOptions() {
    const options = props.options;
    if (!options) {
        return Promise.resolve([]);
    }

    if (options instanceof Array) {
        // 是直接数据
        return Promise.resolve(options);
    } else if (options instanceof Function) {
        // 是函数
        const result = options()
        if (result.then) {
            return result
        } else {
            return Promise.resolve(result)
        }
    } else if (options.type) {
        // 是对象
        valueKey.value = options.valueKey
        if (options.type == 'js' || options.type == 'func') {
            return iv.callFunc(options.data);
        } else {
            return Promise.resolve(options.data || [])
        }
    }
}

function doChanged() {
    emits('change', ...arguments)
}
</script>