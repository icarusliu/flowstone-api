import * as https from '@/utils/https'

export function offline(id) {
    return https.get('/base/api-draft/offline/' + id)
}

export function publish(id) {
    return https.get('/base/api-draft/publish/' + id)
}

export function listDs() {
    return https.post('/base/ds/query', {})
}

// 获取数据源表清单
export function listTables(ds) {
    return https.get('/base/ds/tables', {ds})
}

// 获取表字段列表
export function listTableFields(ds, table) {
    return https.get('/base/ds/table-fields', {ds, table})
}

// 测试接口
export function testApi(method, path, params) {
    if (method == 'get') {
        return https.get('/dua-test/' + path, params)
    } else {
        return https.post('/dua-test/' + path, params)
    }
}

// 查询接入方列表
export function getSuppliers() {
    return https.post('/base/supplier/query')
}