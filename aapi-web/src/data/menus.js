import { Switch, Tickets, Setting } from '@element-plus/icons-vue'

export default [
    {
        name: '接口管理',
        path: '/',
        icon: markRaw(Switch),
        children: [
            {
                name: '分类管理',
                path: '/manager/type',
            },
            {
                name: '接口管理',
                path: '/apis/list',
            },
            {
                name: '数据源管理',
                path: '/manager/ds',
            },
            {
                name: '接入方管理',
                path: '/manager/supplier',
            },
            // {
            //     name: '定时任务',
            //     path: '/manager/supplier',
            // }
        ]
    },
    {
        name: '系统管理',
        path: '/setting',
        icon: markRaw(Setting),
        children: [{
            path: '/base/user',
            name: '用户管理',
        },{
            path: '/manager/schedule-task',
            name: '定时任务',
        },{
            path: '/logs/run',
            name: '运行日志',
        },
            // {
            //     path: '/logs/operations',
            //     name: '操作日志',
            // },
            // {
            //     path: '/manager/export',
            //     name: '接口导出',
            // }, {
            //     path: '/manager/import',
            //     name: '接口导入',
            // }
        ]
    }
]