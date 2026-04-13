import { api } from '../api.js'

export async function getPrescriptionById(prescriptionId) {
  const { data } = await api.get(`/doctor/prescriptions/${prescriptionId}`)
  return data
}

export async function createPrescription(payload) {
  const { data } = await api.post('/doctor/prescriptions', payload)
  return data
}