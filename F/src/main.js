import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './style.css'
import './style/variables.css'
import './style/global.css'
import { useUserStore } from './stores'

const app = createApp(App)
const pinia = createPinia()

// 注册所有 Element Plus 图标
for (const [key, icon] of Object.entries(Icons)) {
  app.component(key, icon)
}

app.use(pinia)
app.use(ElementPlus)
app.use(router)

app.mount('#app')

// 初始化用户信息（在应用挂载后）
const userStore = useUserStore()
userStore.initializeUser()