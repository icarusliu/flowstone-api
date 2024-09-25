import * as https from '@/utils/https'

/**
 * 重置客户端密钥
 * @param {*} id 
 * @returns 
 */
export function resetClientSecret(id) {
    return https.get('/base/client/reset-secret', {id})
}

/**
 * 查询客户端已授权接口列表
 * @param {*} clientId 
 * @returns 
 */
export function loadClientApis(params) {
    return https.post('/base/client-api/find-by-client', params)
}

/**
 * 查询客户端未授权接口列表
 * @param {*} params 
 * @returns 
 */
export function loadClientNotBindApis(params) {
    return https.post('/base/client-api/find-not-bind', params)
}

/**
 * 绑定客户端与接口
 * @param {} params 
 * @returns 
 */
export function bindClientAndApis(params) {
    return https.post('/base/client-api/add', params);
}

/**
 * 删除客户端与接口绑定关系
 * @param {*} params 
 * @returns 
 */
export function deleteClientAndApis(params) {
    return https.post('/base/client-api/delete', params)
}