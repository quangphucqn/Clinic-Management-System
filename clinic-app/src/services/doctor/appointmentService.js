import { api } from '../api.js'

export async function getMyAppointments(params) {
  const { data } = await api.get('/doctor/appointments', { params })
  return data
}

export async function getMyAppointmentById(appointmentId) {
  const { data } = await api.get(`/doctor/appointments/${appointmentId}`)
  return data
}