import { Canvas, Tips } from 'butterfly-dag';
import BaseNode from './anode.js'
import 'butterfly-dag/dist/index.css';
import $ from 'jquery'
import * as _ from 'lodash'
import * as uuid from 'uuid'
import { ref } from 'vue';
import * as utils from '@/utils/utils.js'

export function createDag(dom, popMenuRef) {
    let currentNode = ref()
    let editing = ref(false)

    let canvas = new Canvas({
        root: dom,              //canvas的根节点(必传)
        zoomable: false,         //可缩放(可传)
        moveable: false,         //可平移(可传)
        draggable: false,        //节点可拖动(可传)
        disLinkable: false,
        linkable: false, // 节点可连线
        theme: {
            edge: {
                shapeType: 'AdvancedBezier',
                arrow: true
            }
        }
    });

    canvas.draw({
        groups: [],  //分组信息
        nodes: [],  //节点信息 
        edges: []  // 连线信息
    })

    // 节点选择
    const nodeSelected = (node) => {
        if (currentNode.value) {
            const lastDom = currentNode.value.dom
            $(lastDom).attr('class', 'flow-node iconfont ' + currentNode.value.options.icon)
        }

        if (!node) {
            currentNode.value = null;
            return
        }

        const dom = node.dom
        $(dom).attr('class', 'flow-node active iconfont ' + node.options.icon)
        currentNode.value = node
    }

    // 事件处理
    canvas.on('system.node.click', ({ node }) => nodeSelected(node))
    canvas.on('system.node.move', ({ nodes }) => nodeSelected(nodes[0]))
    canvas.on('system.canvas.click', () => {
        // 拖动开始时隐藏菜单
        $(popMenuRef.value).hide()
    })
    canvas.on('system.nodes.add', ({ nodes }) => {
        // 为每个节点增加菜单
        nodes.forEach(node => {
            const dom = node.dom
            dom.addEventListener("contextmenu", e => {
                if (!editing.value) {
                    return
                }

                e.preventDefault()
                // 同时节点需要选中
                nodeSelected(node)

                // 位置同时需要考虑butterfly-wrapper的位置
                let parent = dom.parentElement

                $(popMenuRef.value)
                    .css('left', e.offsetX + node.left + parseInt(parent.style.left || 0))
                    .css('top', e.offsetY + node.top + parseInt(parent.style.top || 0))
                    .show()
            })
        })
    })

    function setEditing(editing) {
        canvas.setMoveable(editing)
        canvas.setDraggable(editing)
        canvas.setLinkable(editing)
        canvas.setDisLinkable(editing)
    }

    // 重新选中节点
    function reselectNode(id) {
        let nodes = canvas.getDataMap().nodes
        let node = nodes.find(node => node.id === id)
        nodeSelected(node)
    }

    return {
        currentNode,

        // 开始编辑
        startEdit() {
            editing.value = true
            setEditing(true)
        },

        // 停止编辑
        stopEdit() {
            editing.value = false
            setEditing(false)
        },

        addNode: (baseInfo, config = {}) => {
            let nodeSize = canvas.getDataMap().nodes.length

            const nodeInfo = {
                id: uuid.v4(),
                name: baseInfo.label,
                code: 'node' + (nodeSize + 1),
                type: baseInfo.code,
                typeName: baseInfo.label,
                icon: baseInfo.icon,
                top: config.top || 200,
                left: config.left || 200,
                Class: BaseNode,
                endpoints: [
                    { id: 'right', orientation: [1, 0] },
                    { id: 'left', orientation: [-1, 0] },
                ],
                config: baseInfo.default || {}
            }
            // 添加新节点
            canvas.addNode(nodeInfo)
        },

        getNodes() {
            // 组装节点内容
            let { edges, nodes } = canvas.getDataMap()
            let resultNodes = []
            let nodeMap = {}
            nodes.forEach(node => {
                let options = node.options
                let resultNode = {
                    id: node.id,
                    code: options.code,
                    name: options.name,
                    icon: options.icon,
                    type: options.type,
                    left: node.left,
                    top: node.top,
                    parentIds: [],
                    config: options.config || {}
                }

                resultNodes.push(resultNode)
                nodeMap[resultNode.id] = resultNode
            })

            // 处理节点连线
            edges.forEach(edge => {
                let target = edge.targetNode
                let source = edge.sourceNode
                nodeMap[target.id].parentIds.push(source.id)
            })

            return resultNodes
        },

        loadNodes(nodes) {
            if (!nodes) {
                nodeSelected()
                return
            }

            let resultNodes = []
            let edges = []
            let nodeMap = {}
            nodes.forEach(node => {
                let resultNode = {
                    id: node.id,
                    code: node.code,
                    name: node.name,
                    icon: node.icon,
                    type: node.type,
                    typeName: node.typeName,
                    top: node.top,
                    left: node.left,
                    Class: BaseNode,
                    endpoints: [
                        { id: 'right', orientation: [1, 0], type: 'source' },
                        { id: 'left', orientation: [-1, 0], type: 'target' },
                    ],
                    config: node.config || {}
                }

                resultNode.config.filters = resultNode.config.filters || []
                resultNodes.push(resultNode)

                nodeMap[node.id] = resultNode
            })

            // 处理连接线
            nodes.forEach(node => {
                if (!node.parentIds) {
                    return
                }

                node.parentIds.forEach(parentId => {
                    edges.push({
                        type: 'endpoint',
                        sourceNode: parentId,
                        targetNode: node.id,
                        target: 'left',
                        source: 'right'
                    })
                })
            })

            canvas.redraw({
                nodes: resultNodes,
                edges
            }, () => {
                // 如果当前已选中节点，为防止参数修改，需要重新选中节点
                if (currentNode.value) {
                    reselectNode(currentNode.value.id)
                }
            })
        },

        removeNode(id) {
            canvas.removeNode(id)
        },

        removeCurrent() {
            canvas.removeNode(currentNode.value.id)
        },
        
        // 获取当前节点的上级节点列表
        getCurrentUpNodes() {
            if (!currentNode.value) {
                return []
            }

            let {nodes, edges} = canvas.getDataMap()
            let parentIds = {}, nodeMap = {}
            edges.forEach(edge => {
                let target = edge.targetNode, 
                    source = edge.sourceNode
                
                let targetParentIds = parentIds[target.id] 
                if (!targetParentIds) {
                    targetParentIds = parentIds[target.id] = []
                }

                targetParentIds.push(source.id)
            })

            nodes.forEach(node => nodeMap[node.id] = node)

            let parentNodes = []
            findUp(currentNode.value.id, nodeMap, parentIds, parentNodes)
            return parentNodes
        },

        // 获取原始的节点、连线等数据
        getOriginDataMap() {
            return canvas.getDataMap()
        },

        // 设置当前选中的节点
        selectNode(node) {
            nodeSelected(node)
        },
    }
}

/**
 * 根据id查找上级节点列表
 * @param {*} id 当前节点id
 * @param {*} nodeMap  节点信息Map
 * @param {*} parentIdMap 父节点信息Map
 * @param {*} result 找到的上级节点列表
 * @returns 
 */
function findUp(id, nodeMap, parentIdMap, result) {
    let parentIds = parentIdMap[id]
    if (!parentIds) {
        return
    }

    parentIds.forEach(parentId => {
        result.push(nodeMap[parentId])

        findUp(parentId, nodeMap, parentIdMap, result)
    })
}
