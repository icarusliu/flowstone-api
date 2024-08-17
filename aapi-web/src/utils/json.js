import * as _ from 'lodash'
import * as uuid from 'uuid'

/**
 * 从JSON串中加载参数配置
 * @param {*} str 
 */
export function loadJsonSchema(str) {
    if (_.isArray(str)) {
        // 加载数组配置
        let result = loadArray(str)
        result.code = '*'
        return [result]
    } else if (_.isObject(str)) {
        // 加载对象配置
        return loadObjectSchema(str).children
    } else if (_.isNumber(str)) {
        return [{
            id: uuid.v4(),
            code: '*',
            type: 'float'
        }]
    } else {
        return [
            {
                id: uuid.v4(),
                code: '*',
                type: 'string'
            }
        ]
    }
}

function loadObjectSchema(obj) {
    let children = []
    let result = {
        id: uuid.v4(),
        code: '',
        name: '对象',
        type: 'object',
        children
    }
    _.forIn(obj, (value, key) => {
        if (_.isObject(value)) {
            let sub = loadObjectSchema(value)
            sub.code = key
            children.push(sub)
        } else if (_.isArray(value)) {
            let sub = loadArray(value)
            sub.code = key
            children.push(sub)
        } else if (_.isNumber(value)) {
            children.push({
                id: uuid.v4(),
                code: key,
                type: 'float',
                name: key
            })
        } else {
            children.push({
                id: uuid.v4(),
                code: key,
                type: 'string',
                name: key
            })
        }
    })

    return result
}

function loadArray(arr) {
    let children = []
    let result = {
        id: uuid.v4(),
        code: '',
        name: '数组',
        type: 'array',
        children
    }

    if (_.isEmpty(arr)) {
        children.push({
            id: uuid.v4(),
            code: '*',
            type: 'unknown',
            name: '数组子元素'
        })

        return result
    }

    let obj = arr[0]
    if (_.isObject(obj)) {
        let sub = loadObjectSchema(obj)
        sub.code = '*'
        children.push(sub)
        return result
    }

    if (_.isArray(obj)) {
        let sub = loadArray(obj)
        sub.code = '*'
        children.push(sub)
        return result
    }

    // 数组中存储的是直接数据
    let type = 'string'
    if (_.isNumber(obj)) {
        type = 'float'
    }
    children.push({
        id: uuid.v4(),
        code: '*',
        type,
        name: '数组子元素'
    })

    return result
}