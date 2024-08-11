import { defineStore } from 'pinia'

export const useSysStore = defineStore('sysStore', {
    state: () => {
        return {
            userInfo: {},
            unReadMsgCount: 0
        }
    },

    actions: {
        setUserInfo(userInfo) {
            this.userInfo = userInfo || {}
        },

        getUserInfo() {
            return this.userInfo
        },

        setUnReadMsgCount(c) {
            this.unReadMsgCount = c
        },

        getUnReadMsgCount() {
            return this.unReadMsgCount
        }
    }
})