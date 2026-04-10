import { api } from './api.js'

export async function getNotifications(params) {
  const { data } = await api.get('/notifications', { params })
  return data
}

export async function getNotificationById(notificationId) {
  const { data } = await api.get(`/notifications/${notificationId}`)
  return data
}

export async function createNotification(payload) {
  const { data } = await api.post('/notifications', payload)
  return data
}

export async function updateNotification(notificationId, payload) {
  const { data } = await api.patch(`/notifications/${notificationId}`, payload)
  return data
}

export async function deleteNotification(notificationId) {
  await api.delete(`/notifications/${notificationId}`)
}

export async function getMyNotifications(params) {
  const { data } = await api.get('/notifications/me', { params })
  return data
}

