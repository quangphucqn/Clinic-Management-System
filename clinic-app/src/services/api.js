import axios from 'axios'
import { API_BASE_URL, TOKEN_STORAGE_KEY } from '../config/env.js'

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  // Let browser set multipart boundary automatically.
  if (config.data instanceof FormData && config.headers) {
    if (typeof config.headers.set === 'function') {
      config.headers.set('Content-Type', undefined)
    } else {
      delete config.headers['Content-Type']
    }
  }

  const token = localStorage.getItem(TOKEN_STORAGE_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
