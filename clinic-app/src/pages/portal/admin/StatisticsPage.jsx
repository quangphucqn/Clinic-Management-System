import { Column } from '@ant-design/plots'
import { App, Card, Empty, Segmented, Space, Statistic, Table, Tag, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import {
  getPatientStatisticsByMonth,
  getPatientStatisticsByQuarter,
  getPatientStatisticsByYear,
} from '../../../services/patientStatisticsService.js'
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
  const [viewMode, setViewMode] = useState(VIEW_MODE.MONTHLY)
  const [monthlyStats, setMonthlyStats] = useState([])
  const [quarterlyStats, setQuarterlyStats] = useState([])
  const [yearlyStats, setYearlyStats] = useState([])

  async function loadStatistics() {
    setLoading(true)
    try {
      const [monthlyRes, quarterlyRes, yearlyRes] = await Promise.all([
        getPatientStatisticsByMonth(),
        getPatientStatisticsByQuarter(),
        getPatientStatisticsByYear(),
      ])
      setMonthlyStats(monthlyRes?.result || [])
      setQuarterlyStats(quarterlyRes?.result || [])
      setYearlyStats(yearlyRes?.result || [])
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
    if (viewMode === VIEW_MODE.QUARTERLY) return quarterlyStats
    if (viewMode === VIEW_MODE.YEARLY) return yearlyStats
    return monthlyStats
  }, [monthlyStats, quarterlyStats, viewMode, yearlyStats])

  const latestYearlyTotal = yearlyStats[0]?.totalPatients ?? 0
  const allPatientTotal = yearlyStats.reduce(
    (sum, item) => sum + (item.totalPatients || 0),
    0,
  )
  const chartData = useMemo(
    () =>
      currentStats
        .slice()
        .reverse()
        .map((item) => ({
          period: formatPeriod(item, viewMode),
          totalPatients: item.totalPatients || 0,
        })),
    [currentStats, viewMode],
  )

  const chartConfig = {
    data: chartData,
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
        render: (event, { title, items }) => (
          <div style={{ padding: 8 }}>
            <div style={{ marginBottom: 6, fontWeight: 600 }}>{title}</div>
            <div>{`Số bệnh nhân: ${items?.[0]?.value ?? 0}`}</div>
          </div>
        ),
      },
    },
  }

  const columns = [
    {
      title: 'Mốc thời gian',
      key: 'period',
      render: (_, record) => formatPeriod(record, viewMode),
    },
    {
      title: 'Số bệnh nhân',
      dataIndex: 'totalPatients',
      key: 'totalPatients',
      width: 180,
      render: (value) => <Tag color="blue">{value ?? 0}</Tag>,
    },
  ]

  return (
    <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
      <Title level={3} style={{ margin: 0 }}>
        Thống kê
      </Title>

      <Space wrap style={{ width: '100%' }}>
        <Card style={{ minWidth: 280, flex: 1 }}>
          <Statistic title="Doanh thu" value="Đang phát triển" />
          <Text type="secondary">Sẽ bổ sung bộ lọc theo thời gian và biểu đồ doanh thu.</Text>
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
            Thống kê số bệnh nhân
          </Title>
          <Segmented
            value={viewMode}
            options={[
              { label: 'Theo tháng', value: VIEW_MODE.MONTHLY },
              { label: 'Theo quý', value: VIEW_MODE.QUARTERLY },
              { label: 'Theo năm', value: VIEW_MODE.YEARLY },
            ]}
            onChange={setViewMode}
          />
        </Space>
        {chartData.length > 0 ? (
          <Column {...chartConfig} />
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
          columns={columns}
          dataSource={currentStats}
          pagination={{ pageSize: 10, showSizeChanger: false }}
        />
      </Card>
    </Space>
  )
}

