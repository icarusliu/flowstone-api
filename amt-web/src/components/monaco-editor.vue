<template>
  <div class="editor-area" :class="isFull ? 'full' : ''" :style="{ width, height }">
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

<script lang='ts'>
import useMonaco from './useMonaco'
import { defineComponent, ref } from 'vue'
import { ElTooltip } from 'element-plus'

export default defineComponent({
  components: {
    ElTooltip
  },
  props: {
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '90vh'
    },
    language: {
      type: String,
      default: 'json'
    },
    preComment: {
      type: String,
      default: ''
    },
    modelValue: {
      type: String,
      default: ''
    },
    editorOptions: {
      type: Object,
      default: () => ({})
    },
  },
  watch: {
    modelValue(val) {
      val !== this.getEditor()?.getValue() && this.updateMonacoVal(val)
    },

    editorOptions(val) {
      this.editor && this.editor.updateOptions({readOnly: val.readOnly})
    }
  },
  setup(props) {
    const { updateVal, getEditor, createEditor, onFormatDoc } = useMonaco(props.language)
    const isFull = ref(false)
    return {
      isFull,
      updateVal,
      getEditor,
      createEditor,
      onFormatDoc
    }
  },

  data() {
    return {
      editor: null
    }
  },

  methods: {
    updateMonacoVal(_val?: string) {
      const { modelValue, preComment } = this.$props
      let content = _val || modelValue || ''
      const val = preComment ? `${preComment}\n${content}` : content
      this.updateVal(val)
      this.$emit('change', val); 
    }
  },
  mounted() {
    if (this.$el) {
      const monacoEditor = this.createEditor(this.$el, this.$props.editorOptions)
      this.updateMonacoVal()
      monacoEditor!.onDidChangeModelContent(() => {
        this.$emit('update:modelValue', monacoEditor!.getValue())
      })
      monacoEditor!.onDidBlurEditorText(() => {
        this.$emit('blur')
      })

      this.editor = monacoEditor
    }
  }
})
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
    box-shadow: 0 0 22px 10px rgba(0, 0, 0, .3);
    width: 90vw!important;
    height: 90vh!important;
    z-index: 9999;
  }
  .tools {
    z-index: 888;
    position: absolute;
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 0 2px;
    border-right: 1px solid rgba(0, 0, 0, .1);
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
    width: 40px!important;
  }
}
</style>
