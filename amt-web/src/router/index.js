import { createRouter, createWebHistory } from "vue-router";
import Index from '../views/index.vue';
import ApiIndex from '../views/api/index.vue';
import Ds from '../views/base/ds.vue';
import LogIndex from '../views/api/logs/Index.vue';

const routes = [
    {
        path: '/',
        component: Index,
        children: [
            {
                path: '/apis',
                children: [{
                    path: '/',
                    redirect: '/apis/list',
                    meta: {
                        title: '接口列表'
                    }
                },
                {
                    path: '/apis/list',
                    component: ApiIndex,
                    meta: {
                        title: '接口列表'
                    }
                },
                {
                    path: '/apis/editor',
                    component: () => import('../views/api/editor.vue'),
                    meta: {
                        title: '接口编辑'
                    }
                }, {
                    path: '/apis/doc',
                    component: () => import('../views/api/doc.vue'),
                    meta: {
                        title: '接口文档'
                    }
                },
                {
                    path: '/apis/supplier',
                    component: () => import('../views/api/supplier/index.vue'),
                    meta: {
                        title: '接入方管理'
                    }
                }, {
                    path: '/apis/supplier-auth/:id',
                    component: () => import('../views/api/supplier/supplier-auth.vue'),
                    meta: {
                        title: '鉴权管理'
                    }
                }, {
                    path: '/apis/schedule-task',
                    component: () => import('../views/api/schedule-task.vue'),
                    meta: {
                        title: '定时任务'
                    }
                }, {
                    path: '/apis/logs', component: LogIndex,
                    meta: {
                        title: '运行日志'
                    }
                },
                ]
            },
            {
                path: '/sys',
                children: [{
                    path: '/sys/client',
                    component: () => import('../views/base/client.vue'),
                    meta: {
                        title: '客户端管理'
                    }
                }, {
                    path: '/sys/client-apis/:id',
                    component: () => import('../views/base/client-apis.vue'),
                    meta: {
                        title: '客户端授权'
                    }
                },
                {
                    path: '/sys/user',
                    component: () => import('../views/sys/user.vue'),
                    meta: {
                        title: '用户管理'
                    }
                }, {
                    path: '/sys/dict',
                    component: () => import('../views/sys/dict/index.vue'),
                    meta: {
                        title: '字典管理'
                    }
                }]
            }, {
                path: '/etl-manager',
                children: [
                    {
                        path: '/etl/job',
                        component: () => import('../views/etl/job.vue'),
                        meta: {
                            title: '任务管理'
                        }
                    }, {
                        path: '/etl/logs',
                        component: () => import('../views/etl/logs.vue'),
                        meta: {
                            title: '执行日志'
                        }
                    }
                ]
            }, {
                path: '/base',
                children: [
                    {
                        path: '/base/ds',
                        component: Ds,
                        meta: {
                            title: '数据源管理'
                        }
                    }, {
                        path: '/base/model',
                        component: () => import('../views/base/model/index.vue'),
                        meta: {
                            title: '数据模型管理'
                        }
                    }, {
                        path: '/base/model-data',
                        component: () => import('../views/base/model-data/index.vue'),
                        meta: {
                            title: '数据模型管理'
                        }
                    }
                ]
            }
        ]
    },
    {
        path: '/login',
        component: () => import('../views/sys/login.vue')
    }
]

const router = createRouter({
    routes,
    history: createWebHistory()
})

export default router;