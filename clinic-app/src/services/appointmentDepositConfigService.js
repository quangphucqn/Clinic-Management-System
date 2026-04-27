import { api } from './api.js'

export async function getAppointmentDepositConfig() {
  const { data } = await api.get('/appointment-deposit-config')
  return data
}

export async function updateAppointmentDepositConfig(payload) {
  const { data } = await api.patch('/appointment-deposit-config', payload)
  return data
}
