import { api } from './api.js'

function toMedicineFormData(payload, file) {
  const formData = new FormData()
  formData.append(
    'data',
    new Blob([JSON.stringify(payload)], { type: 'application/json' }),
  )
  if (file) {
    formData.append('file', file)
  }
  return formData
}

export async function getMedicines(params) {
  const { data } = await api.get('/medicines', { params })
  return data
}

export async function createMedicine(payload, file) {
  const body = file ? toMedicineFormData(payload, file) : payload
  const { data } = await api.post('/medicines', body)
  return data
}

export async function updateMedicine(medicineId, payload, file) {
  const body = file ? toMedicineFormData(payload, file) : payload
  const { data } = await api.patch(`/medicines/${medicineId}`, body)
  return data
}

export async function deleteMedicine(medicineId) {
  await api.delete(`/medicines/${medicineId}`)
}

