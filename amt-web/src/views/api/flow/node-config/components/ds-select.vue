<template>
    <!-- 数据源配置 -->
    <el-select v-model="model" @change="changed">
        <el-option v-for="ds in list" :key="ds.id" :label="ds.name" :value="ds.code" />
    </el-select>
</template>

<script setup>
import * as apiApis from '@/apis/api.js'

const model = defineModel()
const list = ref([])
const emits = defineEmits(["change"])

onMounted(() => {
    apiApis.listDs().then(resp => {
        list.value = resp
    })
})

function changed(val) {
    console.log(val)
    emits('change', val);
}
</script>