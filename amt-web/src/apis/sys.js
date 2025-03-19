import https from '../utils/https.js';

/**
 * 获取菜单树
 */
export function getMenuTree(params) {
    return https.get('/sys/menu/tree', params)
}

export function getInitInfo() {
    return https.get('/init/info')
}

/**
 * 获取所有角色列表
 */
export function getRoles() {
    return https.post('/sys/role/query', {})
}

/**
 * 查询角色资源列表
 * @param {*} roleId 
 */
export function getRoleResources(roleId) {
    return https.get('/sys/role/resources', { roleId })
}

/**
 * 更新角色资源列表
 * @param {*} params 
 * @returns 
 */
export function saveRoleResources(params) {
    return https.put('/sys/role-resource/update-role-resources', params)
}

export function saveRole(params) {
    if (params.id) {
        return https.put('/sys/role/update', params)
    } else {
        return https.post('/sys/role/add', params)
    }
}

export function deleteRole(id) {
    return https.del('/sys/role/delete/' + id)
}

export function saveMenuButtons(params) {
    return https.post('/sys/menu-button/save', params)
}

export function updateUserStatus(id, status) {
    return https.put('/sys/user/update', {
        id,
        status
    })
}

export function getDeptTree() {
    return https.get('/sys/dept/tree')
}

export function updateConfig(params) {
    return https.put('/sys/config/update', params)
}

export function updateDict(params) {
    return https.put('/sys/dict/update', params)
}

export function getRoleUsers(params) {
    return https.get('/sys/user/query-by-role', params)
}

export function queryNoRoleUsers(params) {
    return https.post('/sys/user/query-no-role', params)
}

export function saveRoleUsers(params) {
    return https.get('/sys/user-role/save-role-users', params);
}

export function removeUserRoles(params) {
    return https.get('/sys/user-role/remove', params)
}

export function removeRoleUsers(params) {
    return https.get('/sys/user-role/remove-role-users', params)
}