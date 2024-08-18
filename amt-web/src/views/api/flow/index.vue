<template>
    <div class="api-flow d-flex bg-white mb-4">
        <div class="node-select-panel d-flex-col">
            <!-- 节点选择列表 -->
            <!-- <div class="title text-center">节点</div> -->
            <div class="nodes text-center p-2 flex-auto">
                <div v-for="type in nodeTypes" :key="type.code" class="node-item p-2 d-flex-col mb-4 cursor-pointer"
                    :draggable="true" :ondragstart="e => dragStart(e, type)">
                    <i class="iconfont" :class="type.icon" />
                    <div>{{ type.label }}</div>
                </div>
            </div>
        </div>

        <div class="flex-auto">
            <!-- 编辑器 -->
            <div id="canvas" ref="canvasRef" :ondragover="allowDrop" :ondrop="addNode">
                <!-- 右键菜单 -->
                <div class="pop-menu bg-white" ref="popMenuRef">
                    <div @click="removeNode">删除</div>
                </div>
            </div>
        </div>

        <config-panel class="config-panel" :dag="dag" :inputParams="inputParams" v-if="dag" :editing="editing" />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { createDag } from '@/utils/dag.js'
import 'butterfly-dag/dist/index.css';
import ConfigPanel from './config-panel.vue'
import { ElMessage } from 'element-plus'
import * as nodeUtils from '@/utils/node.js'

const nodeTypes = ref(nodeUtils.nodeTypes)
const canvasRef = ref()
const props = defineProps(["editing", "inputParams"])
const dag = ref({})
const popMenuRef = ref()
const nodes = defineModel()

onMounted(() => {
    dag.value = createDag(canvasRef.value, popMenuRef)
    dag.value.loadNodes(nodes.value)

    document.addEventListener("click", () => {
        popMenuRef.value && (popMenuRef.value.style.display = 'none')
    })
})

watch(() => props.editing, (val) => {
    if (val) {
        dag.value.startEdit()
    } else {
        dag.value.stopEdit()
    }
})

watch(() => nodes.value, (val) => {
    dag.value.loadNodes(val)
}, {
    immediate: false,
    deep: true
})

// 添加节点
function addNode(e) {
    const type = JSON.parse(e.dataTransfer.getData('node'))
    dag.value.addNode(type, {
        top: e.offsetY,
        left: e.offsetX
    })
}

// 启动拖拉
function dragStart(e, type) {
    e.dataTransfer.setData("node", JSON.stringify(type))
}

// 获取配置内容
function getNodes() {
    return dag.value.getNodes()
}

// 允许拖放
function allowDrop(e) {
    e.preventDefault()
}

// 删除节点
function removeNode() {
    dag.value.removeCurrent()
}

// 参数校验
function validate(callback) {
    // 检查所有节点配置是否完整
    nodeUtils.validateNodes(dag.value, callback)
}

function update() {
    dag.value.loadNodes(nodes.value)
}

defineExpose({ getNodes, validate, update })
</script>

<style lang="scss" scoped>
.api-flow {
    --border-color: #ddd;

    height: calc(100vh - 210px);
    border: 1px solid var(--border-color);
}

.node-select-panel {
    width: 68px;
    min-width: 68px;
    background: #fdfdfd;
    border-right: 1px solid var(--border-color);

    .title {
        border-bottom: 1px solid var(--border-color);
        line-height: 30px;
        background-color: #fdfdfd;
    }

    .nodes {
        font-size: 12px;

        .node-item {
            align-items: center;

            .iconfont {
                font-size: 20px;
            }
        }

        .node-item:hover {
            background-color: #fff;
            color: var(--primary_color);
        }
    }
}

.config-panel {
    width: 800px;
    border-left: 1px solid var(--border-color);
}

.pop-menu {
    position: absolute;
    display: none;
    z-index: 600;
    line-height: 30px;
    width: 80px;
    text-align: center;
    margin-left: -40px;
    box-shadow: 0 0 5px #ddd;
    padding: 4px 0;

    >div {
        font-size: 12px;
        cursor: pointer;

        &:hover {
            background: #efefef
        }
    }
}
</style>

<style lang="scss">
#canvas {
    position: relative;
    height: 100%;
    width: 100%;
    background-color: #efefef;
}

.flow-node {
    width: 140px;
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

.butterfly-svg {
    position: absolute;
}
</style>