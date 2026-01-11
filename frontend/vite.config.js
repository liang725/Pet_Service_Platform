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

  // 开发服务器配置
  server: {
    port: 5173,
    proxy: {
      // 代理所有 /api 请求到后端服务器
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // 可选：添加日志
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log(`[代理请求] ${req.method} ${req.url} -> ${options.target}${req.url}`)
          })
        }
      }
    }
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
