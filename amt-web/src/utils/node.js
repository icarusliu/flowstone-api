import { ElMessage } from 'element-plus'
import * as _ from 'lodash'
import * as uuid from 'uuid'

// 节点类型
export const nodeTypes = [
    { label: '表查询', code: 'table', icon: 'icon-table', default: { params: [] } },
    { label: 'SQL', code: 'sql', icon: 'icon-jiediansql', default: { params: [] } },
    {
        label: 'Http', code: 'http', icon: 'icon-http-', default: {
            method: 'get',
            params: [],
            body: [],
            headers: [],
            result: { type: '', converter: '' },
            batch: false
        }
    },
    { label: 'js', code: 'js', icon: 'icon-js', default: {
        js: `function func(params) { 
    return "hello world" 
}`
    } },
    { label: 'groovy', code: 'groovy', icon: 'icon-icon_Groovy', default: {
        groovy: `def func(params) { 
    return "hello world" 
}`
    } }
]

// 加载通用的节点配置参数（使用NodeParam组件配置的参数）
function loadCommonRequestParams(params, result = []) {
    if (!params) {
        return result
    }

    params.forEach(param => {
        if (param.type == 'js') {
            loadScriptRequestParams(param.value, result)
            loadScriptRequestParams(param.value1, result)
        } else if (param.type == 'request') {
            param.value && result.push(param.value)
            param.value1 && result.push(param.value1)
        }
    })

    return result
}

/**
 * 获取脚本中的请求参数引用
 * @param {*} script 脚本内容
 * @param {*} result 请求参数引用列表
 * @returns 命中列表
 */
function loadScriptRequestParams(script, result = []) {
    if (!script) {
        return result
    }

    let arr = script.match(/(?<=[/. ]request.)[a-zA-Z0-9_]+/g)
    arr && result.push(...arr)

    return result
}

// 支持的节点集合
const nodeMap = {
    sql: {
        validate: validateSqlNode,
        loadParams: ({ config }, result) => loadCommonRequestParams(config.params, result)
    },
    table: {
        validate: validateTableNode,
        loadParams: ({ config }, result) => loadCommonRequestParams(config.params, result)
    },
    http: {
        validate: validateHttpNode,
        loadParams: ({ config }, result = []) => {
            loadCommonRequestParams(config.params, result)
            loadCommonRequestParams(config.headers, result)
            loadCommonRequestParams(config.body, result)
            loadCommonRequestParams(config.pathVariables, result)

            return result
        }
    },
    js: {
        validate: validateJsNode,
        loadParams: ({ config }, result = []) => {
            return loadScriptRequestParams(config.js, result)
        }
    },
    groovy: {
        valdiate: validateGroovyNode,
        loadParams: ({ config }, result = []) => {
            return loadScriptRequestParams(config.groovy, result)
        }
    },
    kafka: {
        validate: validateKafkaNode,
        loadParams: ({ config }, result) => {
            return result
        }
    }
}

// 节点校验
export function validateNodes(dag, callback) {
    const nodes = dag.getOriginDataMap().nodes
    if (!nodes) {
        ElMessage.error("流程配置为空")
        return callback(false)
    }

    const error = (node, msg) => {
        msg && ElMessage.error(msg)
        dag.selectNode(node)
        return callback(false)
    }

    for (var i in nodes) {
        let node = nodes[i]
        let options = node.options

        if (!options.name) {
            return error(node, '节点名称不能为空')
        }

        if (!options.code) {
            return error(node, "节点编码不能为空")
        }

        if (!options.config) {
            return error(node, '节点配置为空')
        }

        // 根据不同类型检查节点参数
        let validate = nodeMap[options.type]?.validate

        if (validate) {
            if (!validate(options)) {
                return error(node)
            }
        }
    }

    return callback(true)
}

/**
 * 从节点中加载请求参数，自动添加到测试数据中 
 * @param {*} dag 流程节点信息
 * @param {*} testData 测试数据
 * @returns 
 */
