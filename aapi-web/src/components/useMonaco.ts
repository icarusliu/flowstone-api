// import * as monaco from 'monaco-editor/esm/vs/editor/editor.main.js'
import * as monaco from "monaco-editor";  

import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker';  
import jsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker';  
import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';  
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker';  
self.MonacoEnvironment = {
  getWorker(_:any, label: string) {
    if (label == 'json') {
      return new jsonWorker();  
    } else if (label == 'javascript') {
      return new jsWorker();  
    } else if (label == 'html') {
      return new htmlWorker(); 
    }

    return new editorWorker();  
  }
}

export default function useMonaco(language = 'json') {
  let monacoEditor: monaco.editor.IStandaloneCodeEditor | null = null
  let initReadOnly = false
  const updateVal = async (val: string) => {
    monacoEditor?.setValue(val)
    setTimeout(async () => {
      initReadOnly && monacoEditor?.updateOptions({ readOnly: false })
      await monacoEditor?.getAction('editor.action.formatDocument')?.run()
      initReadOnly && monacoEditor?.updateOptions({ readOnly: true })
    }, 100)
  }

  const createEditor = (el: HTMLElement | null, editorOption: monaco.editor.IStandaloneEditorConstructionOptions = {}) => {
    if (monacoEditor) {
      return
    }
    initReadOnly = !!editorOption.readOnly
    monacoEditor = el && monaco.editor.create(el, {
      language,
      minimap: { enabled: false },
      theme: 'vs-dark',
      multiCursorModifier: 'ctrlCmd',
      scrollbar: {
        verticalScrollbarSize: 8,
        horizontalScrollbarSize: 8,
      },
      tabSize: 2,
      automaticLayout: true, // 自适应宽高
      ...editorOption
    })

    return monacoEditor
  }
  const onFormatDoc = () => {
    monacoEditor?.getAction('editor.action.formatDocument').run()
  }
  return {
    updateVal,
    getEditor: () => monacoEditor,
    createEditor,
    onFormatDoc
  }
}
