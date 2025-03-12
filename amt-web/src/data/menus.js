import { Switch, Tickets, Setting, Operation, Guide } from '@element-plus/icons-vue'

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
                name: '接入方管理',
                path: '/manager/supplier',
            }, {
                path: '/logs/run',
                name: '运行日志',
            },
        ]
    }, {
        name: '数据加工',
        path: '/etl',
        icon: markRaw(Operation),
        children: [
            {
                name: '任务管理',
                path: '/etl/job',
            },
            {
                name: '执行日志',
                path: '/etl/logs',
            },
        ]
    }, {
        name: '基础管理',
        path: '/base',
        icon: markRaw(Guide),
        children: [
            {
                name: '数据源管理',
                path: '/manager/ds',
            },
        ]
    },
    {
        name: '系统管理',
        path: '/setting',
        icon: markRaw(Setting),
        children: [{
            path: '/base/user',
            name: '用户管理',
        }, {
            path: '/manager/client',
            name: '客户端管理',
        }, {
            path: '/manager/schedule-task',
            name: '定时任务',
        }
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