import { FileTextOutlined } from '@ant-design/icons'
import {
  App,
  Button,
  Card,
  DatePicker,
  Descriptions,
  Drawer,
  Empty,
  Input,
  Space,
  Table,
  Tag,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { getMyLabResults } from '../../../services/appointmentPatientService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph, Text } = Typography
const { RangePicker } = DatePicker

function formatDateTime(value) {
  if (!value) return '-'
  const parsed = dayjs(value)
  return parsed.isValid() ? parsed.format('HH:mm DD/MM/YYYY') : value
}

function normalizeText(value) {
  return (value || '').trim().toLowerCase()
}

export default function PatientLabResultsPage() {
  const { message } = App.useApp()
  const [loading, setLoading] = useState(false)
  const [results, setResults] = useState([])
  const [selectedResult, setSelectedResult] = useState(null)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [dateRange, setDateRange] = useState([])

  const loadResults = useCallback(async () => {
    try {
      setLoading(true)
      const response = await getMyLabResults()
      setResults(response?.result || [])
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [message])

  useEffect(() => {
    loadResults()
  }, [loadResults])

  const filteredResults = useMemo(() => {
    const keyword = normalizeText(searchKeyword)
    const [startDate, endDate] = dateRange || []

    return results.filter((item) => {
      const testName = normalizeText(item?.testName)
      const resultText = normalizeText(item?.result)
      const matchKeyword = !keyword || testName.includes(keyword) || resultText.includes(keyword)

      if (!matchKeyword) return false
      if (!startDate || !endDate) return true

      const itemDate = item?.date ? dayjs(item.date) : null
      if (!itemDate || !itemDate.isValid()) return false
      return (
        itemDate.isSame(startDate, 'day') ||
        itemDate.isSame(endDate, 'day') ||
        (itemDate.isAfter(startDate, 'day') && itemDate.isBefore(endDate, 'day'))
      )
    })
  }, [dateRange, results, searchKeyword])

  const columns = useMemo(
    () => [
      {
        title: 'Ngày trả kết quả',
        dataIndex: 'date',
        key: 'date',
        width: 170,
        render: formatDateTime,
      },
      {
        title: 'Tên xét nghiệm',
        dataIndex: 'testName',
        key: 'testName',
        width: 260,
        render: (value) => value || '-',
      },
      {
        title: 'Kết quả',
        dataIndex: 'result',
        key: 'result',
        render: (value) => value || '-',
      },
      {
        title: 'Tệp đính kèm',
        dataIndex: 'fileUrl',
        key: 'fileUrl',
        width: 170,
        render: (value) =>
          value ? (
            <Button
              type="link"
              icon={<FileTextOutlined />}
              onClick={(event) => {
                event.stopPropagation()
                window.open(value, '_blank', 'noopener,noreferrer')
              }}
            >
              Xem tệp
            </Button>
          ) : (
            <Tag>Không có</Tag>
          ),
      },
    ],
    [],
  )

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Space align="start" style={{ width: '100%', justifyContent: 'space-between' }} wrap>
          <div>
            <Title level={3} style={{ margin: 0 }}>
              Kết quả xét nghiệm
            </Title>
            <Paragraph type="secondary" style={{ marginTop: 8, marginBottom: 0 }}>
              Theo dõi kết quả xét nghiệm của bạn. Nhấn vào từng dòng để xem chi tiết.
            </Paragraph>
          </div>
        </Space>
      </Card>

      <Card loading={loading}>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Space wrap>
            <Input
              allowClear
              placeholder="Tìm theo tên xét nghiệm hoặc nội dung kết quả"
              value={searchKeyword}
              onChange={(event) => setSearchKeyword(event.target.value)}
              style={{ width: 360 }}
            />
            <RangePicker
              format="DD/MM/YYYY"
              value={dateRange}
              onChange={(dates) => setDateRange(dates || [])}
            />
            <Button
              onClick={() => {
                setSearchKeyword('')
                setDateRange([])
              }}
            >
              Xóa bộ lọc
            </Button>
          </Space>

          {filteredResults.length ? (
            <Table
              rowKey={(record, index) => `${record.testName}-${record.date}-${index}`}
              columns={columns}
              dataSource={filteredResults}
              pagination={{ pageSize: 10, showSizeChanger: false }}
              scroll={{ x: 920 }}
              onRow={(record) => ({
                onClick: () => setSelectedResult(record),
                style: { cursor: 'pointer' },
              })}
            />
          ) : (
            <Empty description={results.length ? 'Không có kết quả phù hợp bộ lọc' : 'Chưa có kết quả xét nghiệm'} />
          )}
        </Space>
      </Card>

      <Drawer
        title="Chi tiết kết quả xét nghiệm"
        width={520}
        open={Boolean(selectedResult)}
        onClose={() => setSelectedResult(null)}
      >
        {selectedResult ? (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="Tên xét nghiệm">
              {selectedResult.testName || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Ngày trả kết quả">
              {formatDateTime(selectedResult.date)}
            </Descriptions.Item>
            <Descriptions.Item label="Kết quả">
              <Text>{selectedResult.result || '-'}</Text>
            </Descriptions.Item>
            <Descriptions.Item label="Tệp đính kèm">
              {selectedResult.fileUrl ? (
                <Button
                  type="link"
                  icon={<FileTextOutlined />}
                  style={{ paddingInline: 0 }}
                  onClick={() => window.open(selectedResult.fileUrl, '_blank', 'noopener,noreferrer')}
                >
                  Mở tệp kết quả
                </Button>
              ) : (
                'Không có'
              )}
            </Descriptions.Item>
          </Descriptions>
        ) : null}
      </Drawer>
    </Space>
  )
}

