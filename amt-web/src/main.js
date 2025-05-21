import { createApp } from 'vue'
import './styles/index.scss'
import App from './App.vue'
import elementPlus from 'element-plus';
import router from './router';
import BaseTable from './components/base-table/index.vue'
import BaseForm from './components/base-form/index.vue'
import EntityManager from './components/entity-manager/index.vue'
import TitleBar from './components/title-bar.vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { startWebSocket } from '@/utils/ws.js'
import { useSysStore } from './store'
import * as loginApis from '@/apis/login'
import * as sysApis from '@/apis/sys'
import { directives } from './utils/directives';
import * as utils from './utils/utils'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

const pinia = createPinia()
app
    .component('BaseForm', BaseForm)
    .component('BaseTable', BaseTable)
    .component('EntityManager', EntityManager)
    .component('titleBar', TitleBar)
    .use(elementPlus)
    .use(router)
    .use(pinia)
    .use(directives)

window.app = {
    utils
}


// 获取初始化信息
sysApis.getInitInfo().then(resp => {
    const sysStore = useSysStore()
    sysStore.setUserInfo(resp.userInfo)
    sysStore.setMenuTree(resp.menuTree)

    // 启动websocket
    window.websocket = startWebSocket()

    app.mount('#app')

    if (!resp.menuTree || !resp.menuTree.length) {
        // router.push('/login')
    }
}).catch(() => {
    // 启动websocket
    window.websocket = startWebSocket()

    app.use(elementPlus)
        .use(router)
        .mount('#app')
})
