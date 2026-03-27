import { api } from './api.js'
import { TOKEN_STORAGE_KEY, USER_STORAGE_KEY } from '../config/env.js'

export async function login({ username, password }) {
  const { data } = await api.post('/auth/token', { username, password })
  return data
}

export async function registerPatient(payload) {
  const { data } = await api.post('/patients/register', payload)
  return data
}

export async function getCurrentUserProfile() {
  const { data } = await api.get('/users/me')
  return data
}

export function setAuthToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_STORAGE_KEY, token)
  } else {
    localStorage.removeItem(TOKEN_STORAGE_KEY)
  }
}

export function getAuthToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY)
}

export function setCurrentUserProfile(profile) {
  if (profile) {
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(profile))
  } else {
    localStorage.removeItem(USER_STORAGE_KEY)
  }
}

export function getCurrentUserProfileFromStorage() {
  const raw = localStorage.getItem(USER_STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function logout() {
  localStorage.removeItem(TOKEN_STORAGE_KEY)
  localStorage.removeItem(USER_STORAGE_KEY)
}
