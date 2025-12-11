// AI 模型配置（火山方舟 / Doubao）
// 如需修改：在此文件编辑模型、API Key 与 Base URL
export const AI_BASE_URL = 'https://ark.cn-beijing.volces.com/api/v3'
export const AI_MODEL = 'doubao-seed-1-6-flash-250715'

// 为方便本地演示，默认内置密钥；优先读取 window.__AI_API_KEY__ 或环境变量
// 生产环境请改为从后端下发或使用环境变量注入，避免暴露密钥
export const AI_API_KEY = (typeof window !== 'undefined' && window.__AI_API_KEY__) || import.meta.env.VITE_AI_API_KEY || '537d953b-227c-4238-a92d-ab45f90ef743'


