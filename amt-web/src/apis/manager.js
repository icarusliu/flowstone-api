import * as https from '@/utils/https'

/**
 * 重置客户端密钥
 * @param {*} id 
 * @returns 
 */
export function resetClientSecret(id) {
    return https.get('/base/client/reset-secret', {id})
}