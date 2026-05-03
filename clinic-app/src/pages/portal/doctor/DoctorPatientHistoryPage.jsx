import {
  EyeOutlined,
  FileTextOutlined,
  HistoryOutlined,
  SearchOutlined,
  UserOutlined,
} from '@ant-design/icons'
import {
  App,
  Button,
  Card,
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
import 'dayjs/locale/vi'
import { useEffect, useRef, useState } from 'react'
import { useLocation, useSearchParams } from 'react-router-dom'
import { getLabTestById } from '../../../services/doctor/labTestService.js'
import {
  getMedicalRecordById,
  getPatientMedicalHistory,
  searchDoctorPatients,
} from '../../../services/doctor/medicalRecordService.js'
import { getPrescriptionById } from '../../../services/doctor/prescriptionService.js'
import { getErrorMessage } from '../../../utils/httpError.js'
import './DoctorPatientHistoryPage.css'

dayjs.locale('vi')

const { Title, Text, Paragraph } = Typography

function normalizeText(value) {
  return (value || '').trim().toLowerCase()
}

function formatDateTime(value) {
  if (!value) return '-'
  const parsed = dayjs(value)
  if (!parsed.isValid()) return value
  return parsed.format('HH:mm DD/MM/YYYY')
}

function buildPatientQueryString(patient) {
  const params = new URLSearchParams()

  if (patient?.patientId) {
    params.set('patientId', patient.patientId)
  }

  return params.toString()
}

export default function DoctorPatientHistoryPage() {
  const { message } = App.useApp()
  const location = useLocation()
  const [searchParams, setSearchParams] = useSearchParams()
  const prefilledPatientId = searchParams.get('patientId') || ''
  const patientFromState = location.state?.patient || null
  const searchTimerRef = useRef(null)
  const [keyword, setKeyword] = useState(patientFromState?.fullName || '')
  const [patientLoading, setPatientLoading] = useState(false)
  const [patients, setPatients] = useState([])
  const [patientPage, setPatientPage] = useState(1)
  const [patientPageSize, setPatientPageSize] = useState(10)
  const [patientTotal, setPatientTotal] = useState(0)
  const [selectedPatient, setSelectedPatient] = useState(
    prefilledPatientId
      ? {
          patientId: prefilledPatientId,
          fullName: patientFromState?.fullName,
          email: patientFromState?.email,
        }
      : null,
  )
  const [historyLoading, setHistoryLoading] = useState(false)
  const [historyItems, setHistoryItems] = useState([])
  const [historyPage, setHistoryPage] = useState(1)
  const [historyPageSize, setHistoryPageSize] = useState(10)
  const [historyTotal, setHistoryTotal] = useState(0)
  const [detailLoading, setDetailLoading] = useState(false)
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailRecord, setDetailRecord] = useState(null)
  const [detailPrescription, setDetailPrescription] = useState(null)
  const [detailLabTests, setDetailLabTests] = useState([])

  function resolveMedicalRecordId(item) {
    return item?.medicalRecordId || item?.medicalRecordID || item?.id || null
  }

  function resetDetailState() {
    setDetailOpen(false)
    setDetailRecord(null)
    setDetailPrescription(null)
    setDetailLabTests([])
  }

  async function loadPatients({ nextPage = patientPage, nextPageSize = patientPageSize, nextKeyword = keyword.trim() } = {}) {
    setPatientLoading(true)
    try {
      const response = await searchDoctorPatients({
        name: nextKeyword || undefined,
        page: nextPage - 1,
        size: nextPageSize,
      })
      const pageData = response?.result
      const patientItems = pageData?.content || []
      setPatients(patientItems)
      setPatientPage((pageData?.page ?? 0) + 1)
      setPatientPageSize(pageData?.size || nextPageSize)
      setPatientTotal(pageData?.totalElements || 0)

      if (selectedPatient?.patientId && (!selectedPatient.fullName || !selectedPatient.email)) {
        const matchedPatient = patientItems.find((item) => item.patientId === selectedPatient.patientId)
        if (matchedPatient) {
          setSelectedPatient(matchedPatient)
        }
      }
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setPatientLoading(false)
    }
  }

  async function loadMedicalHistory(patient, { nextPage = 1, nextPageSize = historyPageSize } = {}) {
    if (!patient?.patientId) {
      setHistoryItems([])
      setHistoryTotal(0)
      setHistoryPage(1)
      return
    }

    setHistoryLoading(true)
    try {
      const response = await getPatientMedicalHistory(patient.patientId, {
        page: nextPage - 1,
        size: nextPageSize,
      })
      const pageData = response?.result
      setHistoryItems(pageData?.content || [])
      setHistoryPage((pageData?.page ?? 0) + 1)
      setHistoryPageSize(pageData?.size || nextPageSize)
      setHistoryTotal(pageData?.totalElements || 0)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setHistoryLoading(false)
    }
  }

  useEffect(() => {
    if (selectedPatient?.patientId) return
    loadPatients({ nextPage: 1, nextPageSize: patientPageSize, nextKeyword: keyword.trim() })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedPatient?.patientId])

  useEffect(() => {
    if (selectedPatient?.patientId) return undefined
    if (searchTimerRef.current) {
      window.clearTimeout(searchTimerRef.current)
    }

    searchTimerRef.current = window.setTimeout(() => {
      setPatientPage(1)
      loadPatients({ nextPage: 1, nextPageSize: patientPageSize, nextKeyword: keyword.trim() })
    }, 400)

    return () => {
      if (searchTimerRef.current) {
        window.clearTimeout(searchTimerRef.current)
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyword])

  useEffect(() => {
    if (!selectedPatient?.patientId) {
      setHistoryItems([])
      setHistoryTotal(0)
      return
    }
    loadMedicalHistory(selectedPatient, { nextPage: 1, nextPageSize: historyPageSize })
    setSearchParams((current) => {
      const next = new URLSearchParams(current)
      next.delete('patientId')
      const patientQuery = buildPatientQueryString(selectedPatient)
      if (patientQuery) {
        return new URLSearchParams(patientQuery)
      }
      return next
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedPatient?.patientId])

  async function openHistoryDetail(historyItem) {
    const medicalRecordId = resolveMedicalRecordId(historyItem)
    if (!medicalRecordId) return

    setDetailLoading(true)
    setDetailOpen(true)
    setDetailRecord(null)
    setDetailPrescription(null)
    setDetailLabTests([])
    try {
      const medicalRecordResponse = await getMedicalRecordById(medicalRecordId)
      const medicalRecord = medicalRecordResponse?.result || null
      setDetailRecord(medicalRecord)

      const prescriptionId = medicalRecord?.prescriptionId || historyItem?.prescriptionId
      if (prescriptionId) {
        const prescriptionResponse = await getPrescriptionById(prescriptionId)
        setDetailPrescription(prescriptionResponse?.result || null)
      }

      const labTestOrderIds = medicalRecord?.labTestOrderIds || historyItem?.labTestOrderIds || []
      if (labTestOrderIds.length) {
        const labResponses = await Promise.all(
          labTestOrderIds.map((labTestId) => getLabTestById(labTestId)),
        )
        setDetailLabTests(labResponses.map((response) => response?.result).filter(Boolean))
      }
    } catch (error) {
      resetDetailState()
      message.error(getErrorMessage(error))
    } finally {
      setDetailLoading(false)
    }
  }

  function handleSelectPatient(patient) {
    setSelectedPatient(patient)
    setHistoryPage(1)
  }

  function handleResetSelectedPatient() {
    setSelectedPatient(null)
    setHistoryItems([])
    setHistoryTotal(0)
    setHistoryPage(1)
    setKeyword('')
    resetDetailState()
  }

  const patientColumns = [
    {
      title: 'Bệnh nhân',
      dataIndex: 'fullName',
      key: 'fullName',
      render: (_, record) => (
        <Button
          type="link"
          style={{ paddingInline: 0, textAlign: 'left' }}
          onClick={() => handleSelectPatient(record)}
        >
          {record.fullName || '-'}
        </Button>
      ),
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      render: (value) => value || '-',
    },
  ]

  const historyColumns = [
    {
      title: 'Lần khám',
      dataIndex: 'visitedAt',
      key: 'visitedAt',
      width: 180,
      render: (value) => formatDateTime(value),
    },
    {
      title: 'Chẩn đoán',
      dataIndex: 'diagnosis',
      key: 'diagnosis',
      render: (value) => value || '-',
    },
    {
      title: 'Bác sĩ',
      dataIndex: 'doctorName',
      key: 'doctorName',
      width: 180,
      render: (value) => value || '-',
    },
    {
      title: 'Đơn thuốc',
      dataIndex: 'prescriptionId',
      key: 'prescriptionId',
      width: 120,
      render: (value) => (value ? <Tag color="success">Có</Tag> : <Tag>Không</Tag>),
    },
    {
      title: 'Xét nghiệm',
      dataIndex: 'labTestOrderIds',
      key: 'labTestOrderIds',
      width: 130,
      render: (value) => {
        const total = value?.length || 0
        return total ? <Tag color="processing">{`${total} chỉ định`}</Tag> : <Tag>Không có</Tag>
      },
    },
    {
      title: 'Chi tiết',
      key: 'actions',
      width: 140,
      render: (_, record) => (
        <Button type="link" icon={<EyeOutlined />} onClick={() => openHistoryDetail(record)}>
          Xem hồ sơ
        </Button>
      ),
    },
  ]

  const latestVisitedAt = historyItems[0]?.visitedAt
  const selectedPatientDisplay = selectedPatient?.fullName || '-'

  return (
    <Space orientation="vertical" size="middle" className="doctor-patient-history">
      <Card className="doctor-patient-history__hero">
        <Space orientation="vertical" size="small" style={{ width: '100%' }}>
          <Space align="start" className="doctor-patient-history__hero-header">
            <div>
              <Title level={3} style={{ margin: 0 }}>
                Tra cứu lịch sử khám bệnh
              </Title>
              <Text type="secondary">
                Tìm bệnh nhân theo tên, chọn đúng hồ sơ và xem lại toàn bộ lần khám trước đây.
              </Text>
            </div>
            <Tag color="processing" icon={<HistoryOutlined />}>
              Phục vụ trước và trong khi khám
            </Tag>
          </Space>

          <Input
            allowClear
            size="large"
            prefix={<SearchOutlined />}
            placeholder="Nhập tên bệnh nhân để tìm kiếm"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
          />
        </Space>
      </Card>

      {!selectedPatient ? (
        <Card
          title="Danh sách bệnh nhân"
          extra={patientTotal ? `${patientTotal} kết quả` : null}
          className="doctor-patient-history__section"
        >
          <Table
            rowKey="patientId"
            loading={patientLoading}
            columns={patientColumns}
            dataSource={patients}
            size="middle"
            locale={{ emptyText: <Empty description="Chưa tìm thấy bệnh nhân" /> }}
            pagination={{
              current: patientPage,
              pageSize: patientPageSize,
              total: patientTotal,
              size: 'small',
              showSizeChanger: true,
            }}
            scroll={{ x: 720, y: 420 }}
            onRow={(record) => ({
              onClick: () => handleSelectPatient(record),
            })}
            onChange={(pagination) => {
              const nextPage = pagination.current || 1
              const nextPageSize = pagination.pageSize || 10
              setPatientPage(nextPage)
              setPatientPageSize(nextPageSize)
              loadPatients({ nextPage, nextPageSize, nextKeyword: keyword.trim() })
            }}
          />
        </Card>
      ) : (
        <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
          <Card
            title="Bệnh nhân đã chọn"
            extra={(
              <Button type="link" style={{ paddingInline: 0 }} onClick={handleResetSelectedPatient}>
                Chọn bệnh nhân khác
              </Button>
            )}
            className="doctor-patient-history__section"
          >
            <div className="doctor-patient-history__selected-card">
              <div className="doctor-patient-history__selected-main">
                <span className="doctor-patient-history__identity-icon">
                  <UserOutlined />
                </span>
                <div className="doctor-patient-history__identity-text">
                  <Title level={4} style={{ margin: 0 }}>
                    {selectedPatientDisplay}
                  </Title>
                  <Text type="secondary">{selectedPatient.email || 'Chưa có email'}</Text>
                </div>
              </div>

              <div className="doctor-patient-history__selected-stats">
                <div className="doctor-patient-history__selected-stat">
                  <Text type="secondary">Tổng hồ sơ</Text>
                  <strong>{historyTotal}</strong>
                </div>
                <div className="doctor-patient-history__selected-stat">
                  <Text type="secondary">Đang hiển thị</Text>
                  <strong>{historyItems.length}</strong>
                </div>
                <div className="doctor-patient-history__selected-stat">
                  <Text type="secondary">Lần gần nhất</Text>
                  <strong>{latestVisitedAt ? formatDateTime(latestVisitedAt) : '-'}</strong>
                </div>
              </div>
            </div>
          </Card>

          <Card
            title={`Danh sách bệnh án của ${selectedPatientDisplay}`}
            extra={`Tổng ${historyTotal} hồ sơ`}
            className="doctor-patient-history__section"
          >
            <Text type="secondary" className="doctor-patient-history__section-caption">
              Chọn một bệnh án để xem đầy đủ chẩn đoán, đơn thuốc và xét nghiệm.
            </Text>
            <Table
              rowKey="medicalRecordId"
              loading={historyLoading}
              columns={historyColumns}
              dataSource={historyItems}
              size="middle"
              scroll={{ x: 860 }}
              locale={{ emptyText: <Empty description="Bệnh nhân này chưa có lịch sử khám" /> }}
              onRow={(record) => ({
                onClick: () => openHistoryDetail(record),
              })}
              pagination={{
                current: historyPage,
                pageSize: historyPageSize,
                total: historyTotal,
                showSizeChanger: true,
                showTotal: (value) => `Tổng ${value} hồ sơ`,
              }}
              onChange={(pagination) => {
                const nextPage = pagination.current || 1
                const nextPageSize = pagination.pageSize || 10
                setHistoryPage(nextPage)
                setHistoryPageSize(nextPageSize)
                loadMedicalHistory(selectedPatient, { nextPage, nextPageSize })
              }}
            />
          </Card>
        </Space>
      )}

      <Drawer
        title="Chi tiết hồ sơ bệnh án"
        size="large"
        open={detailOpen}
        onClose={resetDetailState}
      >
        {detailLoading ? (
          <Card loading />
        ) : detailRecord ? (
          <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
            <Card size="small" className="doctor-patient-history__detail-card">
              <Descriptions bordered column={1} size="small">
                <Descriptions.Item label="Ngày khám">
                  {formatDateTime(detailRecord.visitedAt)}
                </Descriptions.Item>
                <Descriptions.Item label="Triệu chứng">
                  <Paragraph style={{ margin: 0 }}>{detailRecord.symptoms || '-'}</Paragraph>
                </Descriptions.Item>
                <Descriptions.Item label="Chẩn đoán">
                  <Paragraph style={{ margin: 0 }}>{detailRecord.diagnosis || '-'}</Paragraph>
                </Descriptions.Item>
                <Descriptions.Item label="Kết luận">
                  <Paragraph style={{ margin: 0 }}>{detailRecord.conclusion || '-'}</Paragraph>
                </Descriptions.Item>
              </Descriptions>
            </Card>

            <Card size="small" title="Đơn thuốc" className="doctor-patient-history__detail-card">
              {detailPrescription ? (
                <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
                  <Descriptions column={1} size="small">
                    <Descriptions.Item label="Bác sĩ">{detailPrescription.doctorName || '-'}</Descriptions.Item>
                    <Descriptions.Item label="Hướng dẫn sử dụng">
                      <Paragraph style={{ margin: 0 }}>{detailPrescription.instructions || '-'}</Paragraph>
                    </Descriptions.Item>
                  </Descriptions>
                  <Table
                    rowKey={(record, index) => `${record.medicineName}-${index}`}
                    pagination={false}
                    size="small"
                    dataSource={detailPrescription.items || []}
                    locale={{ emptyText: 'Không có thuốc trong đơn' }}
                    columns={[
                      { title: 'Tên thuốc', dataIndex: 'medicineName', key: 'medicineName' },
                      { title: 'Số lượng', dataIndex: 'quantity', key: 'quantity', width: 90 },
                      { title: 'Liều dùng', dataIndex: 'dosage', key: 'dosage', render: (value) => value || '-' },
                      { title: 'Tần suất', dataIndex: 'frequency', key: 'frequency', render: (value) => value || '-' },
                      { title: 'Số ngày', dataIndex: 'durationDays', key: 'durationDays', width: 90, render: (value) => value ?? '-' },
                      { title: 'Ghi chú', dataIndex: 'note', key: 'note', render: (value) => value || '-' },
                    ]}
                  />
                </Space>
              ) : (
                <Empty description="Không có đơn thuốc cho lần khám này" />
              )}
            </Card>

            <Card size="small" title="Xét nghiệm" className="doctor-patient-history__detail-card">
              {detailLabTests.length ? (
                <Space orientation="vertical" size="small" style={{ width: '100%' }}>
                  {detailLabTests.map((labTest) => (
                    <Card key={labTest.id} size="small" className="doctor-patient-history__lab-card">
                      <Descriptions column={1} size="small">
                        <Descriptions.Item label="Tên xét nghiệm">{labTest.testName || '-'}</Descriptions.Item>
                        <Descriptions.Item label="Ghi chú chỉ định">{labTest.requestNote || '-'}</Descriptions.Item>
                        <Descriptions.Item label="Kết quả">
                          {labTest.result?.resultValue || 'Chưa có kết quả'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Khoảng bình thường">
                          {labTest.result?.normalRange || '-'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Tệp đính kèm">
                          {labTest.result?.attachmentUrl || '-'}
                        </Descriptions.Item>
                      </Descriptions>
                    </Card>
                  ))}
                </Space>
              ) : (
                <Empty description="Không có chỉ định xét nghiệm" />
              )}
            </Card>
          </Space>
        ) : (
          <Empty description="Không có dữ liệu hồ sơ" />
        )}
      </Drawer>
    </Space>
  )
}