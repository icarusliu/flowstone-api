import { defineStore } from 'pinia'
import * as utils from '@/utils/utils'

export const useSysStore = defineStore('sysStore', {
    state: () => {
        return {
            userInfo: {},
            menuTree: [],

            // 有权限的按钮编码列表
            buttons: [],

            // 有权限的菜单路径列表
            menuPaths: [],            

            menuFolded: false,
        }
    },

    actions: {
        setUserInfo(userInfo) {
            this.userInfo = userInfo || {}
        },

        getUserInfo() {
            return this.userInfo || {}
        },

        isSuperAdmin() {
            return this.getUserInfo().isSuperAdmin
        },

        // 折叠菜单
        reverseMenuFold() {
            this.menuFolded = !this.menuFolded
        },

        getMenuFolded() {
            return this.menuFolded
        },

        getMenuTree() {
            return this.menuTree || []
        },

        setMenuTree(tree) {
            this.menuTree = tree
            if (tree) {
                this.menuPaths = []
                this.buttons = []

                utils.loop(tree, item => {
                    this.menuPaths.push(item.path)

                    if (!item.buttons) {
                        return
                    }

                    item.buttons.forEach(button => {
                        this.buttons.push(item.path + ':' + button.code)
                    })
                })
            }
        },

        getButtons() {
            return this.buttons || []
        }, 

        getMenuPaths() {
            return this.menuPaths
        }
    }
})