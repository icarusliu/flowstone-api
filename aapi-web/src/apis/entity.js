import * as https from '@/utils/https'

export function load(prefix, params) {
    return https.post(prefix + '/page-query', params)
}

export function tree(prefix) {
    return https.get(prefix + '/table-tree');
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