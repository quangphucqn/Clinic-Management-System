import { api } from './api.js'

export async function updateMyPatientProfile(payload) {
  const { data } = await api.patch('/patients/me/profile', payload)
  return data
}

