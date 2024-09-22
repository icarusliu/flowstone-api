import axios from "axios";
import {
    ElMessage
} from "element-plus";
import router from '../router';
import { useSysStore } from '../store'
import * as tokenUtils from './token'

axios.defaults.timeout = 20000; //响应时间
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'; //配置请求头
axios.defaults.baseURL = '/api'; //配置接口地址

//POST传参序列化(添加请求拦截器)
axios.interceptors.request.use((config) => {
    const contentType = config.headers['Content-Type']

    //在发送请求之前做某件事
    if (config.method === 'post' &&
        contentType !== 'application/json;charset=UTF-8' &&
        contentType !== 'multipart/form-data') {
        config.data = JSON.stringify(config.data);
    } else if (config.method === 'get') {
        let url = config.url;
        if (config.params && !(config.params instanceof FormData)) {
            url += '?'
            for (var k in config.params) {
                url += k + '=' + encodeURIComponent(config.params[k]) + '&'
            }
            url = url.substring(0, url.length - 1)
            config.url = url
            delete config.params
        }
    }

    return config;
}, (error) => {
    console.log('错误的传参')
    return Promise.reject(error);
});

// 跳转登录页面
function goLogin() {
    const sysStore = useSysStore()
    const path = router.currentRoute.value.fullPath

    // 清空用户信息
    sysStore.setUserInfo(null)

    if (path == '/login') {
        next = router.removeRoute.value.query.next
    }

    // 跳转路由
    router.replace('/login?next=' + path);
}

//返回状态判断(添加响应拦截器)
axios.interceptors.response.use((res) => {
    return Promise.resolve(res);
}, (error) => {
    let resp = error.response
    let status = resp.status

    if (status === 401 || status === 402) {
        goLogin()
    } else if (status === 500) {
        var msg = resp.data.msg || '系统内部异常'
        ElMessage.error(msg);
    } else if (status === 403) {
        ElMessage.error('权限不足')
    } else if (status === 404) {
        ElMessage.error('系统错误(404)')
    }

    return Promise.reject(resp.data);
});

// 请求实现
function request(func, url, params, config) {
    return new Promise((resolve, reject) => {
        let result
        if (func == axios.get) {
            result = func(url, {
                ...config,
                params,
            })
        } else {
            result = func(url, params, config)
        }
        result
            .then(resp => {
                const res = resp.data
                const code = res.code

                // 也可以没code
                if (!code) {
                    resolve(res)
                    return
                }

                if (code == 200 || code == '200') {
                    resolve(res)
                    return
                } else if (code == '401' && url.indexOf('dua-test') == -1) {
                    // dua-test接口不处理401，否则如果是源系统返回401，会导致工具跳登录页面
                    ElMessage.error("会话超时，请重新登录")
                    goLogin()
                    return
                }

                // 是否要展示异常信息，也可以在此不提示，而在调用处进行提示
                if (config.alertError != false) {
                    ElMessage.error(res.msg)
                }
                reject(res)
            }, reject)
            .catch(reject);
    })
}

//返回一个Promise(发送post请求)
export function post(url, params = {}, config = {}) {
    return request(axios.post, url, params, {
        ...config,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    })
}

export function del(url, params = {}, config = {}) {
    return request(axios.delete, url, params, config)
}

export function put(url, params = {}, config = {}) {
    return request(axios.put, url, params, {
        ...config,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    })
}

////返回一个Promise(发送get请求)
export function get(url, param, config = {}) {
    return request(axios.get, url, param, config)
}

// GET文件上传
export function postForm(url, param, config = {}) {
    return request(axios.post, url, param, {
        ...config,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// POST URL传参
export function postUrlParams(url, param, config = {}) {
    return request(axios.post, url, param, config)
}

export default {
    post,
    put,
    get,
    postForm,
    postUrlParams
}