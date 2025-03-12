import { createRouter, createWebHistory } from "vue-router";
import Index from '../views/index.vue';
import ApiIndex from '../views/api/index.vue';
import Type from '../views/manager/type.vue';
import Ds from '../views/manager/ds.vue';
import LogIndex from '../views/logs/Index.vue';

const routes = [
    {
        path: '/',
        component: Index,
        children: [
            {
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
                path: '/manager/type',
                component: Type,
                meta: {
                    title: '分类管理'
                }
            }, {
                path: '/manager/ds',
                component: Ds,
                meta: {
                    title: '数据源管理'
                }
            }, {
                path: '/manager/supplier',
                component: () => import('../views/manager/supplier.vue'),
                meta: {
                    title: '接入方管理'
                }
            }, {
                path: '/manager/supplier-auth/:id',
                component: () => import('../views/manager/supplier-auth.vue'),
                meta: {
                    title: '鉴权管理'
                }
            }, {
                path: '/manager/client',
                component: () => import('../views/base/client.vue'),
                meta: {
                    title: '客户端管理'
                }
            },{
                path: '/manager/client-apis/:id',
                component: () => import('../views/base/client-apis.vue'),
                meta: {
                    title: '客户端授权'
                }
            },
            {
                path: '/logs',
                children: [
                    { path: '/logs/run', component: LogIndex,
                        meta: {
                            title: '运行日志'
                        } }
                ]
            }, {
                path: '/base/user',
                component: () => import('../views/base/user-manager.vue'),
                meta: {
                    title: '用户管理'
                }
            }, {
                path: '/manager/schedule-task',
                component: () => import('../views/manager/schedule-task.vue'),
                meta: {
                    title: '定时任务'
                }
            }, {
                path: '/etl-manager',
                children: [
                    {
                        path: '/etl/job',
                        component: () => import('../views/etl/job.vue'),
                        meta: {
                            title: '任务管理'
                        }
                    },{
                        path: '/etl/logs',
                        component: () => import('../views/etl/logs.vue'),
                        meta: {
                            title: '执行日志'
                        }
                    }
                ]
            }
        ]
    },
    {
        path: '/login',
        component: () => import('../views/base/login.vue')
    }
]

const router = createRouter({
    routes,
    history: createWebHistory()
})

export default router;