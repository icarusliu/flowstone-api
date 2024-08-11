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
    .use(router)
    .use(elementPlus)
    .mount('#app')
