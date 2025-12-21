// vite.config.js
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue({
      // 确保正确处理图标
      template: {
        compilerOptions: {
          // 允许自定义组件
          isCustomElement: (tag) => tag.includes('icon-')
        }
      }
    }),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  
  // Iconify 优化
  optimizeDeps: {
    include: ['@iconify/vue'],
    exclude: [] // 确保没有排除必要的依赖
  },
  
  // 构建配置
  build: {
    rollupOptions: {
      external: [] // 确保没有将图标库排除
    }
  }
})