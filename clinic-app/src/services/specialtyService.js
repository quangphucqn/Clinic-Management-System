import { api } from './api.js'

export async function getSpecialties(params) {
  const { data } = await api.get('/specialties', { params })
  return data
}

export async function createSpecialty(payload) {
  const { data } = await api.post('/specialties', payload)
  return data
}

export async function updateSpecialty(specialtyId, payload) {
  const { data } = await api.patch(`/specialties/${specialtyId}`, payload)
  return data
}

export async function deleteSpecialty(specialtyId) {
  await api.delete(`/specialties/${specialtyId}`)
}

