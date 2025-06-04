<template>
    <div class="editor-area" :class="isFull ? 'full' : ''" :style="{ width, height }" ref="domRef">
        <div class="tools">
            <ElTooltip placement="right" :content="isFull ? '缩小' : '放大'">
                <div class="expand" @click="isFull = !isFull">
                    <i :class="isFull ? 'el-icon-close' : 'el-icon-full-screen'"></i>
                </div>
            </ElTooltip>
            <ElTooltip placement="right" content="格式化">
                <div class="expand" @click="onFormatDoc">
                    <i class="el-icon-finished"></i>
                </div>
            </ElTooltip>
        </div>
    </div>
</template>

<script lang="ts" setup>
import useMonaco from "./useMonaco";
import { onMounted, ref, watch } from "vue";
import { ElTooltip } from "element-plus";

const props = defineProps({
    width: {
        type: String,
        default: "100%",
    },
    height: {
        type: String,
        default: "90vh",
    },
    language: {
        type: String,
        default: "json",
    },
    preComment: {
        type: String,
        default: "",
    },
    editorOptions: {
        type: Object,
        default: () => ({}),
    },
});

const { updateVal, createEditor, onFormatDoc } = useMonaco(props.language);
let editor: any;
const isFull = ref(false);
const emits = defineEmits(["change", "blur"]);
const domRef = ref();
const model = defineModel();

watch(
    () => props.editorOptions,
    (val) => {
        editor && editor.updateOptions({ readOnly: val.readOnly });
    }
);

onMounted(() => {
    editor = createEditor(domRef.value, props.editorOptions);
    let preComment = props.preComment;
    let val = "";
    if (preComment) {
        val += preComment + "\n";
    }

    if (model.value) {
        val += model.value;
    }
    updateVal(val);

    editor.onDidChangeModelContent(() => {
        let val = editor.getValue()
        model.value = val;
        console.log(val, model.value)
    });
    editor.onDidBlurEditorText(() => {
        emits("blur");
    });
});
</script>

<style lang="scss" scoped>
.editor-area {
    position: relative;
    overflow: hidden;
    padding-left: 0;
    background-color: #fff;
    box-sizing: border-box;
    &.full {
        position: fixed;
        left: calc(10vw / 2);
        top: calc(10vh / 2);
        box-shadow: 0 0 22px 10px rgba(0, 0, 0, 0.3);
        width: 90vw !important;
        height: 90vh !important;
        z-index: 9999;
    }
    .tools {
        z-index: 888;
        position: absolute;
        display: flex;
        flex-direction: column;
        height: 100%;
        padding: 0 2px;
        border-right: 1px solid rgba(0, 0, 0, 0.1);
        left: 0;
        bottom: 0px;
        top: 0;
        .expand {
            cursor: pointer;
            line-height: 0;
            margin-top: 5px;
        }
    }

    .margin-view-overlays {
        width: 40px !important;
    }
}
</style>
