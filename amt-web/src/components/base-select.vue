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
import * as _ from 'lodash'

const props = defineProps(["options", "valueKey"])
const model = defineModel()
const emits = defineEmits(["change"])
const finalOptions = ref([])
const valueKey = ref(props.valueKey)

onMounted(() => {
    loadOptions().then(resp => {
        finalOptions.value = resp;
    })
})


if (_.isArray(props.options)) {
    // 防止数据出现变化时未重新加载
    // 但函数返回的数据出现变化时，暂时不能进行监听与处理，尤其是异步函数
    watch(() => props.options, () => {
        finalOptions.value = props.options
    }, {
        deep: true
    })
}

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

function doChanged(val) {
    // 需要匹配到整个对象，作为第二个参数
    let item = _.find(finalOptions.value, item => item.value||item.id == val)

    emits('change', val, item)
}
</script>