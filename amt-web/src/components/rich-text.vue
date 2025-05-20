<template>
    <div class="container">
        <Toolbar class="toolbar" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
        <Editor
            class="editor"
            v-model="model"
            :defaultConfig="editorConfig"
            :mode="mode"
            @onCreated="handleCreated"
            :style="{ height: height }"
            @onChange="onChange"
        />
    </div>
</template>
<script setup>
// import "@wangeditor/editor/dist/css/style.css"; // 引入 css
// import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { onMounted, shallowRef, watch } from "vue";
import { ElLoading } from "element-plus";

const props = defineProps({
    disabled: Boolean,
    height: String,
});
const emits = defineEmits("change");
const editorRef = shallowRef();
const model = defineModel();
const toolbarConfig = {};
const mode = "default";
let loading;
const editorConfig = ref({
    placeholder: "请输入内容...",
    MENU_CONF: {
        uploadImage: {
            server: "/api/base/sys-file/upload/wangeditor",
            fieldName: "file",
            withCredentials: true,
            maxFileSize: 20 * 1024 * 1024,
            timeout: 30 * 1000,
            onBeforeUpload: (file) => {
                loading = ElLoading.service();
            },
            onProgress: (p) => {
                loading && loading.setText(p + "%");
            },
            onSuccess: () => {
                loading && loading.close();
            },
            onFailed: () => {
                loading && loading.close();
            },
            onError: () => {
                loading && loading.close();
            },
        },uploadVideo: {
            server: "/api/base/sys-file/upload/wangeditor",
            fieldName: "file",
            withCredentials: true,
            maxFileSize: 500 * 1024 * 1024,
            timeout: 120 * 1000,
            onBeforeUpload: (file) => {
                loading = ElLoading.service();
            },
            onProgress: (p) => {
                loading && loading.setText(p + "%");
            },
            onSuccess: () => {
                loading && loading.close();
            },
            onFailed: () => {
                loading && loading.close();
            },
            onError: () => {
                loading && loading.close();
            },
        },
    },
});
const handleCreated = (editor) => {
    editorRef.value = editor; // 记录 editor 实例，重要！
    reloadDisabled();
};

watch(() => props.disabled, reloadDisabled, {
    immediate: true,
});

function reloadDisabled() {
    if (!editorRef.value) {
        return;
    }
    if (props.disabled) {
        editorRef.value.disable();
    } else {
        editorRef.value.enable();
    }
}

onBeforeUnmount(() => {
    const editor = editorRef.value;
    if (editor == null) return;
    editor.destroy();
});

function onChange(val) {
    emits("change", val.getHtml());
}
</script>

<style lang="scss" scoped>
.container {
    border: 1px solid #ddd;
    z-index: 12;

    .toolbar {
        border-bottom: 1px solid #ddd;
    }

    .editor {
        min-height: 200px;
        overflow-y: auto;
    }
}
</style>
