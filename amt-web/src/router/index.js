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
                path: '/apis/list',
                component: ApiIndex
            },
            {
                path: '/apis/editor',
                component: () => import('../views/api/editor.vue')
            }, {
                path: '/apis/doc',
                component: () => import('../views/api/doc.vue')
            },
            {
                path: '/manager/type',
                component: Type
            }, {
                path: '/manager/ds',
                component: Ds
            }, {
                path: '/manager/supplier',
                component: () => import('../views/manager/supplier.vue')
            }, {
                path: '/manager/supplier-auth/:id',
                component: () => import('../views/manager/supplier-auth.vue')
            },
            {
                path: '/logs',
                children: [
                    { path: '/logs/run', component: LogIndex }
                ]
            }, {
                path: '/base/user',
                component: () => import('../views/base/user-manager.vue')
            }, {
                path: '/manager/schedule-task',
                component: () => import('../views/manager/schedule-task.vue')
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