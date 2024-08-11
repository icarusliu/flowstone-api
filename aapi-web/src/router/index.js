import { createRouter, createWebHistory } from "vue-router";
import Index from '../views/index.vue';
import ApiIndex from '../views/apis/index.vue';
import Type from '../views/apis/type.vue';
import Ds from '../views/apis/ds.vue';
import LogIndex from '../views/logs/Index.vue';

const routes = [
    {
        path: '/',
        redirect: () => '/apis'
    }, {
        path: '/apis',
        component: Index,
        children: [
            {
                path: '/apis/list',
                component: ApiIndex
            },
            {
                path: '/apis/editor',
                component: () => import('../views/apis/editor/index.vue')
            },
            {
                path: '/apis/type',
                component: Type
            },{
                path: '/apis/ds',
                component: Ds
            }, {
                path: '/apis/run',
                component: LogIndex
            }
        ]
    }
]

const router = createRouter({
    routes,
    history: createWebHistory()
})

export default router;