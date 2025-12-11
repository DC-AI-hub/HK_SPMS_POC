import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const backendHostName = 'localhost:8081'
const backendEndpoint = 'http://' + "localhost:8081";
const frontendHostName = 'localhost:5173';
const fontendEndpoint = 'http://' + frontendHostName;

export default defineConfig({
  plugins: [react()],
  base: './',
  server: {
    proxy: {
      '/spms': {
        target: backendEndpoint,
        changeOrigin: true,
        secure: false,
        // 关键：处理重定向
        hostRewrite: frontendHostName,
        xfwd: true,
        configure: (proxy) => {
          proxy.on('proxyRes', (proxyRes, req, res) => {
            /*
            const location = proxyRes.headers['location'];
            if (proxyRes.statusCode === 302 && location) {
              // 替换后端地址为前端地址
              if (location.includes(backendHostName)) {
                proxyRes.headers['location'] = location.replace(
                  backendEndpoint,
                  fontendEndpoint,
                );
              }
              // 处理 IDP 重定向
              else if (location.includes('idp.localhost')) {
                proxyRes.headers['location'] = location.replace(
                  'http://idp.localhost',
                  fontendEndpoint + '/idp'
                );
              }
            }*/
          });
        }
      }
    }
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('bpmn-js')) return 'vendor_bpmn';
            if (id.includes('@mui/icons-material')) return 'vendor_mui_icons';
            if (id.includes('@mui/material')) return 'vendor_mui_core';
            if (id.includes('@mui/x-data-grid')) return 'vendor_mui_datagrid';
            if (id.includes('react-i18next')) return 'vendor_i18n';
            if (id.includes('@babel/runtime')) return 'vendor_babel';
            return 'vendor';
          }
        }
      }
    },
    chunkSizeWarningLimit: 700,
  }
})