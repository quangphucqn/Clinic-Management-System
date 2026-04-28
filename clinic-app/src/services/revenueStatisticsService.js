import { api } from './api.js'

export async function getRevenueStatisticsByMonth() {
  const { data } = await api.get('/statistics/revenue/monthly')
  return data
}

export async function getRevenueStatisticsByQuarter() {
  const { data } = await api.get('/statistics/revenue/quarterly')
  return data
}

export async function getRevenueStatisticsByYear() {
  const { data } = await api.get('/statistics/revenue/yearly')
  return data
}

