import { api } from './api.js'

export async function getUnits(params) {
  const { data } = await api.get('/units', { params })
  return data
}

export async function createUnit(payload) {
  const { data } = await api.post('/units', payload)
  return data
}

export async function updateUnit(unitId, payload) {
  const { data } = await api.patch(`/units/${unitId}`, payload)
  return data
}

export async function deleteUnit(unitId) {
  await api.delete(`/units/${unitId}`)
}

