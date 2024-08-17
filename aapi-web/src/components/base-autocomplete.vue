<template>
    <el-autocomplete v-model="model" clearable :placeholder="placeholder" :fetch-suggestions="fetchData" @select="onChange"
        valueKey="label"
        :disabled="disabled" />
</template>

<script setup>
import * as _ from 'lodash'

const model = defineModel()
const props = defineProps(["placeholder", "options", "disabled"])
const emits = defineEmits(["change"])

function onChange(val) {
    emits('change', val)
}

function fetchData(key, cb) {
    let options = props.options
    if (_.isFunction(options)) {
        options = options(key)
    }

    console.log(options)

    return cb(options)
}
</script>