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

            // 系统配置项
            sysConfig: {},

            // 字典信息
            dictInfo: {},
        };
    },

    actions: {
        topMenusVisible() {
            let userConfig = this.userInfo.metadata || {};
            if (userConfig.showTopMenus || userConfig.showTopMenus == false) {
                return userConfig.showTopMenus;
            }

            return this.sysConfig.showTopMenus;
        },

        tagTabsVisible() {
            let userConfig = this.userInfo.metadata || {};
            if (userConfig.showTagTabs || userConfig.showTagTabs == false) {
                return userConfig.showTagTabs;
            }

            return this.sysConfig.showTagTabs;
        },

        init(resp) {
            this.userInfo = resp.userInfo || {};
            this.sysConfig = resp.configInfo || {};
            this.dictInfo = resp.dictInfo || {};
            let showTopMenus = this.userInfo.metadata?.showTopMenus;
            this.setMenuTree(resp.menuTree);
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

        getDictInfo() {
            return this.dictInfo;
        },

        setDictInfo(dictInfo) {
            this.dictInfo = dictInfo;
        },
    },
});
