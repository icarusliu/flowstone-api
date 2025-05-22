import { defineStore } from "pinia";
import * as utils from "@/utils/utils";

export const useSysStore = defineStore("sysStore", {
    state: () => {
        return {
            userInfo: {},

            // 顶部菜单
            topMenus: [],

            // 当前选中的顶部菜单下的子菜单，如果不展示顶部菜单时，则为全部菜单
            menuTree: [],

            // 有权限的按钮编码列表
            buttons: [],

            // 有权限的菜单路径列表
            menuPaths: [],

            menuFolded: false,

            // 系统配置项
            sysConfig: {},

            // 字典信息
            dictInfo: {},

            // keepAlive缓存的路由name
            cacheRoutes: []
        }
    },

    actions: {
        topMenusVisible() {
            let userConfig = this.userInfo.metadata || {}
            if (userConfig.showTopMenus || userConfig.showTopMenus == false) {
                return userConfig.showTopMenus
            }

            return this.sysConfig.showTopMenus;
        },

        tagTabsVisible() {
            let userConfig = this.userInfo.metadata || {}
            if (userConfig.showTagTabs || userConfig.showTagTabs == false) {
                return userConfig.showTagTabs
            }

            return this.sysConfig.showTagTabs;
        },

        init(resp) {
            this.userInfo = resp.userInfo || {};
            this.sysConfig = resp.configInfo || {};
            this.topMenus = resp.menuTree || [];

            let showTopMenus = this.userInfo.metadata?.showTopMenus 

            // 不展示顶部菜单时，需要设置右侧菜单；否则，在顶部菜单中设置右侧菜单
            if (showTopMenus == false) {
                this.menuTree = resp.menuTree || []
            } else if (showTopMenus == true) {
                return  
            } else if (!this.sysConfig.showTopMenus) {
                this.menuTree = resp.menuTree || [];
            }
        },

        setUserInfo(userInfo) {
            this.userInfo = userInfo || {};
        },

        getUserInfo() {
            return this.userInfo || {};
        },

        isSuperAdmin() {
            return this.getUserInfo().isSuperAdmin;
        },

        // 折叠菜单
        reverseMenuFold() {
            this.menuFolded = !this.menuFolded;
        },

        getMenuFolded() {
            return this.menuFolded;
        },

        getMenuTree() {
            return this.menuTree || [];
        },

        setMenuTree(tree) {
            this.menuTree = tree;
            if (tree) {
                this.menuPaths = [];
                this.buttons = [];

                utils.loop(tree, (item) => {
                    this.menuPaths.push(item.path);

                    if (!item.buttons) {
                        return;
                    }

                    item.buttons.forEach((button) => {
                        this.buttons.push(item.path + ":" + button.code);
                    });
                });
            }
        },

        getButtons() {
            return this.buttons || [];
        },

        getMenuPaths() {
            return this.menuPaths;
        },

        setSysConfig(sysConfig) {
            this.sysConfig = sysConfig;
        },

        getSysConfig() {
            return this.sysConfig || {};
        },

        addRoute(name) {
            this.cacheRoutes.push(name)
    },

        removeRoute(name) {
            let idx = this.cacheRoutes.indexOf(name)
            if (idx != -1) {
                this.cacheRoutes.splice(idx, 1)
            }
        },

        getCachedRoutes() {
            return this.cacheRoutes
        }
    }
})