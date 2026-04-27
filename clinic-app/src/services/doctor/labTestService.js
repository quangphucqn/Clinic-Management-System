import { api } from '../api.js'

export async function getLabTestById(labTestId) {
  const { data } = await api.get(`/doctor/lab-tests/${labTestId}`)
  return data
}

export async function createLabTestOrder(payload) {
  const { data } = await api.post('/doctor/lab-tests', payload)
  return data
}

export async function createLabResult(payload) {
  const { data } = await api.post('/doctor/lab-results', payload)
  return data
}