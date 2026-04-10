import { api } from './api.js'

/**
 * @param {{
 *  doctorId: string
 *  appointmentDate: string
 *  timeSlotId: string
 *  reason: string
 * }} payload
 */
export async function bookAppointment(payload) {
  const { data } = await api.post('/patient/appointments', payload)
  return data
}

