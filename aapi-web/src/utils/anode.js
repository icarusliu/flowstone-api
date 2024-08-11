import { Node } from 'butterfly-dag'
import $ from 'jquery'

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
        if (obj.top !== undefined) {
            node.css('top', `${obj.top}px`);
        }
        if (obj.left !== undefined) {
            node.css('left', `${obj.left}px`);
        }
        node.attr("title", options.name);
        node.html(options.name);
        node.attr("class", "flow-node iconfont " + options.icon)
        return node[0];
    }
}

export default BaseNode