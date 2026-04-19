import { api } from './api.js'

export async function getPatientStatisticsByMonth() {
  const { data } = await api.get('/statistics/patients/monthly')
  return data
}

export async function getPatientStatisticsByQuarter() {
  const { data } = await api.get('/statistics/patients/quarterly')
  return data
}

export async function getPatientStatisticsByYear() {
  const { data } = await api.get('/statistics/patients/yearly')
  return data
}

