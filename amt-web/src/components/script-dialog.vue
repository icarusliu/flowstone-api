<!-- 脚本编辑 -->
<template>
    <!-- 脚本编辑器 -->
    <el-dialog v-model="visible" :title="'脚本编辑_' + lang" :append-to-body="true" width="80%">
        <monacoEditor height="65vh" v-model="script" :language="lang" />
        <template #footer>
            <div>
                <el-link type="primary" class="mr-4" @click="visible = false">取消</el-link>
                <el-button type="primary" @click="doSaveScript" :disabled="readonly">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import monacoEditor from './monaco-editor.vue'
import * as _ from 'lodash'

const props = defineProps(["lang", "readonly"])
const script = ref()
const visible = defineModel("visible")
const model = defineModel()

watch(visible, (val) => {
    if (val) {
        script.value = _.cloneDeep(model.value)
    }
})

watch(() => model.value, (val, oldVal) => {
    if (!val) {
        let lang = props.lang
        if (lang == 'js') {
            val = `function func(params) {
    return {
        a: 1
    }
}`
        } else if (lang == 'groovy') {
            val = `def func(params) {
    return [
        a: 1
    ]
}`
        } else if (lang == 'json') {
            val = '{}'
        }

        script.value = val
        return
    }

    if (_.isEqual(val, oldVal)) {
        return
    }

    script.value = _.cloneDeep(val)
}, {
    immediate: true
})

// 保存脚本
function doSaveScript() {
    model.value = script.value
    visible.value = false
}
</script>