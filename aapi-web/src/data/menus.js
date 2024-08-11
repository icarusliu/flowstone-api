export default [
    {
        path: '/apis',
        name: '接口管理',
        children: [
            {
                name: '分类管理',
                path: '/apis/type'
            },
            {
                name: '接口管理',
                path: '/apis/list'
            },
            {
                name: '数据源管理',
                path: '/apis/ds'
            }
        ]
    },
    {
        path: '/logs',
        name: '日志管理',
        children: [
            {
                path: '/logs/run',
                name: '运行日志'
            }
        ]
    }
]