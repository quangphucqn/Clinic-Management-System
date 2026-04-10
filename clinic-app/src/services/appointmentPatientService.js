import { api } from './api.js'

export async function bookAppointment(payload) {
  const { data } = await api.post('/patient/appointments', payload)
  return data
}