export function loadParams(method, nodes, testData) {
    if (!nodes) {
        ElMessage.error("流程配置为空")
        return callback(false)
    }

debugger

    let params = []
    nodes.forEach(node => {
        // 根据节点类型进行处理
        const type = node.type
        const nodeFunc = nodeMap[type]
        if (nodeFunc && nodeFunc.loadParams) {
            nodeFunc.loadParams(node, params)
        }
    })

    if (!params || !params.length) {
        return
    }

    if (method == 'get') {
        // 加载已存在的参数值
        let paramValue = {}
        if (testData.queryParams) {
            testData.queryParams.forEach(param => {
                paramValue[param.code] = param.value
            })
        }

        // 需要去重处理
        let queryParams = testData.queryParams = []
        let processed = []
        params.forEach(param => {
            if (processed.indexOf(param) != -1) {
                return
            }

            processed.push(param)

            queryParams.push({
                id: uuid.v4(),
                code: param,
                value: paramValue[param]
            })
        })
    } else {
        let oldBody = JSON.parse(testData.body || '{}')
        if (!_.isObject(oldBody)) {
            return
        }

        let body = {}
        params.forEach(param => {
            body[param] = oldBody[param]
        })
        testData.body = JSON.stringify(body)
    }
}

// SQL节点校验
function validateSqlNode({config, name}) {
    if (!config.ds) {
        return error(name, '未选择数据源')
    }

    if (!config.sql) {
        return error(name, '的SQL语句为空')
    }

    return true
}

// table节点校验
function validateTableNode({config, name}) {
    if (!config.ds) {
        return error(name, '未选择数据源')
    }

    if (!config.table) {
        return error(name, '未选择表')
    }

    // 查询条件校验
    let params = config.params
    if (!params) {
        return true
    }

    for (var i in params) {
        let filter = params[i]
        if (!filter.key) {
            return error(name, '查询条件中存在字段为空的记录')
        }

        let op = filter.op
        if (op == 'null' || op == 'notNull') {
            // 为空或不为空不需要继续检查
            continue
        }

        if (!filter.value) {
            return error(name, '查询条件中存在参数值为空的记录')
        }

        if (op == 'between' && !filter.value1) {
            return error(name, '查询条件中，操作符如果是两者之中，参数值中两个参数必须全部不为空')
        }
    }

    return true
}

// JS节点校验
function validateJsNode({ config, name }) {
    if (!config.js) {
        return error(name, '脚本内容为空')
    }

    return true
}

// Groovy节点校验
function validateGroovyNode({ config, name }) {
    if (!config.groovy) {
        return error(name, '脚本内容为空')
    }

    return true
}

// http节点校验
function validateHttpNode({ config, name }) {
    if (!config.supplier) {
        return error(name, '接入方不能为空')
    }

    if (!config.path) {
        return error(name, '接口路径不能为空')
    }

    // 路径参数校验
    let b = validateParams(config.pathVariables, name)
    if (!b) {
        return b
    }

    // 查询参数校验
    if (config.method == 'get' || config.method == 'delete') {
        b = validateParams(config.params, name)
        if (!b) {
            return false
        }
    } else {
        // 请求体校验
        b = validateParams(config.body, name)
        if (!b) {
            return false
        }
    }

    // 请求头校验
    return validateParams(config.headers, name)
}

// 参数校验
function validateParams(params, name) {
    if (!params || !params.length) {
        return true
    }

    for (var i in params) {
        const param = params[i]
        if (!param.value && param.value != 0) {
            return error(name, '路径参数值不能为空')
        }

        if (param.type == 'node' && !param.nodeCode) {
            // 来源是节点时，节点编码不能为空
            return error(name, '路径参数中节点未选择')
        }

        if (!param.key) {
            return error(name, '参数不能为空')
        }
    }

    return true;
}

function error(name, msg) {
    ElMessage.error(`节点${name}${msg}`)
    return false
}

// kafka节点校验
function validateKafkaNode({ config, name }) {
    return true
}

