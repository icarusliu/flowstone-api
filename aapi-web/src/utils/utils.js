import * as _ from 'lodash'

// 遍历对象及子元素
export function loop(item, func) {
    if (!item || !func) {
        return
    }

    if (_.isArray(item)) {
        item.forEach(subItem => loop(subItem, func))
        return
    } 

    func(item)
    loop(item.children, func)
}