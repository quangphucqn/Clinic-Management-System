import {
  App,
  Button,
  Card,
  DatePicker,
  Descriptions,
  Drawer,
  Empty,
  Input,
  Modal,
  Select,
  Space,
  Table,
  Tag,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import 'dayjs/locale/vi'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '../../../constants/routes.js'
import {
  getMyAppointmentById,
  getMyAppointments,
} from '../../../services/doctor/appointmentService.js'
import { getLabTestById } from '../../../services/doctor/labTestService.js'
import { getMedicalRecordById } from '../../../services/doctor/medicalRecordService.js'
import { getPrescriptionById } from '../../../services/doctor/prescriptionService.js'
import { getErrorMessage } from '../../../utils/httpError.js'
import './DoctorSchedulePage.css'

dayjs.locale('vi')

const { Title, Text, Paragraph } = Typography

const STATUS_OPTIONS = [
  { value: 'CONFIRMED', label: 'Đã xác nhận' },
  { value: 'COMPLETED', label: 'Đã hoàn thành' },
  { value: 'CANCELLED', label: 'Đã hủy' },
]

function getStatusMeta(status) {
  switch (status) {
    case 'CONFIRMED':
      return { color: 'processing', label: 'Đã xác nhận' }
    case 'COMPLETED':
      return { color: 'success', label: 'Đã hoàn thành' }
    case 'CANCELLED':
      return { color: 'default', label: 'Đã hủy' }
    default:
      return { color: 'default', label: status || '-' }
  }
}

function formatDate(value) {
  if (!value) return '-'
  const parsed = dayjs(value)
  if (!parsed.isValid()) return value
  return parsed.format('DD/MM/YYYY')
}

function formatDateTime(value) {
  if (!value) return '-'
  const parsed = dayjs(value)
  if (!parsed.isValid()) return value
  return parsed.format('HH:mm DD/MM/YYYY')
}

function formatCurrency(value) {
  if (value == null) return '-'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(value)
}

function getLabStatusMeta(status) {
  switch (status) {
    case 'COMPLETED':
      return { color: 'success', label: 'Đã có kết quả' }
    case 'PENDING':
      return { color: 'warning', label: 'Đang chờ' }
    case 'CANCELLED':
      return { color: 'default', label: 'Đã hủy' }
    default:
      return { color: 'default', label: status || '-' }
  }
}

export default function DoctorSchedulePage() {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [detailLoading, setDetailLoading] = useState(false)
  const [medicalRecordLoading, setMedicalRecordLoading] = useState(false)
  const [prescriptionLoading, setPrescriptionLoading] = useState(false)
  const [labTestLoading, setLabTestLoading] = useState(false)
  const [appointments, setAppointments] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [appointmentDate, setAppointmentDate] = useState(() => dayjs())
  const [selectedStatus, setSelectedStatus] = useState(undefined)
  const [patientKeyword, setPatientKeyword] = useState('')
  const [debouncedPatientKeyword, setDebouncedPatientKeyword] = useState('')
  const [selectedAppointment, setSelectedAppointment] = useState(null)
  const [medicalRecord, setMedicalRecord] = useState(null)
  const [medicalRecordVisible, setMedicalRecordVisible] = useState(false)
  const [prescriptionModalOpen, setPrescriptionModalOpen] = useState(false)
  const [selectedPrescription, setSelectedPrescription] = useState(null)
  const [labTestModalOpen, setLabTestModalOpen] = useState(false)
  const [selectedLabTest, setSelectedLabTest] = useState(null)
  const [drawerOpen, setDrawerOpen] = useState(false)

  async function loadAppointments({
    nextPage = page,
    nextPageSize = pageSize,
    nextDate = appointmentDate,
    nextStatus = selectedStatus,
    nextPatientName = debouncedPatientKeyword,
  } = {}) {
    setLoading(true)
    try {
      const response = await getMyAppointments({
        appointmentDate: nextDate ? nextDate.format('YYYY-MM-DD') : undefined,
        status: nextStatus || undefined,
        patientName: nextPatientName || undefined,
        page: nextPage - 1,
        size: nextPageSize,
      })
      const pageData = response?.result
      setAppointments(pageData?.content || [])
      setTotal(pageData?.totalElements || 0)
      setPage((pageData?.page ?? 0) + 1)
      setPageSize(pageData?.size || nextPageSize)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedPatientKeyword(patientKeyword.trim())
    }, 500)

    return () => window.clearTimeout(timer)
  }, [patientKeyword])

  useEffect(() => {
    setPage(1)
    loadAppointments({
      nextPage: 1,
      nextPageSize: pageSize,
      nextDate: appointmentDate,
      nextStatus: selectedStatus,
      nextPatientName: debouncedPatientKeyword,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [appointmentDate, selectedStatus, debouncedPatientKeyword])

  async function openAppointmentDetail(appointmentId) {
    setDetailLoading(true)
    setDrawerOpen(true)
    setMedicalRecord(null)
    setMedicalRecordVisible(false)
    setSelectedPrescription(null)
    setPrescriptionModalOpen(false)
    setSelectedLabTest(null)
    setLabTestModalOpen(false)
    try {
      const response = await getMyAppointmentById(appointmentId)
      setSelectedAppointment(response?.result || null)
    } catch (error) {
      setDrawerOpen(false)
      message.error(getErrorMessage(error))
    } finally {
      setDetailLoading(false)
    }
  }

  function closeDrawer() {
    setDrawerOpen(false)
    setSelectedAppointment(null)
    setMedicalRecord(null)
    setMedicalRecordVisible(false)
    setSelectedPrescription(null)
    setPrescriptionModalOpen(false)
    setSelectedLabTest(null)
    setLabTestModalOpen(false)
  }

  async function handleViewMedicalRecord() {
    const medicalRecordId = selectedAppointment?.medicalRecordID
    if (!medicalRecordId) return

    setMedicalRecordLoading(true)
    try {
      const response = await getMedicalRecordById(medicalRecordId)
      setMedicalRecord(response?.result || null)
      setMedicalRecordVisible(true)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setMedicalRecordLoading(false)
    }
  }

  async function handleViewPrescription() {
    const prescriptionId = medicalRecord?.prescriptionId
    if (!prescriptionId) return

    setPrescriptionLoading(true)
    setPrescriptionModalOpen(true)
    try {
      const response = await getPrescriptionById(prescriptionId)
      setSelectedPrescription(response?.result || null)
    } catch (error) {
      setPrescriptionModalOpen(false)
      message.error(getErrorMessage(error))
    } finally {
      setPrescriptionLoading(false)
    }
  }

  async function handleViewLabTest(labTestId) {
    if (!labTestId) return

    setLabTestLoading(true)
    setLabTestModalOpen(true)
    try {
      const response = await getLabTestById(labTestId)
      setSelectedLabTest(response?.result || null)
    } catch (error) {
      setLabTestModalOpen(false)
      message.error(getErrorMessage(error))
    } finally {
      setLabTestLoading(false)
    }
  }

  function openExaminationWorkspace(appointmentId) {
    if (!appointmentId) return
    navigate(ROUTES.doctorExamination(appointmentId))
  }

  function getExaminationActionLabel(appointment) {
    return appointment?.medicalRecordID ? 'Tiếp tục khám' : 'Bắt đầu khám'
  }

  function canOpenExamination(appointment) {
    return appointment?.status !== 'CANCELLED'
  }

  const columns = [
    {
      title: 'STT',
      key: 'index',
      width: 80,
      render: (_, __, index) => (page - 1) * pageSize + index + 1,
    },
    {
      title: 'Tên bệnh nhân',
      dataIndex: ['patient', 'fullName'],
      key: 'patientName',
      render: (_, record) => (
        <Button
          type="link"
          style={{ paddingInline: 0 }}
          onClick={() => openAppointmentDetail(record.id)}
        >
          {record.patient?.fullName || '-'}
        </Button>
      ),
    },
    {
      title: 'Ngày khám',
      dataIndex: 'appointmentDate',
      key: 'appointmentDate',
      width: 160,
      render: (value) => formatDate(value),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 180,
      render: (value) => {
        const meta = getStatusMeta(value)
        return <Tag color={meta.color}>{meta.label}</Tag>
      },
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 140,
      render: (_, record) => (
        <Button type="link" onClick={() => openAppointmentDetail(record.id)}>
          Xem chi tiết
        </Button>
      ),
    },
  ]

  return (
    <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Space
          className="doctor-schedule__toolbar"
          align="start"
          wrap
        >
          <div className="doctor-schedule__heading">
            <Title level={3} style={{ margin: 0 }}>
              Lịch khám theo ngày
            </Title>
            <Text type="secondary">
              Theo dõi lịch hẹn của bác sĩ theo ngày, lọc theo trạng thái và tìm nhanh bệnh nhân.
            </Text>
          </div>

          <Space wrap className="doctor-schedule__filters">
            <DatePicker
              value={appointmentDate}
              format="DD/MM/YYYY"
              allowClear={false}
              onChange={(value) => setAppointmentDate(value || dayjs())}
            />
            <Select
              allowClear
              placeholder="Lọc theo trạng thái"
              options={STATUS_OPTIONS}
              value={selectedStatus}
              onChange={(value) => setSelectedStatus(value)}
              style={{ width: 180 }}
            />
            <Input
              allowClear
              placeholder="Tìm theo tên bệnh nhân"
              value={patientKeyword}
              onChange={(event) => setPatientKeyword(event.target.value)}
              style={{ width: 240 }}
            />
          </Space>
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={appointments}
          locale={{
            emptyText: (
              <Empty description="Không có lịch khám phù hợp" />
            ),
          }}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (value) => `Tổng ${value} lịch khám`,
          }}
          onChange={(pagination) => {
            const nextPage = pagination.current || 1
            const nextPageSize = pagination.pageSize || 10
            setPage(nextPage)
            setPageSize(nextPageSize)
            loadAppointments({ nextPage, nextPageSize })
          }}
        />
      </Card>

      <Drawer
        title="Chi tiết lịch khám"
        size="large"
        open={drawerOpen}
        onClose={closeDrawer}
      >
        {detailLoading ? (
          <Card loading />
        ) : selectedAppointment ? (
          <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
            <Descriptions bordered column={1} size="small">
              <Descriptions.Item label="Tên bệnh nhân">
                {selectedAppointment.patient?.fullName || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Ngày khám">
                {formatDate(selectedAppointment.appointmentDate)}
              </Descriptions.Item>
              <Descriptions.Item label="Khung giờ">
                {selectedAppointment.timeSlot || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Trạng thái">
                <Tag color={getStatusMeta(selectedAppointment.status).color}>
                  {getStatusMeta(selectedAppointment.status).label}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Tiền cọc">
                {formatCurrency(selectedAppointment.depositAmount)}
              </Descriptions.Item>
              <Descriptions.Item label="Lý do khám">
                <Paragraph style={{ margin: 0 }}>
                  {selectedAppointment.reason || '-'}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="Ghi chú">
                <Paragraph style={{ margin: 0 }}>
                  {selectedAppointment.note || '-'}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="Hồ sơ bệnh án">
                {selectedAppointment.medicalRecordID ? (
                  <Button
                    type="link"
                    style={{ padding: 0 }}
                    loading={medicalRecordLoading}
                    onClick={handleViewMedicalRecord}
                  >
                    Xem hồ sơ bệnh án
                  </Button>
                ) : (
                  <Text type="secondary">Chưa có hồ sơ bệnh án</Text>
                )}
              </Descriptions.Item>
              <Descriptions.Item label="Khám bệnh">
                {canOpenExamination(selectedAppointment) ? (
                  <Button
                    type="primary"
                    onClick={() => openExaminationWorkspace(selectedAppointment.id)}
                  >
                    {getExaminationActionLabel(selectedAppointment)}
                  </Button>
                ) : (
                  <Text type="secondary">Lịch khám đã hủy, không thể thao tác</Text>
                )}
              </Descriptions.Item>
              <Descriptions.Item label="Ngày tạo lịch hẹn">
                {formatDateTime(selectedAppointment.createdAt)}
              </Descriptions.Item>
            </Descriptions>

            {medicalRecordVisible ? (
              <div className="doctor-schedule__record-reveal">
                <Card
                  title="Hồ sơ bệnh án"
                  loading={medicalRecordLoading}
                  className="doctor-schedule__record-card"
                >
                  {medicalRecord ? (
                    <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
                      <Descriptions bordered column={1} size="small">
                        <Descriptions.Item label="Bệnh nhân">
                          {medicalRecord.patientName || '-'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Triệu chứng">
                          <Paragraph style={{ margin: 0 }}>
                            {medicalRecord.symptoms || '-'}
                          </Paragraph>
                        </Descriptions.Item>
                        <Descriptions.Item label="Chẩn đoán">
                          <Paragraph style={{ margin: 0 }}>
                            {medicalRecord.diagnosis || '-'}
                          </Paragraph>
                        </Descriptions.Item>
                        <Descriptions.Item label="Kết luận">
                          <Paragraph style={{ margin: 0 }}>
                            {medicalRecord.conclusion || '-'}
                          </Paragraph>
                        </Descriptions.Item>
                        <Descriptions.Item label="Ngày khám">
                          {formatDateTime(medicalRecord.visitedAt)}
                        </Descriptions.Item>
                      </Descriptions>

                      <Card
                        size="small"
                        title="Đơn thuốc"
                        className="doctor-schedule__record-subcard"
                      >
                        {medicalRecord.prescriptionId ? (
                          <Button type="primary" onClick={handleViewPrescription}>
                            Xem đơn thuốc
                          </Button>
                        ) : (
                          <Text type="secondary">Chưa có đơn thuốc</Text>
                        )}
                      </Card>

                      <Card
                        size="small"
                        title="Kết quả xét nghiệm"
                        className="doctor-schedule__record-subcard"
                      >
                        {medicalRecord.labTestOrderIds?.length ? (
                          <Space wrap>
                            {medicalRecord.labTestOrderIds.map((labTestId, index) => (
                              <Button
                                key={labTestId}
                                onClick={() => handleViewLabTest(labTestId)}
                              >
                                {`Xem xét nghiệm ${index + 1}`}
                              </Button>
                            ))}
                          </Space>
                        ) : (
                          <Text type="secondary">Chưa có kết quả xét nghiệm</Text>
                        )}
                      </Card>
                    </Space>
                  ) : (
                    <Empty description="Không có dữ liệu hồ sơ bệnh án" />
                  )}
                </Card>
              </div>
            ) : null}
          </Space>
        ) : (
          <Empty description="Không có dữ liệu chi tiết" />
        )}
      </Drawer>

      <Modal
        title="Đơn thuốc"
        open={prescriptionModalOpen}
        onCancel={() => {
          setPrescriptionModalOpen(false)
          setSelectedPrescription(null)
        }}
        footer={null}
        width={720}
      >
        {prescriptionLoading ? (
          <Card loading />
        ) : selectedPrescription ? (
          <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
            <Descriptions bordered column={1} size="small">
              <Descriptions.Item label="Bệnh nhân">
                {selectedPrescription.patientName || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Bác sĩ">
                {selectedPrescription.doctorName || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Hướng dẫn sử dụng">
                <Paragraph style={{ margin: 0 }}>
                  {selectedPrescription.instructions || '-'}
                </Paragraph>
              </Descriptions.Item>
            </Descriptions>

            <Table
              rowKey={(record, index) => `${record.medicineName}-${index}`}
              pagination={false}
              size="small"
              dataSource={selectedPrescription.items || []}
              locale={{ emptyText: 'Chưa có thuốc trong đơn' }}
              columns={[
                {
                  title: 'Tên thuốc',
                  dataIndex: 'medicineName',
                  key: 'medicineName',
                },
                {
                  title: 'Số lượng',
                  dataIndex: 'quantity',
                  key: 'quantity',
                  width: 90,
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
                  width: 140,
                  render: (value) => value || '-',
                },
                {
                  title: 'Số ngày',
                  dataIndex: 'durationDays',
                  key: 'durationDays',
                  width: 100,
                  render: (value) => value ?? '-',
                },
                {
                  title: 'Ghi chú',
                  dataIndex: 'note',
                  key: 'note',
                  render: (value) => value || '-',
                },
              ]}
            />
          </Space>
        ) : (
          <Empty description="Không có dữ liệu đơn thuốc" />
        )}
      </Modal>

      <Modal
        title="Kết quả xét nghiệm"
        open={labTestModalOpen}
        onCancel={() => {
          setLabTestModalOpen(false)
          setSelectedLabTest(null)
        }}
        footer={null}
        width={680}
      >
        {labTestLoading ? (
          <Card loading />
        ) : selectedLabTest ? (
          <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
            <Descriptions bordered column={1} size="small">
              <Descriptions.Item label="Tên xét nghiệm">
                {selectedLabTest.testName || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Trạng thái">
                <Tag color={getLabStatusMeta(selectedLabTest.status).color}>
                  {getLabStatusMeta(selectedLabTest.status).label}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Ghi chú chỉ định">
                <Paragraph style={{ margin: 0 }}>
                  {selectedLabTest.requestNote || '-'}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="Kết quả">
                <Paragraph style={{ margin: 0 }}>
                  {selectedLabTest.result?.resultValue || '-'}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="Normal Range">
                {selectedLabTest.result?.normalRange || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Tệp đính kèm">
                {selectedLabTest.result?.attachmentUrl || '-'}
              </Descriptions.Item>
            </Descriptions>
          </Space>
        ) : (
          <Empty description="Không có dữ liệu kết quả xét nghiệm" />
        )}
      </Modal>
    </Space>
  )
}