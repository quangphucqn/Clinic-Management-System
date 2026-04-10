import {
  CalendarOutlined,
  ClusterOutlined,
  ClockCircleOutlined,
  UserOutlined,
} from '@ant-design/icons'
import {
  Alert,
  App,
  Button,
  Card,
  Col,
  DatePicker,
  Descriptions,
  Form,
  Input,
  Row,
  Select,
  Space,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useEffect, useMemo, useState } from 'react'
import { bookAppointment } from '../../../services/appointmentPatientService.js'
import { getDoctors } from '../../../services/doctorService.js'
import { getSpecialties } from '../../../services/specialtyService.js'
import { getTimeSlots } from '../../../services/timeSlotService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input

function toTimeLabel(slot) {
  if (!slot?.startTime || !slot?.endTime) return slot?.slotCode || 'Khung giờ'
  return `${slot.startTime.slice(0, 5)} - ${slot.endTime.slice(0, 5)} (${slot.slotCode || 'N/A'})`
}

export default function AppointmentBookingPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loadingOptions, setLoadingOptions] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [specialties, setSpecialties] = useState([])
  const [doctors, setDoctors] = useState([])
  const [timeSlots, setTimeSlots] = useState([])
  const [bookingResult, setBookingResult] = useState(null)

  useEffect(() => {
    let active = true

    async function loadOptions() {
      setLoadingOptions(true)
      try {
        const [specialtyResponse, doctorResponse, slotResponse] = await Promise.all([
          getSpecialties({ page: 0, size: 200 }),
          getDoctors({ page: 0, size: 200 }),
          getTimeSlots({ page: 0, size: 200 }),
        ])

        if (!active) return

        setSpecialties(specialtyResponse?.result?.content || [])
        setDoctors(doctorResponse?.result?.content || [])
        setTimeSlots(slotResponse?.result?.content || [])
      } catch (error) {
        if (!active) return
        message.error(getErrorMessage(error))
      } finally {
        if (active) setLoadingOptions(false)
      }
    }

    loadOptions()

    return () => {
      active = false
    }
  }, [message])

  const selectedSpecialtyId = Form.useWatch('specialtyId', form)
  const selectedDoctorId = Form.useWatch('doctorId', form)
  const selectedDate = Form.useWatch('appointmentDate', form)

  const specialtyOptions = useMemo(() => {
    return specialties
      .filter((specialty) => specialty?.id && specialty?.name)
      .map((specialty) => ({ value: specialty.id, label: specialty.name }))
      .sort((a, b) => a.label.localeCompare(b.label))
  }, [specialties])

  const doctorOptions = useMemo(() => {
    const filtered = selectedSpecialtyId
      ? doctors.filter((doctor) => doctor.specialtyId === selectedSpecialtyId)
      : []

    return filtered.map((doctor) => ({
      value: doctor.id,
      label: `${doctor.fullName}${doctor.specialtyName ? ` - ${doctor.specialtyName}` : ''}`,
    }))
  }, [doctors, selectedSpecialtyId])

  const slotOptions = useMemo(() => {
    if (!selectedDoctorId || !selectedDate) return []
    return timeSlots
      .filter((slot) => slot.enabled !== false)
      .map((slot) => ({ value: slot.id, label: toTimeLabel(slot) }))
  }, [selectedDate, selectedDoctorId, timeSlots])

  const selectedDoctorLabel = useMemo(
    () => doctorOptions.find((item) => item.value === form.getFieldValue('doctorId'))?.label,
    [doctorOptions, form],
  )
  const selectedSlotLabel = useMemo(
    () => slotOptions.find((item) => item.value === form.getFieldValue('timeSlotId'))?.label,
    [form, slotOptions],
  )

  function handleValuesChange(changedValues) {
    if ('specialtyId' in changedValues) {
      form.setFieldsValue({ doctorId: undefined, timeSlotId: undefined })
    }
    if ('doctorId' in changedValues || 'appointmentDate' in changedValues) {
      form.setFieldsValue({ timeSlotId: undefined })
    }
  }

  async function handleSubmit() {
    try {
      const values = await form.validateFields()
      setSubmitting(true)

      const payload = {
        doctorId: values.doctorId,
        timeSlotId: values.timeSlotId,
        appointmentDate: values.appointmentDate.format('YYYY-MM-DD'),
        reason: values.reason.trim(),
        note: values.note?.trim() || undefined,
      }

      const response = await bookAppointment(payload)
      setBookingResult(response?.result || null)
      message.success('Đặt lịch khám thành công')
      form.resetFields(['reason', 'note'])
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Space direction="vertical" size={4}>
          <Title level={3} style={{ margin: 0 }}>
            Đặt khám
          </Title>
          <Paragraph type="secondary" style={{ marginBottom: 0 }}>
            Chọn khoa, bác sĩ, ngày khám và khung giờ để gửi yêu cầu đặt lịch.
          </Paragraph>
        </Space>
      </Card>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={16}>
          <Card loading={loadingOptions}>
            <Form form={form} layout="vertical" onValuesChange={handleValuesChange}>
              <Form.Item
                label="Khoa"
                name="specialtyId"
                rules={[{ required: true, message: 'Vui lòng chọn khoa' }]}
              >
                <Select
                  placeholder="Chọn khoa"
                  options={specialtyOptions}
                  showSearch
                  optionFilterProp="label"
                  prefix={<ClusterOutlined />}
                />
              </Form.Item>

              <Form.Item
                label="Danh sách bác sĩ"
                name="doctorId"
                rules={[{ required: true, message: 'Vui lòng chọn bác sĩ' }]}
              >
                <Select
                  placeholder="Chọn bác sĩ theo khoa"
                  options={doctorOptions}
                  showSearch
                  optionFilterProp="label"
                  disabled={!selectedSpecialtyId}
                  prefix={<UserOutlined />}
                />
              </Form.Item>

              <Form.Item
                label="Ngày khám"
                name="appointmentDate"
                rules={[{ required: true, message: 'Vui lòng chọn ngày khám' }]}
              >
                <DatePicker
                  style={{ width: '100%' }}
                  format="DD/MM/YYYY"
                  disabledDate={(current) =>
                    current ? current.isBefore(dayjs().startOf('day')) : false
                  }
                  suffixIcon={<CalendarOutlined />}
                />
              </Form.Item>

              <Form.Item
                label="Khung giờ khám của bác sĩ"
                name="timeSlotId"
                rules={[{ required: true, message: 'Vui lòng chọn khung giờ' }]}
              >
                <Select
                  placeholder="Chọn khung giờ khám"
                  options={slotOptions}
                  showSearch
                  optionFilterProp="label"
                  disabled={!selectedDoctorId || !selectedDate}
                  prefix={<ClockCircleOutlined />}
                />
              </Form.Item>

              <Form.Item
                label="Lý do khám"
                name="reason"
                rules={[
                  { required: true, message: 'Vui lòng nhập lý do khám' },
                  { max: 500, message: 'Tối đa 500 ký tự' },
                ]}
              >
                <TextArea rows={4} placeholder="Mô tả triệu chứng hoặc nhu cầu khám" />
              </Form.Item>

              <Form.Item
                label="Ghi chú"
                name="note"
                rules={[{ max: 500, message: 'Tối đa 500 ký tự' }]}
              >
                <TextArea rows={3} placeholder="Thông tin bổ sung (không bắt buộc)" />
              </Form.Item>

              <Space>
                <Button type="primary" loading={submitting} onClick={handleSubmit}>
                  Xác nhận đặt lịch
                </Button>
                <Button onClick={() => form.resetFields()}>Làm mới</Button>
              </Space>
            </Form>
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card title="Thông tin đang chọn">
            <Space direction="vertical" size={8}>
              <Text>
                <ClusterOutlined /> Khoa:{' '}
                {specialtyOptions.find((item) => item.value === form.getFieldValue('specialtyId'))
                  ?.label || '-'}
              </Text>
              <Text>
                <UserOutlined /> Bác sĩ:{' '}
                {selectedDoctorLabel || '-'}
              </Text>
              <Text>
                <ClockCircleOutlined /> Khung giờ:{' '}
                {selectedSlotLabel || '-'}
              </Text>
              <Text>
                <CalendarOutlined /> Ngày khám:{' '}
                {form.getFieldValue('appointmentDate')
                  ? form.getFieldValue('appointmentDate').format('DD/MM/YYYY')
                  : '-'}
              </Text>
            </Space>
          </Card>

          {bookingResult ? (
            <Card style={{ marginTop: 16 }}>
              <Alert
                message="Đặt lịch thành công"
                description="Bạn có thể theo dõi trạng thái lịch hẹn trong mục Lịch khám."
                type="success"
                showIcon
                style={{ marginBottom: 16 }}
              />
              <Descriptions bordered size="small" column={1}>
                <Descriptions.Item label="Mã lịch hẹn">
                  {bookingResult.appointmentId}
                </Descriptions.Item>
                <Descriptions.Item label="Doctor ID">{bookingResult.doctorId}</Descriptions.Item>
                <Descriptions.Item label="Time Slot ID">
                  {bookingResult.timeSlotId}
                </Descriptions.Item>
                <Descriptions.Item label="Ngày khám">
                  {bookingResult.appointmentDate
                    ? dayjs(bookingResult.appointmentDate).format('DD/MM/YYYY')
                    : '-'}
                </Descriptions.Item>
                <Descriptions.Item label="Trạng thái">
                  {bookingResult.status || 'PENDING'}
                </Descriptions.Item>
              </Descriptions>
            </Card>
          ) : null}
        </Col>
      </Row>
    </Space>
  )
}

