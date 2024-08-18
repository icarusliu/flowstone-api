import { createApp } from 'vue'
import './styles/index.scss'
import App from './App.vue'
import elementPlus from 'element-plus';
import router from './router';
import BaseTable from './components/base-table.vue'
import BaseForm from './components/base-form.vue'
import EntityManager from './components/entity-manager.vue'
import TitleBar from './components/title-bar.vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { startWebSocket } from '@/utils/ws.js'
import { useSysStore } from './store'
import * as loginApis from '@/apis/login'

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
    .use(pinia)


// 获取初始化信息
loginApis.getUserInfo().then(resp => {
    const sysStore = useSysStore()
    sysStore.setUserInfo(resp)

    // 启动websocket
    window.websocket = startWebSocket()

    app.use(elementPlus)
        .use(router)
        .mount('#app')
}).catch(() => {
    app.use(elementPlus)
        .use(router)
        .mount('#app')
})
