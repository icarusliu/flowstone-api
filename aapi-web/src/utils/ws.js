import * as tokenUtils from './token'
import { useSysStore } from '../store/index.js'



export function startWebSocket() {
    let websocket = new WebSocket("ws://ui.ngq.com:8080/ws/front")

    // 心跳检测器
    let timer

    // 消息监听者
    let listeners = {}

    // 代理发送消息，增加token
    let proxy = {
        send: (msg) => {
            if (websocket.readyState != WebSocket.OPEN) {
                console.warn("连接已关闭")
                return
            }

            websocket.send(JSON.stringify({
                userId: useSysStore().getUserInfo().id,
                msg
            }))
        },

        // 增加消息监听
        addListener: (type, listener) => {
            let arr = listeners[type]
            if (!arr) {
                arr = listeners[type] = []
            }

            arr.push(listener)
        }
    }

    websocket.onopen = e => {
        proxy.send('ping')
        timer = setInterval(() => {
            if (websocket && websocket.readyState === WebSocket.OPEN) {
                proxy.send('ping')
            }
        }, 30000)
    }

    websocket.onmessage = e => {
        let data = e.data;
        if (!data || data == 'success') {
            return
        }

        data = JSON.parse(data)
        if (data.type) {
            var arr = listeners[data.type]
            arr && arr.forEach(listener => listener(data))
        }

        // data = JSON.parse(data)
        // ElNotification({
        //     title: data.title,
        //     message: data.message,
        //     position: 'bottom-right'
        // })
    }


    websocket.onclose = () => {
        timer && clearInterval(timer)
    }

    return proxy;
}