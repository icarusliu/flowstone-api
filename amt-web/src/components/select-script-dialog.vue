<!-- 脚本编辑 -->
<template>
    <!-- 脚本编辑器 -->
    <el-dialog v-model="visible" :title="'脚本编辑_' + lang" :append-to-body="true" width="80%" destroy-on-close>
        <template #header>
            <span class="font-bold mr-2">脚本编辑——{{ lang }}</span>
            <slot name="header"></slot>
        </template>

        <div class="mb-2 v-center">
            <span>脚本类型：</span>
            <el-radio-group v-model="model.scriptType" @change="changeType">
                <el-radio-button label="JS" value="javascript"/>
                <el-radio-button label="Groovy" value="groovy"/>
            </el-radio-group>
        </div>
        
        <div class="mb-4">脚本内容：</div>
        <monacoEditor height="65vh" v-model="script" language="javascript" v-if="lang == 'javascript'"/>
        <monacoEditor height="65vh" v-model="script" language="groovy" v-else/>
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

const props = defineProps(["readonly", "defJs", "defGroovy"])
const script = ref()
const visible = defineModel("visible")
const model = defineModel({
})

watch(visible, (val) => {
    if (val) {
        script.value = _.cloneDeep(model.value.script)
    }
})

const lang = computed(() => model.value?.scriptType || 'javascript')

watch(() => model.value, (val, oldVal) => {
    if (!val) {
        let lang = model.value?.scriptType
        if (lang == 'js' || lang == 'javascript') {
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

function changeType() {
    let type = model.value.scriptType 
    if (type == 'javascript' || type == 'js') {
        if (props.defJs) {
            script.value = _.cloneDeep(props.defJs)
        }
    } else {
        if (props.defGroovy) {
            script.value = _.cloneDeep(props.defGroovy)
        }
    }
}

// 保存脚本
function doSaveScript() {
    model.value.script = script.value
    visible.value = false
}
</script>