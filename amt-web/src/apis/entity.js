import * as https from '@/utils/https'

export function load(prefix, params) {
    return https.post(prefix + '/page-query', params)
}

export function query(prefix, params) {
    return https.post(prefix + '/query', params);
}

export function tree(prefix, params) {
    return https.get(prefix + '/tree', params);
}

export function save(prefix, params) {
    if (params.id) {
        return https.put(prefix + '/update', params)
    } else {
        return https.post(prefix + '/add', params)
    }
}

export function remove(prefix, id) {
    return https.del(prefix + '/delete/' + id)
}

// 根据id获取记录
export function findById(prefix, id) {
    return https.get(prefix + '/detail/' + id)
}