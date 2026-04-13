import { api } from '../api.js'

export async function getMedicalRecordById(medicalRecordId) {
  const { data } = await api.get(`/doctor/medical-records/${medicalRecordId}`)
  return data
}

export async function searchDoctorPatients(params) {
  const { data } = await api.get('/doctor/patients', { params })
  return data
}

export async function getPatientMedicalHistory(patientId, params) {
  const { data } = await api.get(`/doctor/patients/${patientId}/medical-history`, { params })
  return data
}

export async function createMedicalRecord(payload) {
  const { data } = await api.post('/doctor/medical-records', payload)
  return data
}