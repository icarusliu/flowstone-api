import https from '../utils/https.js';

/**
 * 获取菜单树
 */
export function getMenuTree(params) {
    return https.get('/base/menu/tree', params)
}

export function getInitInfo() {
    return https.get('/init/info')
}

/**
 * 获取所有角色列表
 */
export function getRoles() {
    return https.post('/base/role/query', {})
}

/**
 * 查询角色资源列表
 * @param {*} roleId 
 */
export function getRoleResources(roleId) {
    return https.get('/base/role/resources', { roleId })
}

/**
 * 更新角色资源列表
 * @param {*} params 
 * @returns 
 */
export function saveRoleResources(params) {
    return https.put('/base/role-resource/update-role-resources', params)
}

export function saveRole(params) {
    if (params.id) {
        return https.put('/base/role/update', params)
    } else {
        return https.post('/base/role/add', params)
    }
}

export function deleteRole(id) {
    return https.del('/base/role/delete/' + id)
}

export function saveMenuButtons(params) {
    return https.post('/base/menu-button/save', params)
}

export function updateUserStatus(id, status) {
    return https.put('/base/user/update', {
        id,
        status
    })
}

export function getDeptTree() {
    return https.get('/base/dept/tree')
}

export function updateConfig(params) {
    return https.put('/base/config/update', params)
}

export function updateDict(params) {
    return https.put('/base/dict/update', params)
}