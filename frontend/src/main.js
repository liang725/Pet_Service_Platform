import { createApp } from 'vue'
import { Icon } from '@iconify/vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

const app = createApp(App)

// 全局注册Icon组件（使用原始Iconify组件）
app.component('Icon', Icon)

// 在挂载前添加全局CSS变量
const addGlobalStyles = () => {
  if (!document.querySelector('#pet-home-variables')) {
    const style = document.createElement('style')
    style.id = 'pet-home-variables'
    style.textContent = `
      :root {
        /* 主要颜色 - 基于HTML的配色方案 */
        --pet-primary: #FF9F43;
        --pet-primary-light: #FFD166;
        --pet-primary-lighter: #FFEAA7;
        --pet-primary-dark: #E17055;

        /* 背景颜色 */
        --pet-bg-primary: #FFF9E6;
        --pet-bg-secondary: #ffffff;

        /* 文本颜色 */
        --pet-text-primary: #5A4B3A;
        --pet-text-secondary: #8B5A2B;
        --pet-text-light: #ffffff;

        /* 阴影 */
        --pet-shadow: 0 4px 12px rgba(255, 200, 100, 0.2);
        --pet-shadow-dark: 0 8px 20px rgba(255, 200, 100, 0.3);

        /* 边框颜色 */
        --pet-border: #e0e0e0;

        /* 黑色系 */
        --pet-black-soft: #FFD166;
      }

      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        font-family: 'Arial Rounded MT Bold', 'Arial', sans-serif;
      }

      body {
        background-color: var(--pet-bg-primary);
        color: var(--pet-text-primary);
        line-height: 1.6;
      }
    `
    document.head.appendChild(style)
  }
}

// 立即添加样式
addGlobalStyles()

app.use(createPinia())
app.use(router)
app.mount('#app')
