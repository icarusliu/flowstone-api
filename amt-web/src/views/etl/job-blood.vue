<template>
    <full-panel title="任务血缘分析" @close="close">
        <div id="canvas" ref="canvasRef" class="blood-canvas">
        </div>
        <div class="text-center mt-4">
            <el-button @click="close">关闭</el-button>
        </div>
    </full-panel>
</template>
<script setup>
import fullPanel from '@/components/full-panel.vue'
import https from '@/utils/https'
import { createDag } from './job-blood.js'

const emits = defineEmits(['close'])
const props = defineProps({
    job: { type: Object }
})
const canvasRef = ref()
const dag = ref({})

onMounted(() => {
    https.get('/etl/job/blood-tree', { id: props.job.id }).then(resp => {
        dag.value = createDag(canvasRef.value)
        dag.value.loadNodes(resp)
    })
})

function close() {
    emits('close')
}

</script>

<style lang='scss'>
.blood-canvas {
    position: relative;
    height: 80vh;
    width: 100%;
    background-color: #efefef;
}

.flow-node {
    width: 200px;
    height: 50px;
    cursor: pointer;
    border: 1px solid #bbb;
    background-color: #fff;
    border-radius: 2px;
    padding: 8px;
    box-sizing: border-box;
    position: absolute;
    font-size: 13px;
    display: flex;
    align-items: center;
    text-overflow: ellipsis;
    -webkit-line-clamp: 1;
    display: -webkit-box;
    overflow: hidden;
    word-break: keep-all;

    &::before {
        font-size: 24px;
        margin-right: 16px;
    }

    &.active {
        border-color: blue;
        background: #c1d4ff;
    }

    >span {
        display: flex;
        flex-direction: column;
        pointer-events: none;
        label:last-child {
            color: gray;
            font-size: 12px;
        }
    }
}

</style>