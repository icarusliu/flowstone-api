import vue from '@vitejs/plugin-vue'
import { defineConfig, loadEnv } from 'vite'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'

export default ({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  const baseUrl = env.VITE_BASE_URL;

  return defineConfig({
    plugins: [vue(), AutoImport({
      imports: ['vue'],
    })],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        'vue': 'vue/dist/vue.esm-bundler.js'
      }
    },
    server: {
      host: 'localhost',
      port: 3000,
      proxy: {
        '/api/': {
          target: baseUrl,
          rewrite: (path) => path.replace(/^\/api/, '')
        },
        '/profile': {
          target: baseUrl
        }
      }
    },
    alias: {
      'vue': 'vue/dist/vue.esm-bundler.js'
    },
  })

}