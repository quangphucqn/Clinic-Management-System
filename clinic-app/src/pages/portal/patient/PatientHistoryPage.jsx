import { App, Button, Card, Descriptions, Drawer, Empty, Space, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { getMyMedicalHistory } from '../../../services/appointmentPatientService.js'
import { getPrescriptionByMedicalRecordId } from '../../../services/prescriptionService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph, Text } = Typography

export default function PatientHistoryPage() {
  const { message } = App.useApp()
  const [loading, setLoading] = useState(false)
  const [historyItems, setHistoryItems] = useState([])
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [total, setTotal] = useState(0)
  const [selectedRecord, setSelectedRecord] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [detailPrescription, setDetailPrescription] = useState(null)

  const loadHistory = useCallback(
    async ({ nextPage = 1, nextPageSize = pageSize } = {}) => {
      try {
        setLoading(true)
        const response = await getMyMedicalHistory({
          page: nextPage - 1,
          size: nextPageSize,
        })
        const pageData = response?.result
        setHistoryItems(pageData?.content || [])
        setPage((pageData?.page ?? 0) + 1)
        setPageSize(pageData?.size || nextPageSize)
        setTotal(pageData?.totalElements || 0)
      } catch (error) {
        message.error(getErrorMessage(error))
      } finally {
        setLoading(false)
      }
    },
    [message, pageSize],
  )

  useEffect(() => {
    loadHistory({ nextPage: 1, nextPageSize: pageSize })
  }, [loadHistory, pageSize])

  const openHistoryDetail = useCallback(
    async (record) => {
      setSelectedRecord(record)
      setDetailPrescription(null)
      if (!record?.medicalRecordId) return

      try {
        setDetailLoading(true)
        const response = await getPrescriptionByMedicalRecordId(record.medicalRecordId)
        setDetailPrescription(response?.result || null)
      } catch {
        setDetailPrescription(null)
      } finally {
        setDetailLoading(false)
      }
    },
    [],
  )

  const columns = useMemo(
    () => [
      {
        title: 'Ngày khám',
        dataIndex: 'appointmentDate',
        key: 'appointmentDate',
        width: 130,
        render: (value) => (value ? dayjs(value).format('DD/MM/YYYY') : '-'),
      },
      {
        title: 'Bác sĩ',
        dataIndex: 'doctorName',
        key: 'doctorName',
        width: 220,
        render: (value) => value || '-',
      },
      {
        title: 'Khung giờ',
        dataIndex: 'timeSlot',
        key: 'timeSlot',
        width: 160,
        render: (value) => value || '-',
      },
      {
        title: 'Đã khám lúc',
        dataIndex: 'visitedAt',
        key: 'visitedAt',
        width: 170,
        render: (value) => (value ? dayjs(value).format('HH:mm DD/MM/YYYY') : '-'),
      },
      {
        title: 'Đơn thuốc',
        dataIndex: 'prescriptionId',
        key: 'prescriptionId',
        width: 130,
        render: (value) => (value ? <Tag color="success">Có</Tag> : <Tag>Không</Tag>),
      },
      {
        title: 'Xét nghiệm',
        dataIndex: 'labTestOrderIds',
        key: 'labTestOrderIds',
        width: 130,
        render: (value) => {
          const totalLab = value?.length || 0
          return totalLab ? <Tag color="processing">{`${totalLab} chỉ định`}</Tag> : <Tag>Không có</Tag>
        },
      },
    ],
    [],
  )

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Space
          align="start"
          style={{ width: '100%', justifyContent: 'space-between' }}
          wrap
        >
          <div>
            <Title level={3} style={{ margin: 0 }}>
              Lịch sử khám bệnh
            </Title>
            <Paragraph type="secondary" style={{ marginTop: 8, marginBottom: 0 }}>
              Theo dõi các lần khám: ngày khám, bác sĩ, khung giờ và chi tiết từng lần khám như chẩn
              đoán, đơn thuốc, xét nghiệm.
            </Paragraph>
          </div>
        </Space>
      </Card>

      <Card>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Text type="secondary">Nhấn vào từng dòng để xem chi tiết lần khám</Text>

          {historyItems.length ? (
            <Table
              rowKey="medicalRecordId"
              loading={loading}
              columns={columns}
              dataSource={historyItems}
              pagination={{
                current: page,
                pageSize,
                total,
                showSizeChanger: true,
                showTotal: (value) => `Tổng ${value} lần khám`,
              }}
              scroll={{ x: 940 }}
              onRow={(record) => ({
                onClick: () => openHistoryDetail(record),
                style: { cursor: 'pointer' },
              })}
              onChange={(pagination) => {
                const nextPage = pagination.current || 1
                const nextPageSize = pagination.pageSize || 10
                setPage(nextPage)
                setPageSize(nextPageSize)
                loadHistory({ nextPage, nextPageSize })
              }}
            />
          ) : (
            <Empty description="Chưa có lịch sử khám bệnh" />
          )}
        </Space>
      </Card>

      <Drawer
        title="Chi tiết lịch sử khám"
        width={520}
        open={Boolean(selectedRecord)}
        onClose={() => setSelectedRecord(null)}
      >
        {selectedRecord ? (
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="Mã hồ sơ">{selectedRecord.medicalRecordId}</Descriptions.Item>
              <Descriptions.Item label="Bác sĩ">{selectedRecord.doctorName || '-'}</Descriptions.Item>
              <Descriptions.Item label="Ngày khám">
                {selectedRecord.appointmentDate
                  ? dayjs(selectedRecord.appointmentDate).format('DD/MM/YYYY')
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Khung giờ">{selectedRecord.timeSlot || '-'}</Descriptions.Item>
              <Descriptions.Item label="Thời điểm khám">
                {selectedRecord.visitedAt
                  ? dayjs(selectedRecord.visitedAt).format('HH:mm DD/MM/YYYY')
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Triệu chứng">{selectedRecord.symptoms || '-'}</Descriptions.Item>
              <Descriptions.Item label="Chẩn đoán">{selectedRecord.diagnosis || '-'}</Descriptions.Item>
              <Descriptions.Item label="Kết luận">{selectedRecord.conclusion || '-'}</Descriptions.Item>
              <Descriptions.Item label="Chỉ định xét nghiệm">
                {selectedRecord.labTestOrderIds?.length
                  ? `${selectedRecord.labTestOrderIds.length} chỉ định`
                  : 'Không có'}
              </Descriptions.Item>
            </Descriptions>

            <Card size="small" title="Đơn thuốc">
              {detailLoading ? (
                <Card loading />
              ) : detailPrescription ? (
                <Space direction="vertical" size="small" style={{ width: '100%' }}>
                  <Descriptions size="small" column={1}>
                    <Descriptions.Item label="Bác sĩ kê đơn">
                      {detailPrescription.doctorName || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="Hướng dẫn">
                      {detailPrescription.instructions || '-'}
                    </Descriptions.Item>
                  </Descriptions>
                  <Table
                    rowKey={(record, index) => `${record.medicineName}-${index}`}
                    size="small"
                    pagination={false}
                    dataSource={detailPrescription.items || []}
                    locale={{ emptyText: 'Không có thuốc trong đơn' }}
                    columns={[
                      { title: 'Tên thuốc', dataIndex: 'medicineName', key: 'medicineName' },
                      {
                        title: 'Số lượng',
                        dataIndex: 'quantity',
                        key: 'quantity',
                        width: 90,
                        render: (value) => value ?? '-',
                      },
                      {
                        title: 'Liều dùng',
                        dataIndex: 'dosage',
                        key: 'dosage',
                        width: 120,
                        render: (value) => value || '-',
                      },
                      {
                        title: 'Tần suất',
                        dataIndex: 'frequency',
                        key: 'frequency',
                        width: 120,
                        render: (value) => value || '-',
                      },
                      {
                        title: 'Số ngày',
                        dataIndex: 'durationDays',
                        key: 'durationDays',
                        width: 90,
                        render: (value) => value ?? '-',
                      },
                    ]}
                  />
                </Space>
              ) : (
                <Empty description="Lần khám này chưa có đơn thuốc" />
              )}
            </Card>
          </Space>
        ) : null}
      </Drawer>
    </Space>
  )
}

