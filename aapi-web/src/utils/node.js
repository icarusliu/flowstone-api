import { ElMessage } from 'element-plus'

// 节点类型
export const nodeTypes = [
    { label: '表查询', code: 'table', icon: 'icon-table', default: {filters: []} },
    { label: 'SQL', code: 'sql', icon: 'icon-jiediansql' },
    { label: 'Http', code: 'http', icon: 'icon-http-', default: {method: 'get', params: [], body: [], headers: [], result: {type: '', converter: ''} }},
    { label: 'js', code: 'js', icon: 'icon-js' },
    { label: 'groovy', code: 'groovy', icon: 'icon-icon_Groovy' }
]

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
        let validate
        switch(options.type) {
            case "sql": 
                validate = validateSqlNode
                break
            case "table": 
                validate = validateTableNode
                break
            case "http":
                validate = validateHttpNode
                break
            case "kafka":
                validate = validateKafkaNode
                break
            case "js":
                validate = validateJsNode
                break
            case "groovy":
                validate = validateGroovyNode
                break
            default:
                console.error("未支持的节点类型")
                return callback(true)
        }

        if (validate) {
            if (!validate(options.config, options)) {
                return error(node)
            } 
        }
    }

    return callback(true)
}

// SQL节点校验
function validateSqlNode(config, {name}) {
    if (!config.ds) {
        return error(name, '未选择数据源')
    }

    if (!config.sql) {
        return error(name, '的SQL语句为空')
    }

    return true
}

// table节点校验
function validateTableNode(config, {name}) {
    if (!config.ds) {
        return error(name, '未选择数据源')
    }

    if (!config.table) {
        return error(name, '未选择表')
    }

    // 查询条件校验
    let filters = config.filters
    if (!filters) {
        return true
    }

    for (var i in filters) {
        let filter = filters[i]
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
function validateJsNode(config, {name}) {
    if (!config.js) {
        return error(name, '脚本内容为空')
    }

    return true
}

// Groovy节点校验
function validateGroovyNode(config, {name}) {
    if (!config.groovy) {
        return error(name, '脚本内容为空')
    }

    return true
}

// http节点校验
function validateHttpNode(config, {name}) {
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

        if (!param.code) {
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
function validateKafkaNode(config, {name}) {
    return true
}