import { defineStore } from 'pinia'

export const useSysStore = defineStore('sysStore', {
    state: () => {
        return {
            userInfo: {},
            menuFolded: false
        }
    },

    actions: {
        setUserInfo(userInfo) {
            this.userInfo = userInfo || {}
        },

        getUserInfo() {
            return this.userInfo || {}
        },

        // 折叠菜单
        reverseMenuFold() {
            this.menuFolded = !this.menuFolded
        },

        getMenuFolded() {
            return this.menuFolded
        }
    }
})