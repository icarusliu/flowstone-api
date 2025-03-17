import { Canvas, Tips, Node } from 'butterfly-dag';
import 'butterfly-dag/dist/index.css';
import $ from 'jquery'
import * as _ from 'lodash'
import * as uuid from 'uuid'
import { ref } from 'vue';
import * as utils from '@/utils/utils.js'

function loadAllNodes(nodes, resultNodes, edges) {
    if (!nodes) {
        return
    }

    nodes.forEach(node => {
        let resultNode = {
            id: node.jobId,
            code: node.jobCode,
            name: node.jobName,
            // top: node.top,
            // left: node.left,
            usedTables: node.usedTables,
            updatedTables: node.updatedTables,
            Class: BaseNode,
            endpoints: [
                { id: 'left', orientation: [1, 0], type: 'source' },
                { id: 'right', orientation: [-1, 0], type: 'target' },
            ]
        }

        resultNodes.push(resultNode)

        // 处理连接线
        if (node.parentIds) {
            node.parentIds.forEach(parentId => {
                edges.push({
                    type: 'endpoint',
                    sourceNode: parentId,
                    targetNode: node.jobId,
                    target: 'right',
                    source: 'left'
                })
            })
        }

        // 加载子元素
        if (node.children) {
            loadAllNodes(node.children, resultNodes, edges);
        }
    })
}

export function createDag(dom) {
    let canvas = new Canvas({
        root: dom,              //canvas的根节点(必传)
        zoomable: true,         //可缩放(可传)
        moveable: true,         //可平移(可传)
        draggable: true,        //节点可拖动(可传)
        disLinkable: false,
        linkable: false, // 节点可连线
        theme: {
            edge: {
                shapeType: 'AdvancedBezier',
                arrow: true
            }
        },
        layout: {
            type: 'dagreGroupLayout',
            options: {
                rankdir: 'LR',
                nodesep: 50,
                ranksep: 80,
                controlPoints: false,
            },
        },
    });

    canvas.draw({
        nodes: [],  //节点信息 
        edges: []  // 连线信息
    })

    return {
        loadNodes(nodes) {
            if (!nodes) {
                return
            }

            let resultNodes = []
            let edges = []

            loadAllNodes(nodes, resultNodes, edges)

            canvas.redraw({
                nodes: resultNodes,
                edges
            }, () => {

            })
        },
    }
}

class BaseNode extends Node {
    draw = (obj) => {
        let _dom = obj.dom;
        let options = obj.options
        if (!_dom) {
            _dom = $('<div></div>')
                .attr('class', 'flow-node')
                .attr('id', obj.id);
        }
        const node = $(_dom);
        node.css('top', `${obj.top}px`);
        node.css('left', `${obj.left}px`);
        node.attr("title", options.name);
        node.html(`<span><label>${options.name}</label>
            <label>${options.code}</label></span>`);
        return node[0];
    }
}