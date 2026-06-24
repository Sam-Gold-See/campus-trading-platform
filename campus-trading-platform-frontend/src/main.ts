import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 导入赛博朋克主题样式（替代默认样式）
import 'element-plus/dist/index.css'
import '@/styles/cyberpunk.css'
import '@/styles/element-override.css'
import '@/styles/global.css'

import App from './App.vue'
import router from './router'
import { useUserStore } from './stores/user'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 初始化用户状态 - 确保在应用挂载前执行
const userStore = useUserStore(pinia)
userStore.init().then(() => {
  app.mount('#app')
})