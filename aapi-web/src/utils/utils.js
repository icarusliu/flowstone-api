import * as _ from 'lodash'

// 遍历对象及子元素
export function loop(item, func, parent) {
    if (!item || !func) {
        return
    }

    if (_.isArray(item)) {
        for (var i in item) {
            let subItem = item[i]
            let b = loop(subItem, func, parent)
            if (b) {
                // 中断执行
                return true
            }
        }
        return
    } 

    let b = func(item, parent)
    if (b) {
        // 中断执行
        return true
    }
    return loop(item.children, func, item)
}

// 从树中删除符合条件的元素
export function removeFromTree(tree, predicate) {
    let removed = _.remove(tree, predicate)
    if (removed && removed.length) {
        return true
    }

    // 当前层级没找到，则继续处理下一层级的数据
    for (var i in tree) {
        let sub = tree[i]
        if (!sub.children) {
            continue
        }

        removed = removeFromTree(sub.children, predicate)
        if (removed) {
            return true
        }
    }

    return false
}