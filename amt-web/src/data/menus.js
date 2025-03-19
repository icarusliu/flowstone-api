import { Switch, Tickets, Setting, Operation, Guide } from '@element-plus/icons-vue'

export default [
    {
        name: '数据接口管理',
        path: '/',
        icon: markRaw(Switch),
        children: [
            {
                name: '接口管理',
                path: '/apis/list',
            },
            {
                name: '接入方管理',
                path: '/apis/supplier',
            },{
                path: '/apis/schedule-task',
                name: '定时任务',
            }, {
                path: '/apis/logs',
                name: '运行日志',
            }, 
        ]
    }, {
        name: '数据集成&加工',
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
                path: '/base/ds',
            }, {
                name: '数据模型管理',
                path: '/base/model',
            }
        ]
    },
    {
        name: '系统管理',
        path: '/sys',
        icon: markRaw(Setting),
        children: [{
            path: '/sys/user',
            name: '用户管理',
        }, {
            path: '/sys/client',
            name: '客户端管理',
        }, {
            name: '字典管理',
            path: '/sys/dict',
        },
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