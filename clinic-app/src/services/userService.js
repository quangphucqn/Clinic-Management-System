import { api } from './api.js'

export async function getUsers(params) {
  const { data } = await api.get('/users', { params })
  return data
}

export async function changeMyPassword(payload) {
  const { data } = await api.patch('/users/me/password', payload)
  return data
}

