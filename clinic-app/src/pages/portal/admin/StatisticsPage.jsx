import { Column, Line } from '@ant-design/plots'
import {
  BarChartOutlined,
  FileTextOutlined,
  RiseOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { App, Button, Card, Col, Empty, Row, Select, Space, Statistic, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import {
  getPatientStatisticsByMonth,
  getPatientStatisticsByQuarter,
  getPatientStatisticsByYear,
} from '../../../services/patientStatisticsService.js'
import {
  getRevenueStatisticsByMonth,
  getRevenueStatisticsByQuarter,
  getRevenueStatisticsByYear,
} from '../../../services/revenueStatisticsService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Text } = Typography

const CYCLE = {
  MONTHLY: 'MONTHLY',
  QUARTERLY: 'QUARTERLY',
  YEARLY: 'YEARLY',
}

function toAmountNumber(value) {
  const amount = Number(value)
  if (!Number.isFinite(amount)) return 0
  return amount
}

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(toAmountNumber(value))
}

function cycleLabel(cycle) {
  if (cycle === CYCLE.MONTHLY) return 'Theo tháng'
  if (cycle === CYCLE.QUARTERLY) return 'Theo quý'
  return 'Theo năm'
}

function buildRevenueSeries(cycle, year, monthlyRevenueStats, quarterlyRevenueStats, yearlyRevenueStats) {
  if (!year) return []
  if (cycle === CYCLE.MONTHLY) {
    const map = new Map(monthlyRevenueStats.filter((item) => item.year === year).map((item) => [item.month, item]))
    return Array.from({ length: 12 }, (_, index) => {
      const month = index + 1
      const found = map.get(month)
      return {
        periodLabel: `T${month}`,
        amount: Number(found?.totalAmount || 0),
        transactions: Number(found?.totalTransactions || 0),
      }
    })
  }

  if (cycle === CYCLE.QUARTERLY) {
    const map = new Map(quarterlyRevenueStats.filter((item) => item.year === year).map((item) => [item.quarter, item]))
    return Array.from({ length: 4 }, (_, index) => {
      const quarter = index + 1
      const found = map.get(quarter)
      return {
        periodLabel: `Q${quarter}`,
        amount: Number(found?.totalAmount || 0),
        transactions: Number(found?.totalTransactions || 0),
      }
    })
  }

  const yearly = yearlyRevenueStats.find((item) => item.year === year)
  return [
    {
      periodLabel: `${year}`,
      amount: Number(yearly?.totalAmount || 0),
      transactions: Number(yearly?.totalTransactions || 0),
    },
  ]
}

function buildPatientSeries(cycle, year, monthlyStats, quarterlyStats, yearlyStats) {
  if (!year) return []
  if (cycle === CYCLE.MONTHLY) {
    const map = new Map(monthlyStats.filter((item) => item.year === year).map((item) => [item.month, item]))
    return Array.from({ length: 12 }, (_, index) => {
      const month = index + 1
      const found = map.get(month)
      return { periodLabel: `T${month}`, totalPatients: Number(found?.totalPatients || 0) }
    })
  }

  if (cycle === CYCLE.QUARTERLY) {
    const map = new Map(quarterlyStats.filter((item) => item.year === year).map((item) => [item.quarter, item]))
    return Array.from({ length: 4 }, (_, index) => {
      const quarter = index + 1
      const found = map.get(quarter)
      return { periodLabel: `Q${quarter}`, totalPatients: Number(found?.totalPatients || 0) }
    })
  }

  const yearly = yearlyStats.find((item) => item.year === year)
  return [{ periodLabel: `${year}`, totalPatients: Number(yearly?.totalPatients || 0) }]
}

export default function StatisticsPage() {
  const { message } = App.useApp()
  const [loading, setLoading] = useState(false)
  const [selectedYear, setSelectedYear] = useState(undefined)
  const [selectedCycle, setSelectedCycle] = useState(CYCLE.MONTHLY)
  const [appliedYear, setAppliedYear] = useState(undefined)
  const [appliedCycle, setAppliedCycle] = useState(CYCLE.MONTHLY)
  const [monthlyStats, setMonthlyStats] = useState([])
  const [quarterlyStats, setQuarterlyStats] = useState([])
  const [yearlyStats, setYearlyStats] = useState([])
  const [monthlyRevenueStats, setMonthlyRevenueStats] = useState([])
  const [quarterlyRevenueStats, setQuarterlyRevenueStats] = useState([])
  const [yearlyRevenueStats, setYearlyRevenueStats] = useState([])

  async function loadStatistics() {
    setLoading(true)
    try {
      const [
        monthlyRes,
        quarterlyRes,
        yearlyRes,
        monthlyRevenueRes,
        quarterlyRevenueRes,
        yearlyRevenueRes,
      ] = await Promise.all([
        getPatientStatisticsByMonth(),
        getPatientStatisticsByQuarter(),
        getPatientStatisticsByYear(),
        getRevenueStatisticsByMonth(),
        getRevenueStatisticsByQuarter(),
        getRevenueStatisticsByYear(),
      ])

      setMonthlyStats(monthlyRes?.result || [])
      setQuarterlyStats(quarterlyRes?.result || [])
      setYearlyStats(yearlyRes?.result || [])
      setMonthlyRevenueStats(monthlyRevenueRes?.result || [])
      setQuarterlyRevenueStats(quarterlyRevenueRes?.result || [])
      setYearlyRevenueStats(yearlyRevenueRes?.result || [])
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadStatistics()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const yearOptions = useMemo(() => {
    const years = new Set()
    yearlyStats.forEach((item) => {
      if (item.year) years.add(item.year)
    })
    yearlyRevenueStats.forEach((item) => {
      if (item.year) years.add(item.year)
    })
    return Array.from(years)
      .sort((a, b) => b - a)
      .map((year) => ({ label: `${year}`, value: year }))
  }, [yearlyRevenueStats, yearlyStats])

  useEffect(() => {
    if (!yearOptions.length) return
    if (selectedYear !== undefined) return
    const latestYear = yearOptions[0].value
    setSelectedYear(latestYear)
    setAppliedYear(latestYear)
  }, [selectedYear, yearOptions])

  function applyFilter() {
    setAppliedYear(selectedYear)
    setAppliedCycle(selectedCycle)
  }

  const revenueSeries = useMemo(
    () =>
      buildRevenueSeries(
        appliedCycle,
        appliedYear,
        monthlyRevenueStats,
        quarterlyRevenueStats,
        yearlyRevenueStats,
      ),
    [appliedCycle, appliedYear, monthlyRevenueStats, quarterlyRevenueStats, yearlyRevenueStats],
  )

  const patientSeries = useMemo(
    () => buildPatientSeries(appliedCycle, appliedYear, monthlyStats, quarterlyStats, yearlyStats),
    [appliedCycle, appliedYear, monthlyStats, quarterlyStats, yearlyStats],
  )

  const totalInvoices = revenueSeries.reduce((sum, item) => sum + (item.transactions || 0), 0)
  const totalCustomers = patientSeries.reduce((sum, item) => sum + (item.totalPatients || 0), 0)
  const avgInvoices = revenueSeries.length ? Math.round(totalInvoices / revenueSeries.length) : 0
  const avgCustomers = patientSeries.length ? Math.round(totalCustomers / patientSeries.length) : 0
  const totalRevenue = revenueSeries.reduce((sum, item) => sum + toAmountNumber(item.amount), 0)
  const avgRevenue = revenueSeries.length ? totalRevenue / revenueSeries.length : 0

  const revenueChartConfig = {
    data: revenueSeries,
    xField: 'periodLabel',
    yField: 'transactions',
    color: '#1890ff',
    smooth: true,
    point: { size: 4, shape: 'circle' },
    area: { style: { fillOpacity: 0.14 } },
    autoFit: true,
    xAxis: { title: false },
    yAxis: { title: false },
    tooltip: {
      items: [
        (datum) => ({ name: 'Hóa đơn', value: `${datum.transactions}` }),
        (datum) => ({ name: 'Doanh thu', value: formatCurrency(datum.amount) }),
      ],
    },
  }

  const patientChartConfig = {
    data: patientSeries,
    xField: 'periodLabel',
    yField: 'totalPatients',
    color: '#52c41a',
    label: false,
    autoFit: true,
    xAxis: { title: false },
    yAxis: { title: false },
    tooltip: {
      items: [
        (datum) => ({
          name: 'Số bệnh nhân',
          value: `${datum.totalPatients}`,
        }),
      ],
    },
  }

  return (
    <Space orientation="vertical" size={20} style={{ width: '100%' }}>
      <div>
        <Space size={10} align="center">
          <BarChartOutlined style={{ fontSize: 24, color: '#1890ff' }} />
          <Title level={2} style={{ margin: 0 }}>
            Thống kê hệ thống
          </Title>
        </Space>
        <Text type="secondary">Theo dõi hiệu suất hóa đơn và khách hàng theo thời gian</Text>
      </div>

      <Card loading={loading}>
        <Space size={12} wrap>
          <Text strong>Năm:</Text>
          <Select
            style={{ minWidth: 120 }}
            value={selectedYear}
            options={yearOptions}
            onChange={setSelectedYear}
            placeholder="Chọn năm"
          />
          <Text strong>Chu kỳ:</Text>
          <Select
            style={{ minWidth: 140 }}
            value={selectedCycle}
            options={[
              { label: 'Theo tháng', value: CYCLE.MONTHLY },
              { label: 'Theo quý', value: CYCLE.QUARTERLY },
              { label: 'Theo năm', value: CYCLE.YEARLY },
            ]}
            onChange={setSelectedCycle}
          />
          <Button type="primary" icon={<RiseOutlined />} onClick={applyFilter} disabled={!selectedYear}>
            Xem thống kê
          </Button>
        </Space>
      </Card>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="Tổng hóa đơn năm"
              value={totalInvoices}
              prefix={<FileTextOutlined style={{ color: '#1677ff' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="Tổng khách hàng năm"
              value={totalCustomers}
              prefix={<UserOutlined style={{ color: '#52c41a' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title={`Trung bình hóa đơn/${cycleLabel(appliedCycle).replace('Theo ', '')}`}
              value={avgInvoices}
              prefix={<FileTextOutlined style={{ color: '#faad14' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title={`Trung bình khách hàng/${cycleLabel(appliedCycle).replace('Theo ', '')}`}
              value={avgCustomers}
              prefix={<UserOutlined style={{ color: '#ff4d4f' }} />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card
            loading={loading}
            title={`Thống kê hóa đơn ${appliedYear || ''}`}
            extra={<Text type="secondary">{cycleLabel(appliedCycle)}</Text>}
          >
            {revenueSeries.length ? (
              <Line {...revenueChartConfig} />
            ) : (
              <Empty description="Chưa có dữ liệu hóa đơn" />
            )}
            <Text type="secondary" style={{ marginTop: 8, display: 'block' }}>
              Tổng doanh thu: {formatCurrency(totalRevenue)} | Trung bình doanh thu/{cycleLabel(appliedCycle).replace('Theo ', '')}:{' '}
              {formatCurrency(avgRevenue)}
            </Text>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card
            loading={loading}
            title={`Thống kê khách hàng ${appliedYear || ''}`}
            extra={<Text type="secondary">{cycleLabel(appliedCycle)}</Text>}
          >
            {patientSeries.length ? (
              <Column {...patientChartConfig} />
            ) : (
              <Empty description="Chưa có dữ liệu khách hàng" />
            )}
          </Card>
        </Col>
      </Row>
    </Space>
  )
}
