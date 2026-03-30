import { api } from './api.js'

export async function getTimeSlots(params) {
  const { data } = await api.get('/time-slots', { params })
  return data
}

export async function createTimeSlot(payload) {
  const { data } = await api.post('/time-slots', payload)
  return data
}

export async function updateTimeSlot(timeSlotId, payload) {
  const { data } = await api.patch(`/time-slots/${timeSlotId}`, payload)
  return data
}

export async function deleteTimeSlot(timeSlotId) {
  await api.delete(`/time-slots/${timeSlotId}`)
}

