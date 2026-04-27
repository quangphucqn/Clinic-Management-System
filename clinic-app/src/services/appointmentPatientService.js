import { api } from './api.js'

export async function bookAppointment(payload) {
  const { data } = await api.post('/patient/appointments', payload)
  return data
}

export async function getMyAppointmentHistory(params = {}) {
  const { data } = await api.get('/patient/appointments', { params })
  return data
}

export async function getMyMedicalHistory(params = {}) {
  const { data } = await api.get('/patient/medical-history', { params })
  return data
}

export async function getMyLabResults() {
  const { data } = await api.get('/patient/lab-results')
  return data
}

export async function createDoctorReview(payload) {
  const { data } = await api.post('/patient/reviews', payload)
  return data
}

export async function getMyDoctorReviews(params = {}) {
  const { data } = await api.get('/patient/reviews', { params })
  return data
}

export async function getBookedTimeSlotIds(params) {
  const { data } = await api.get('/patient/appointments/booked-slots', { params })
  return data
}

export async function retryAppointmentPayment(appointmentId, paymentMethod) {
  const { data } = await api.post(`/patient/appointments/${appointmentId}/retry-payment`, null, {
    params: paymentMethod ? { paymentMethod } : undefined,
  })
  return data
}

