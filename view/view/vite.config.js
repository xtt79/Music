import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/yolomusic_war/api'),
        // 配置 Cookie 路径重写，解决 Session 跨域问题
        cookiePathRewrite: {
          '/yolomusic_war': '/'
        }
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets'
  }
})

