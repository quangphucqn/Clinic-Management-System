import { Column } from '@ant-design/plots'
import { App, Card, Empty, Segmented, Space, Statistic, Table, Tag, Typography } from 'antd'
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

const VIEW_MODE = {
  MONTHLY: 'MONTHLY',
  QUARTERLY: 'QUARTERLY',
  YEARLY: 'YEARLY',
}

function formatPeriod(record, mode) {
  if (mode === VIEW_MODE.MONTHLY) return `${record.month}/${record.year}`
  if (mode === VIEW_MODE.QUARTERLY) return `Q${record.quarter}/${record.year}`
  return `${record.year}`
}

export default function StatisticsPage() {
  const { message } = App.useApp()
  const [loading, setLoading] = useState(false)
  const [patientViewMode, setPatientViewMode] = useState(VIEW_MODE.MONTHLY)
  const [revenueViewMode, setRevenueViewMode] = useState(VIEW_MODE.MONTHLY)
  const [monthlyStats, setMonthlyStats] = useState([])
  const [quarterlyStats, setQuarterlyStats] = useState([])
  const [yearlyStats, setYearlyStats] = useState([])
  const [monthlyRevenueStats, setMonthlyRevenueStats] = useState([])
  const [quarterlyRevenueStats, setQuarterlyRevenueStats] = useState([])
  const [yearlyRevenueStats, setYearlyRevenueStats] = useState([])

  function toAmountNumber(value) {
    const numberValue = Number(value)
    return Number.isFinite(numberValue) ? numberValue : 0
  }

  function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      maximumFractionDigits: 0,
    }).format(toAmountNumber(value))
  }

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

  const currentStats = useMemo(() => {
    if (patientViewMode === VIEW_MODE.QUARTERLY) return quarterlyStats
    if (patientViewMode === VIEW_MODE.YEARLY) return yearlyStats
    return monthlyStats
  }, [monthlyStats, patientViewMode, quarterlyStats, yearlyStats])

  const currentRevenueStats = useMemo(() => {
    if (revenueViewMode === VIEW_MODE.QUARTERLY) return quarterlyRevenueStats
    if (revenueViewMode === VIEW_MODE.YEARLY) return yearlyRevenueStats
    return monthlyRevenueStats
  }, [monthlyRevenueStats, quarterlyRevenueStats, revenueViewMode, yearlyRevenueStats])

  const latestYearlyTotal = yearlyStats[0]?.totalPatients ?? 0
  const allPatientTotal = yearlyStats.reduce(
    (sum, item) => sum + (item.totalPatients || 0),
    0,
  )
  const latestRevenueAmount = yearlyRevenueStats[0]?.totalAmount ?? 0
  const allRevenueAmount = yearlyRevenueStats.reduce(
    (sum, item) => sum + toAmountNumber(item.totalAmount),
    0,
  )

  const patientChartData = useMemo(
    () =>
      currentStats
        .slice()
        .reverse()
        .map((item) => ({
          period: formatPeriod(item, patientViewMode),
          totalPatients: item.totalPatients || 0,
        })),
    [currentStats, patientViewMode],
  )

  const revenueChartData = useMemo(
    () =>
      currentRevenueStats
        .slice()
        .reverse()
        .map((item) => ({
          period: formatPeriod(item, revenueViewMode),
          totalAmount: toAmountNumber(item.totalAmount),
        })),
    [currentRevenueStats, revenueViewMode],
  )

  const patientChartConfig = {
    data: patientChartData,
    xField: 'period',
    yField: 'totalPatients',
    color: '#6cc5d8',
    autoFit: true,
    height: 360,
    label: {
      position: 'top',
      style: {
        fill: '#4a4a4a',
      },
    },
    axis: {
      y: {
        title: 'Số bệnh nhân',
      },
      x: {
        title: 'Mốc thời gian',
      },
    },
    interaction: {
      tooltip: {
        render: (_event, { title, items }) => (
          <div style={{ padding: 8 }}>
            <div style={{ marginBottom: 6, fontWeight: 600 }}>{title}</div>
            <div>{`Số bệnh nhân: ${items?.[0]?.value ?? 0}`}</div>
          </div>
        ),
      },
    },
  }

  const revenueChartConfig = {
    data: revenueChartData,
    xField: 'period',
    yField: 'totalAmount',
    color: '#73d13d',
    autoFit: true,
    height: 360,
    label: {
      position: 'top',
      formatter: (datum) => `${Math.round(datum.totalAmount).toLocaleString('vi-VN')} đ`,
      style: { fill: '#4a4a4a' },
    },
    axis: {
      y: { title: 'Doanh thu (VND)' },
      x: { title: 'Mốc thời gian' },
    },
    interaction: {
      tooltip: {
        render: (_event, { title, items }) => (
          <div style={{ padding: 8 }}>
            <div style={{ marginBottom: 6, fontWeight: 600 }}>{title}</div>
            <div>{`Doanh thu: ${formatCurrency(items?.[0]?.value ?? 0)}`}</div>
          </div>
        ),
      },
    },
  }

  const patientColumns = [
    {
      title: 'Mốc thời gian',
      key: 'period',
      render: (_, record) => formatPeriod(record, patientViewMode),
    },
    {
      title: 'Số bệnh nhân',
      dataIndex: 'totalPatients',
      key: 'totalPatients',
      width: 180,
      render: (value) => <Tag color="blue">{value ?? 0}</Tag>,
    },
  ]

  const revenueColumns = [
    {
      title: 'Mốc thời gian',
      key: 'period',
      render: (_, record) => formatPeriod(record, revenueViewMode),
    },
    {
      title: 'Doanh thu',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 220,
      render: (value) => <Tag color="green">{formatCurrency(value)}</Tag>,
    },
    {
      title: 'Số giao dịch thành công',
      dataIndex: 'totalTransactions',
      key: 'totalTransactions',
      width: 180,
      render: (value) => value ?? 0,
    },
  ]

  return (
    <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
      <Title level={3} style={{ margin: 0 }}>
        Thống kê
      </Title>

      <Space wrap style={{ width: '100%' }}>
        <Card style={{ minWidth: 280, flex: 1 }}>
          <Statistic title="Tổng doanh thu (toàn thời gian)" value={formatCurrency(allRevenueAmount)} />
          <Text type="secondary">
            Năm gần nhất: {yearlyRevenueStats[0]?.year || '-'} - {formatCurrency(latestRevenueAmount)}
          </Text>
        </Card>
        <Card style={{ minWidth: 280, flex: 1 }}>
          <Statistic title="Tổng bệnh nhân (toàn thời gian)" value={allPatientTotal} />
          <Text type="secondary">
            Năm gần nhất: {yearlyStats[0]?.year || '-'} - {latestYearlyTotal} bệnh nhân
          </Text>
        </Card>
      </Space>

      <Card>
        <Space
          style={{ width: '100%', justifyContent: 'space-between', marginBottom: 12 }}
          align="center"
          wrap
        >
          <Title level={5} style={{ margin: 0 }}>
            Thống kê doanh thu
          </Title>
          <Segmented
            value={revenueViewMode}
            options={[
              { label: 'Theo tháng', value: VIEW_MODE.MONTHLY },
              { label: 'Theo quý', value: VIEW_MODE.QUARTERLY },
              { label: 'Theo năm', value: VIEW_MODE.YEARLY },
            ]}
            onChange={setRevenueViewMode}
          />
        </Space>
        {revenueChartData.length > 0 ? (
          <Column {...revenueChartConfig} />
        ) : (
          <Empty description="Chưa có dữ liệu doanh thu" style={{ marginBlock: 40 }} />
        )}
        <Title level={5} style={{ marginBottom: 12 }}>
          Dữ liệu chi tiết doanh thu
        </Title>
        <Table
          rowKey={(record) =>
            `revenue-${record.year || '0'}-${record.month || '0'}-${record.quarter || '0'}`
          }
          loading={loading}
          columns={revenueColumns}
          dataSource={currentRevenueStats}
          pagination={{ pageSize: 10, showSizeChanger: false }}
        />
      </Card>

      <Card>
        <Space
          style={{ width: '100%', justifyContent: 'space-between', marginBottom: 12 }}
          align="center"
          wrap
        >
          <Title level={5} style={{ margin: 0 }}>
            Thống kê số bệnh nhân
          </Title>
          <Segmented
            value={patientViewMode}
            options={[
              { label: 'Theo tháng', value: VIEW_MODE.MONTHLY },
              { label: 'Theo quý', value: VIEW_MODE.QUARTERLY },
              { label: 'Theo năm', value: VIEW_MODE.YEARLY },
            ]}
            onChange={setPatientViewMode}
          />
        </Space>
        {patientChartData.length > 0 ? (
          <Column {...patientChartConfig} />
        ) : (
          <Empty description="Chưa có dữ liệu thống kê" style={{ marginBlock: 40 }} />
        )}
        <Title level={5} style={{ marginBottom: 12 }}>
          Dữ liệu chi tiết
        </Title>
        <Table
          rowKey={(record) =>
            `${record.year || '0'}-${record.month || '0'}-${record.quarter || '0'}`
          }
          loading={loading}
          columns={patientColumns}
          dataSource={currentStats}
          pagination={{ pageSize: 10, showSizeChanger: false }}
        />
      </Card>
    </Space>
  )
}

