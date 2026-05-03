import {
  ArrowLeftOutlined,
  ExperimentOutlined,
  FileTextOutlined,
  HistoryOutlined,
  MedicineBoxOutlined,
} from '@ant-design/icons'
import {
  App,
  Button,
  Card,
  Col,
  Descriptions,
  Empty,
  Form,
  Input,
  InputNumber,
  Modal,
  Row,
  Select,
  Space,
  Spin,
  Table,
  Tag,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import 'dayjs/locale/vi'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ROUTES } from '../../../constants/routes.js'
import { getMedicines } from '../../../services/medicineService.js'
import {
  getMyAppointmentById,
} from '../../../services/doctor/appointmentService.js'
import {
  createLabResult,
  createLabTestOrder,
  getLabTestById,
} from '../../../services/doctor/labTestService.js'
import {
  createMedicalRecord,
  getMedicalRecordById,
} from '../../../services/doctor/medicalRecordService.js'
import {
  createPrescription,
  getPrescriptionById,
} from '../../../services/doctor/prescriptionService.js'
import { getErrorMessage } from '../../../utils/httpError.js'
import './DoctorExaminationPage.css'

dayjs.locale('vi')

const { Title, Text, Paragraph } = Typography
const { TextArea } = Input

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

function getAppointmentStatusMeta(status) {
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

function getLabStatusMeta(status) {
  switch (status) {
    case 'COMPLETED':
      return { color: 'success', label: 'Đã có kết quả' }
    case 'REQUESTED':
      return { color: 'processing', label: 'Đã chỉ định' }
    case 'PENDING':
      return { color: 'warning', label: 'Đang chờ' }
    case 'CANCELLED':
      return { color: 'default', label: 'Đã hủy' }
    default:
      return { color: 'default', label: status || '-' }
  }
}

function buildMedicineOptions(items) {
  return items.map((item) => ({
    label: `${item.name} - ${item.unitName}`,
    value: item.id,
  }))
}

export default function DoctorExaminationPage() {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const { appointmentId } = useParams()
  const [medicalRecordForm] = Form.useForm()
  const [prescriptionForm] = Form.useForm()
  const [labOrderForm] = Form.useForm()
  const [labResultForm] = Form.useForm()
  const [loading, setLoading] = useState(true)
  const [recordSaving, setRecordSaving] = useState(false)
  const [prescriptionSaving, setPrescriptionSaving] = useState(false)
  const [labOrderSaving, setLabOrderSaving] = useState(false)
  const [labResultSaving, setLabResultSaving] = useState(false)
  const [medicineLoading, setMedicineLoading] = useState(false)
  const [appointment, setAppointment] = useState(null)
  const [medicalRecord, setMedicalRecord] = useState(null)
  const [prescription, setPrescription] = useState(null)
  const [labTests, setLabTests] = useState([])
  const [medicineOptions, setMedicineOptions] = useState([])
  const [labResultModalOpen, setLabResultModalOpen] = useState(false)
  const [activeLabTest, setActiveLabTest] = useState(null)

  async function loadMedicineOptions(keyword = '') {
    setMedicineLoading(true)
    try {
      const response = await getMedicines({
        page: 0,
        size: 10,
        name: keyword || undefined,
      })
      const items = response?.result?.content || []
      setMedicineOptions(buildMedicineOptions(items))
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setMedicineLoading(false)
    }
  }

  async function hydrateMedicalRecord(medicalRecordId) {
    const medicalRecordResponse = await getMedicalRecordById(medicalRecordId)
    const medicalRecordData = medicalRecordResponse?.result || null
    setMedicalRecord(medicalRecordData)

    if (medicalRecordData?.prescriptionId) {
      const prescriptionResponse = await getPrescriptionById(medicalRecordData.prescriptionId)
      setPrescription(prescriptionResponse?.result || null)
    } else {
      setPrescription(null)
      prescriptionForm.setFieldsValue({
        instructions: undefined,
        items: [{ quantity: 1, durationDays: 1 }],
      })
    }

    if (medicalRecordData?.labTestOrderIds?.length) {
      const responses = await Promise.all(
        medicalRecordData.labTestOrderIds.map((labTestId) => getLabTestById(labTestId)),
      )
      setLabTests(responses.map((response) => response?.result).filter(Boolean))
    } else {
      setLabTests([])
    }
  }

  async function loadExaminationData() {
    setLoading(true)
    try {
      const appointmentResponse = await getMyAppointmentById(appointmentId)
      const appointmentData = appointmentResponse?.result || null
      setAppointment(appointmentData)

      if (appointmentData?.medicalRecordID) {
        await hydrateMedicalRecord(appointmentData.medicalRecordID)
      } else {
        setMedicalRecord(null)
        setPrescription(null)
        setLabTests([])
        medicalRecordForm.resetFields()
        prescriptionForm.setFieldsValue({
          instructions: undefined,
          items: [{ quantity: 1, durationDays: 1 }],
        })
      }
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!appointmentId) return
    loadExaminationData()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [appointmentId])

  async function handleCreateMedicalRecord() {
    try {
      const values = await medicalRecordForm.validateFields()
      setRecordSaving(true)
      const response = await createMedicalRecord({
        appointmentId,
        symptoms: values.symptoms.trim(),
        diagnosis: values.diagnosis.trim(),
        conclusion: values.conclusion?.trim() || undefined,
      })
      message.success('Tạo bệnh án thành công')
      medicalRecordForm.resetFields()
      await hydrateMedicalRecord(response?.result?.id)
      await loadExaminationData()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setRecordSaving(false)
    }
  }

  async function handleCreatePrescription() {
    if (!medicalRecord) return
    try {
      const values = await prescriptionForm.validateFields()
      setPrescriptionSaving(true)
      const payload = {
        medicalRecordId: medicalRecord.id,
        instructions: values.instructions?.trim() || undefined,
        items: (values.items || []).map((item) => ({
          medicineId: item.medicineId,
          quantity: item.quantity,
          dosage: item.dosage.trim(),
          frequency: item.frequency.trim(),
          durationDays: item.durationDays,
          note: item.note?.trim() || '',
        })),
      }
      const response = await createPrescription(payload)
      setPrescription(response?.result || null)
      message.success('Tạo đơn thuốc thành công')
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setPrescriptionSaving(false)
    }
  }

  async function handleCreateLabOrder() {
    if (!medicalRecord) return
    try {
      const values = await labOrderForm.validateFields()
      setLabOrderSaving(true)
      const response = await createLabTestOrder({
        medicalRecordId: medicalRecord.id,
        testName: values.testName.trim(),
        requestNote: values.requestNote?.trim() || undefined,
      })
      const createdLabTestId = response?.result?.id
      const detailResponse = await getLabTestById(createdLabTestId)
      setLabTests((prev) => [detailResponse?.result, ...prev].filter(Boolean))
      labOrderForm.resetFields()
      message.success('Tạo yêu cầu xét nghiệm thành công')
      await loadExaminationData()
    } catch (error) {
      if (error?.errorFields) return
    
    // Check nếu BE trả về lỗi về prescription đã tồn tại
    const errorMsg = error?.response?.data?.message || error?.message || ''
    if (errorMsg.toLowerCase().includes('prescription') || errorMsg.toLowerCase().includes('already exists')) {
      message.error('Không thể tạo yêu cầu xét nghiệm')
    } else {
      message.error(getErrorMessage(error))
    }
    } finally {
      setLabOrderSaving(false)
    }
  }

  function openLabResultModal(labTest) {
    setActiveLabTest(labTest)
    labResultForm.setFieldsValue({
      resultValue: labTest?.result?.resultValue,
      normalRange: labTest?.result?.normalRange,
      attachmentUrl: labTest?.result?.attachmentUrl,
    })
    setLabResultModalOpen(true)
  }

  async function handleCreateLabResult() {
    if (!activeLabTest) return
    try {
      const values = await labResultForm.validateFields()
      setLabResultSaving(true)
      await createLabResult({
        labTestOrderId: activeLabTest.id,
        resultValue: values.resultValue.trim(),
        normalRange: values.normalRange?.trim() || undefined,
        attachmentUrl: values.attachmentUrl?.trim() || undefined,
      })
      const detailResponse = await getLabTestById(activeLabTest.id)
      setLabTests((prev) => prev.map((item) => (
        item.id === activeLabTest.id ? detailResponse?.result || item : item
      )))
      setLabResultModalOpen(false)
      setActiveLabTest(null)
      labResultForm.resetFields()
      message.success('Tạo kết quả xét nghiệm thành công')
      await loadExaminationData()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setLabResultSaving(false)
    }
  }

  const completedLabResults = labTests.filter((item) => item?.result).length
  const patientHistoryParams = new URLSearchParams()
  const patientId = appointment?.patient?.patientId

  if (patientId) {
    patientHistoryParams.set('patientId', patientId)
  }

  const patientHistoryRoute = patientHistoryParams.size
    ? `${ROUTES.doctorPatientHistory}?${patientHistoryParams.toString()}`
    : ROUTES.doctorPatientHistory

  const progressItems = [
    {
      key: 'medical-record',
      title: 'Bệnh án',
      tag: medicalRecord ? <Tag color="success">Hoàn tất</Tag> : <Tag color="processing">Cần thực hiện</Tag>,
    },
    {
      key: 'prescription',
      title: 'Đơn thuốc',
      tag: prescription ? <Tag color="success">Đã tạo</Tag> : <Tag>Chưa tạo</Tag>,
    },
    {
      key: 'lab-order',
      title: 'Yêu cầu xét nghiệm',
      tag: labTests.length ? <Tag color="processing">{`${labTests.length} yêu cầu`}</Tag> : <Tag>Chưa có</Tag>,
    },
    {
      key: 'lab-result',
      title: 'Kết quả xét nghiệm',
      tag: completedLabResults
        ? <Tag color="success">{`${completedLabResults} kết quả`}</Tag>
        : <Tag>Chưa có</Tag>,
    },
  ]

  if (loading) {
    return (
      <Card>
        <Spin />
      </Card>
    )
  }

  if (!appointment) {
    return (
      <Card>
        <Empty description="Không tìm thấy lịch khám" />
      </Card>
    )
  }

  const appointmentStatus = getAppointmentStatusMeta(appointment.status)

  return (
    <div className="doctor-examination">
      <Space orientation="vertical" size="middle" className="doctor-examination__stack">
        <Card className="doctor-examination__hero">
          <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
            <div className="doctor-examination__hero-row">
              <div className="doctor-examination__hero-text">
                <Button
                  type="link"
                  icon={<ArrowLeftOutlined />}
                  style={{ paddingInline: 0, width: 'fit-content' }}
                  onClick={() => navigate(ROUTES.doctorSchedule)}
                >
                  Quay lại lịch khám
                </Button>
                <Title level={3} style={{ margin: 0 }}>
                  Khám bệnh cho {appointment.patient?.fullName || 'bệnh nhân'}
                </Title>
                <Button
                  type="link"
                  icon={<HistoryOutlined />}
                  style={{ paddingInline: 0, width: 'fit-content' }}
                  onClick={() => navigate(patientHistoryRoute, {
                    state: {
                      patient: {
                        patientId,
                        fullName: appointment?.patient?.fullName,
                        email: appointment?.patient?.email,
                      },
                    },
                  })}
                >
                  Tra cứu lịch sử khám bệnh
                </Button>
              </div>

              <div className="doctor-examination__hero-tags">
                <Tag color={appointmentStatus.color}>{appointmentStatus.label}</Tag>
                <Tag color={medicalRecord ? 'success' : 'processing'}>
                  {medicalRecord ? 'Đã có bệnh án' : 'Chưa có bệnh án'}
                </Tag>
                <Tag color={prescription ? 'success' : 'default'}>
                  {prescription ? 'Đã có đơn thuốc' : 'Chưa có đơn thuốc'}
                </Tag>
              </div>
            </div>

            <Descriptions bordered column={{ xs: 1, md: 2 }} size="small" className="doctor-examination__summary-grid">
              <Descriptions.Item label="Ngày khám">
                {formatDate(appointment.appointmentDate)}
              </Descriptions.Item>
              <Descriptions.Item label="Khung giờ">
                {appointment.timeSlot || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Lý do khám">
                <Paragraph style={{ margin: 0 }}>
                  {appointment.reason || '-'}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="Ghi chú bệnh nhân">
                <Paragraph style={{ margin: 0 }}>
                  {appointment.note || '-'}
                </Paragraph>
              </Descriptions.Item>
            </Descriptions>
          </Space>
        </Card>

        <Row gutter={[16, 16]}>
          <Col xs={24} lg={8}>
            <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
              <Card title="Tiến trình khám" className="doctor-examination__progress-card">
                <div className="doctor-examination__progress-list">
                  {progressItems.map((item) => (
                    <div key={item.key} className="doctor-examination__progress-item">
                      <strong>{item.title}</strong>
                      {item.tag}
                    </div>
                  ))}
                </div>
              </Card>

              {medicalRecord ? (
                <Card title="Tóm tắt bệnh án" className="doctor-examination__summary-card">
                  <Descriptions column={1} size="small">
                    <Descriptions.Item label="Chẩn đoán">
                      {medicalRecord.diagnosis || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="Kết luận">
                      {medicalRecord.conclusion || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="Ngày khám">
                      {formatDateTime(medicalRecord.visitedAt)}
                    </Descriptions.Item>
                  </Descriptions>
                </Card>
              ) : null}
            </Space>
          </Col>

          <Col xs={24} lg={16}>
            <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
              <Card className="doctor-examination__section">
                <div className="doctor-examination__section-header">
                  <Space>
                    <FileTextOutlined />
                    <Title level={4} style={{ margin: 0 }}>
                      Hồ Sơ Bệnh án
                    </Title>
                  </Space>
                </div>

                {medicalRecord ? (
                  <Descriptions bordered column={1} size="small" style={{ marginTop: 16 }}>
                    <Descriptions.Item label="Triệu chứng">
                      <Paragraph style={{ margin: 0 }}>{medicalRecord.symptoms || '-'}</Paragraph>
                    </Descriptions.Item>
                    <Descriptions.Item label="Chẩn đoán">
                      <Paragraph style={{ margin: 0 }}>{medicalRecord.diagnosis || '-'}</Paragraph>
                    </Descriptions.Item>
                    <Descriptions.Item label="Kết luận">
                      <Paragraph style={{ margin: 0 }}>{medicalRecord.conclusion || '-'}</Paragraph>
                    </Descriptions.Item>
                    <Descriptions.Item label="Thời gian tạo">
                      {formatDateTime(medicalRecord.visitedAt)}
                    </Descriptions.Item>
                  </Descriptions>
                ) : (
                  <Form
                    form={medicalRecordForm}
                    layout="vertical"
                    style={{ marginTop: 16 }}
                  >
                    <Form.Item
                      label="Triệu chứng"
                      name="symptoms"
                      rules={[{ required: true, message: 'Vui lòng nhập triệu chứng' }]}
                    >
                      <TextArea rows={4} placeholder="Mô tả các triệu chứng ghi nhận được" />
                    </Form.Item>
                    <Form.Item
                      label="Chẩn đoán"
                      name="diagnosis"
                      rules={[{ required: true, message: 'Vui lòng nhập chẩn đoán' }]}
                    >
                      <TextArea rows={3} placeholder="Nhập chẩn đoán của bác sĩ" />
                    </Form.Item>
                    <Form.Item label="Kết luận" name="conclusion">
                      <TextArea rows={3} placeholder="Kết luận sau khám" />
                    </Form.Item>
                    <Button type="primary" loading={recordSaving} onClick={handleCreateMedicalRecord}>
                      Tạo bệnh án
                    </Button>
                  </Form>
                )}
              </Card>


              <Card className="doctor-examination__section">
                <div className="doctor-examination__section-header">
                  <Space>
                    <ExperimentOutlined />
                    <Title level={4} style={{ margin: 0 }}>
                      Xét nghiệm và kết quả
                    </Title>
                  </Space>
                </div>

                {!medicalRecord ? (
                  <Empty style={{ marginTop: 16 }} description="Tạo bệnh án trước để chỉ định xét nghiệm" />
                ) : (
                  <Space orientation="vertical" size="middle" style={{ width: '100%', marginTop: 16 }}>
                    <Form form={labOrderForm} layout="vertical">
                      <Row gutter={12}>
                        <Col xs={24} md={10}>
                          <Form.Item
                            label="Tên xét nghiệm"
                            name="testName"
                            rules={[{ required: true, message: 'Vui lòng nhập tên xét nghiệm' }]}
                          >
                            <Input placeholder="Ví dụ: Xét nghiệm chức năng thận" />
                          </Form.Item>
                        </Col>
                        <Col xs={24} md={14}>
                          <Form.Item label="Ghi chú chỉ định" name="requestNote">
                            <Input placeholder="Ví dụ: Xét nghiệm máu" />
                          </Form.Item>
                        </Col>
                      </Row>
                      <Button type="primary" loading={labOrderSaving} onClick={handleCreateLabOrder}>
                        Thêm yêu cầu xét nghiệm
                      </Button>
                    </Form>

                    {labTests.length ? (
                      <Space orientation="vertical" size="small" style={{ width: '100%' }}>
                        {labTests.map((labTest) => {
                          const statusMeta = getLabStatusMeta(labTest.status)
                          return (
                            <Card key={labTest.id} size="small" className="doctor-examination__lab-item">
                              <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
                                <div className="doctor-examination__section-header">
                                  <div>
                                    <Text strong>{labTest.testName || '-'}</Text>
                                    <br />
                                    <Text type="secondary">
                                      {labTest.requestNote || 'Không có ghi chú chỉ định'}
                                    </Text>
                                  </div>
                                  <Space wrap>
                                    <Tag color={statusMeta.color}>{statusMeta.label}</Tag>
                                    {labTest.result ? (
                                      <Button onClick={() => openLabResultModal(labTest)}>
                                        Xem kết quả
                                      </Button>
                                    ) : (
                                      <Button type="primary" onClick={() => openLabResultModal(labTest)}>
                                        Nhập kết quả
                                      </Button>
                                    )}
                                  </Space>
                                </div>

                                {labTest.result ? (
                                  <div className="doctor-examination__result-block">
                                    <Descriptions size="small" column={1}>
                                      <Descriptions.Item label="Kết quả">
                                        {labTest.result.resultValue || '-'}
                                      </Descriptions.Item>
                                      <Descriptions.Item label="Normal Range">
                                        {labTest.result.normalRange || '-'}
                                      </Descriptions.Item>
                                      <Descriptions.Item label="Tệp đính kèm">
                                        {labTest.result.attachmentUrl || '-'}
                                      </Descriptions.Item>
                                    </Descriptions>
                                  </div>
                                ) : (
                                  <Text type="secondary">Chưa có kết quả xét nghiệm.</Text>
                                )}
                              </Space>
                            </Card>
                          )
                        })}
                      </Space>
                    ) : (
                      <Empty description="Chưa có yêu cầu xét nghiệm nào" />
                    )}
                  </Space>
                )}
              </Card>

              <Card className="doctor-examination__section">
                <div className="doctor-examination__section-header">
                  <Space>
                    <MedicineBoxOutlined />
                    <Title level={4} style={{ margin: 0 }}>
                      Đơn thuốc
                    </Title>
                  </Space>
                </div>

                {!medicalRecord ? (
                  <Empty style={{ marginTop: 16 }} description="Tạo bệnh án trước để kê đơn" />
                ) : prescription ? (
                  <Space orientation="vertical" size="middle" style={{ width: '100%', marginTop: 16 }}>
                    <Descriptions bordered column={1} size="small">
                      <Descriptions.Item label="Bệnh nhân">
                        {prescription.patientName || appointment.patient?.fullName || '-'}
                      </Descriptions.Item>
                      <Descriptions.Item label="Bác sĩ">
                        {prescription.doctorName || '-'}
                      </Descriptions.Item>
                      <Descriptions.Item label="Hướng dẫn sử dụng">
                        <Paragraph style={{ margin: 0 }}>{prescription.instructions || '-'}</Paragraph>
                      </Descriptions.Item>
                    </Descriptions>

                    <Table
                      rowKey={(record, index) => `${record.medicineName}-${index}`}
                      pagination={false}
                      size="small"
                      dataSource={prescription.items || []}
                      locale={{ emptyText: 'Chưa có thuốc trong đơn' }}
                      columns={[
                        { title: 'Tên thuốc', dataIndex: 'medicineName', key: 'medicineName' },
                        { title: 'Số lượng', dataIndex: 'quantity', key: 'quantity', width: 90 },
                        { title: 'Liều dùng', dataIndex: 'dosage', key: 'dosage', width: 130, render: (value) => value || '-' },
                        { title: 'Tần suất', dataIndex: 'frequency', key: 'frequency', width: 150, render: (value) => value || '-' },
                        { title: 'Số ngày', dataIndex: 'durationDays', key: 'durationDays', width: 100, render: (value) => value ?? '-' },
                        { title: 'Ghi chú', dataIndex: 'note', key: 'note', render: (value) => value || '-' },
                      ]}
                    />
                  </Space>
                ) : (
                  <Form
                    form={prescriptionForm}
                    layout="vertical"
                    initialValues={{ items: [{ quantity: 1, durationDays: 1 }] }}
                    style={{ marginTop: 16 }}
                  >
                    <Form.Item label="Hướng dẫn sử dụng chung" name="instructions">
                      <TextArea rows={3} placeholder="Ví dụ: Uống sau ăn, tái khám sau 5 ngày" />
                    </Form.Item>

                    <Form.List
                      name="items"
                      rules={[
                        {
                          validator: async (_, value) => {
                            if (value?.length) return
                            throw new Error('Vui lòng thêm ít nhất một thuốc')
                          },
                        },
                      ]}
                    >
                      {(fields, { add, remove }, { errors }) => (
                        <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
                          {fields.map((field, index) => {
                            const { key, ...restField } = field
                            return (
                            <div key={key} className="doctor-examination__medication-row">
                              <Row gutter={12}>
                                <Col xs={24} md={24}>
                                  <Form.Item
                                    {...restField}
                                    label={`Thuốc ${index + 1}`}
                                    name={[field.name, 'medicineId']}
                                    rules={[{ required: true, message: 'Vui lòng chọn thuốc' }]}
                                  >
                                    <Select
                                      showSearch
                                      placeholder="Tìm thuốc theo tên"
                                      filterOption={false}
                                      options={medicineOptions}
                                      loading={medicineLoading}
                                      onSearch={loadMedicineOptions}
                                      onFocus={() => {
                                        if (medicineOptions.length === 0) {
                                          loadMedicineOptions('')
                                        }
                                      }}
                                      notFoundContent={medicineLoading ? 'Đang tải thuốc...' : 'Không tìm thấy thuốc'}
                                    />
                                  </Form.Item>
                                </Col>
                                <Col xs={24} md={8}>
                                  <Form.Item
                                    {...restField}
                                    label="Số lượng"
                                    name={[field.name, 'quantity']}
                                    rules={[{ required: true, message: 'Nhập số lượng' }]}
                                  >
                                    <InputNumber min={1} style={{ width: '100%' }} />
                                  </Form.Item>
                                </Col>
                                <Col xs={24} md={8}>
                                  <Form.Item
                                    {...restField}
                                    label="Liều dùng"
                                    name={[field.name, 'dosage']}
                                    rules={[{ required: true, message: 'Nhập liều dùng' }]}
                                  >
                                    <Input placeholder="Ví dụ: 1 viên" />
                                  </Form.Item>
                                </Col>
                                <Col xs={24} md={8}>
                                  <Form.Item
                                    {...restField}
                                    label="Tần suất"
                                    name={[field.name, 'frequency']}
                                    rules={[{ required: true, message: 'Nhập tần suất' }]}
                                  >
                                    <Input placeholder="Ví dụ: 2 lần/ngày" />
                                  </Form.Item>
                                </Col>
                                <Col xs={24} md={8}>
                                  <Form.Item
                                    {...restField}
                                    label="Số ngày"
                                    name={[field.name, 'durationDays']}
                                    rules={[{ required: true, message: 'Nhập số ngày' }]}
                                  >
                                    <InputNumber min={1} style={{ width: '100%' }} />
                                  </Form.Item>
                                </Col>
                                <Col xs={24} md={16}>
                                  <Form.Item
                                    {...restField}
                                    label="Ghi chú"
                                    name={[field.name, 'note']}
                                  >
                                    <Input placeholder="Nhập lưu ý nếu có" />
                                  </Form.Item>
                                </Col>
                              </Row>
                              {fields.length > 1 ? (
                                <Button danger type="link" style={{ paddingInline: 0 }} onClick={() => remove(field.name)}>
                                  Xóa thuốc này
                                </Button>
                              ) : null}
                            </div>
                            )
                          })}

                          <Form.ErrorList errors={errors} />

                          <Space wrap>
                            <Button onClick={() => add({ quantity: 1, durationDays: 1 })}>
                              Thêm thuốc
                            </Button>
                            <Button type="primary" loading={prescriptionSaving} onClick={handleCreatePrescription}>
                              Tạo đơn thuốc
                            </Button>
                          </Space>
                        </Space>
                      )}
                    </Form.List>
                  </Form>
                )}
              </Card>
            </Space>
          </Col>
        </Row>
      </Space>

      <Modal
        title={activeLabTest?.result ? 'Kết quả xét nghiệm' : 'Nhập kết quả xét nghiệm'}
        open={labResultModalOpen}
        forceRender
        onCancel={() => {
          setLabResultModalOpen(false)
          setActiveLabTest(null)
          labResultForm.resetFields()
        }}
        onOk={activeLabTest?.result ? undefined : handleCreateLabResult}
        okText="Lưu kết quả"
        cancelText={activeLabTest?.result ? 'Đóng' : 'Hủy'}
        confirmLoading={labResultSaving}
        okButtonProps={{ disabled: !!activeLabTest?.result }}
      >
        <Form form={labResultForm} layout="vertical">
          <Form.Item
            label="Kết quả"
            name="resultValue"
            rules={[{ required: true, message: 'Vui lòng nhập kết quả xét nghiệm' }]}
          >
            <TextArea rows={4} placeholder="Nhập giá trị kết quả" disabled={!!activeLabTest?.result} />
          </Form.Item>
          <Form.Item label="Normal Range" name="normalRange">
            <Input placeholder="Nhập khoảng giá trị bình thường" disabled={!!activeLabTest?.result} />
          </Form.Item>
          <Form.Item label="Tệp đính kèm" name="attachmentUrl">
            <Input placeholder="Nhập URL tệp kết quả nếu có" disabled={!!activeLabTest?.result} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}