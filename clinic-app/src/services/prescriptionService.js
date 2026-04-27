import { api } from './api.js'

export async function getPrescriptionById(prescriptionId) {
  const { data } = await api.get(`/prescriptions/${prescriptionId}`)
  return data
}

export async function getPrescriptionByMedicalRecordId(medicalRecordId) {
  const { data } = await api.get(`/prescriptions/medical-record/${medicalRecordId}`)
  return data
}

