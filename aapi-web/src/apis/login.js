import * as https from '../utils/https';

// 登录
export function login(params) {
    return https.get('/auth/login', params, {alertError: false})
}

// 获取当前登录用户
export function getUserInfo() {
    return https.get('/base/user/info')
}