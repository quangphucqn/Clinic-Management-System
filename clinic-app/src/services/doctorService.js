import { api } from './api.js'

export async function getDoctors(params) {
  const { data } = await api.get('/doctors', { params })
  return data
}

export async function getDoctorById(doctorId) {
  const { data } = await api.get(`/doctors/${doctorId}`)
  return data
}

export async function createDoctor(payload) {
  const { data } = await api.post('/doctors', payload)
  return data
}

export async function updateDoctor(doctorId, payload) {
  const { data } = await api.patch(`/doctors/${doctorId}`, payload)
  return data
}

export async function deleteDoctor(doctorId) {
  await api.delete(`/doctors/${doctorId}`)
}

export async function updateMyDoctorProfile(payload) {
  const { data } = await api.patch('/doctors/me/profile', payload)
  return data
}