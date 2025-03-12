<template>
<ElDialog v-model="visible" :title="title || '新增'" width="600px">
    <el-input :type="type" :rows="rows" v-model="value" ref="inputRef" @keyup.enter.native="keyup"/>

    <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="doOk">确定</el-button>
    </template>
</ElDialog>
</template>

<script setup>
const visible = defineModel()
const props = defineProps(['type', 'title', 'rows', 'content'])
const value = ref()
const emits = defineEmits(['ok'])
const inputRef = ref()

watch(() => props.content, (val) => {
    value.value = val
})

watch(visible, (val) => {
    if (!val) {
        return
    }

    value.value = props.content || ''
    setTimeout(() =>{
        inputRef.value.focus()
    }, 0)
})

function keyup(e) {
    if (e.ctrlKey) {
        doOk()
    }
}

function doOk() {
    emits('ok', value.value)
    visible.value = false
}
</script>