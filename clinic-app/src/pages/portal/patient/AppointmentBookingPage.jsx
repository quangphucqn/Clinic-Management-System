import {
  CalendarOutlined,
  ClusterOutlined,
  ClockCircleOutlined,
  UserOutlined,
} from '@ant-design/icons'
import {
  App,
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  Row,
  Select,
  Space,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useEffect, useMemo, useState } from 'react'
import {
  bookAppointment,
  getBookedTimeSlotIds,
} from '../../../services/appointmentPatientService.js'
import { getAppointmentDepositConfig } from '../../../services/appointmentDepositConfigService.js'
import { getDoctors } from '../../../services/doctorService.js'
import { getSpecialties } from '../../../services/specialtyService.js'
import { getTimeSlots } from '../../../services/timeSlotService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph, Text } = Typography
const { TextArea } = Input
const PAYMENT_METHODS = {
  MOMO: 'MOMO',
}

function toTimeLabel(slot) {
  if (!slot?.startTime || !slot?.endTime) return slot?.slotCode || 'Khung giờ'
  return `${slot.startTime.slice(0, 5)} - ${slot.endTime.slice(0, 5)}`
}

function getSessionByStartTime(startTime) {
  if (!startTime) return 'Khác'
  const hour = Number(startTime.slice(0, 2))
  if (Number.isNaN(hour)) return 'Khác'
  return hour < 12 ? 'Buổi sáng' : 'Buổi chiều'
}

export default function AppointmentBookingPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loadingOptions, setLoadingOptions] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [specialties, setSpecialties] = useState([])
  const [doctors, setDoctors] = useState([])
  const [timeSlots, setTimeSlots] = useState([])
  const [slotAvailabilities, setSlotAvailabilities] = useState([])
  const [depositAmount, setDepositAmount] = useState(0)

  useEffect(() => {
    let active = true

    async function loadOptions() {
      setLoadingOptions(true)
      try {
        const [specialtyResponse, doctorResponse, slotResponse, depositConfigResponse] = await Promise.all([
          getSpecialties({ page: 0, size: 200 }),
          getDoctors({ page: 0, size: 200 }),
          getTimeSlots({ page: 0, size: 200 }),
          getAppointmentDepositConfig(),
        ])

        if (!active) return

        setSpecialties(specialtyResponse?.result?.content || [])
        setDoctors(doctorResponse?.result?.content || [])
        setTimeSlots(slotResponse?.result?.content || [])
        setDepositAmount(Number(depositConfigResponse?.result?.amount || 0))
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
  const selectedTimeSlotId = Form.useWatch('timeSlotId', form)

  const specialtyOptions = useMemo(() => {
    return specialties
      .filter((specialty) => specialty?.id && specialty?.name)
      .map((specialty) => ({ value: specialty.id, label: specialty.name }))
      .sort((a, b) => a.label.localeCompare(b.label))
  }, [specialties])

  const doctorOptions = useMemo(() => {
    const filtered = selectedSpecialtyId
      ? doctors.filter(
          (doctor) => doctor.specialtyId === selectedSpecialtyId || doctor.specialty?.id === selectedSpecialtyId,
        )
      : []

    return filtered.map((doctor) => ({
      value: doctor.id,
      label: `${doctor.fullName}${doctor.specialtyName ? ` - ${doctor.specialtyName}` : ''}`,
    }))
  }, [doctors, selectedSpecialtyId])

  const slotAvailabilityMap = useMemo(() => {
    const map = new Map()
    slotAvailabilities.forEach((item) => {
      if (item?.timeSlotId) map.set(item.timeSlotId, item)
    })
    return map
  }, [slotAvailabilities])

  const slotOptions = useMemo(() => {
    if (!selectedDoctorId || !selectedDate) return []
    const isToday = selectedDate.isSame(dayjs(), 'day')
    const currentTime = dayjs().format('HH:mm:ss')
    const mapped = timeSlots
      .filter((slot) => slot.enabled !== false)
      .filter((slot) => {
        if (!isToday) return true
        if (!slot?.startTime) return false
        return slot.startTime > currentTime
      })
      .map((slot) => {
        const availability = slotAvailabilityMap.get(slot.id)
        const bookedByCurrentPatient = Boolean(availability?.bookedByCurrentPatient)
        const slotFull = Boolean(availability?.slotFull)
        const isBooked = bookedByCurrentPatient || slotFull
        const labelSuffix = bookedByCurrentPatient
          ? ' - đã đặt lịch'
          : slotFull
            ? ' - đã hết slot'
            : ''
        return {
          value: slot.id,
          label: `${toTimeLabel(slot)}${labelSuffix}`,
          disabled: isBooked,
          session: getSessionByStartTime(slot.startTime),
        }
      })

    const grouped = mapped.reduce((acc, option) => {
      const group = acc[option.session] || []
      group.push({
        value: option.value,
        label: option.label,
        disabled: option.disabled,
      })
      acc[option.session] = group
      return acc
    }, {})

    const orderedSessions = ['Buổi sáng', 'Buổi chiều', 'Khác']
    return orderedSessions
      .filter((session) => grouped[session]?.length)
      .map((session) => ({
        label: session,
        options: grouped[session],
      }))
  }, [selectedDate, selectedDoctorId, slotAvailabilityMap, timeSlots])

  const flatSlotOptions = useMemo(
    () => slotOptions.flatMap((group) => group.options || []),
    [slotOptions],
  )

  const selectedDoctorLabel = useMemo(
    () => doctorOptions.find((item) => item.value === selectedDoctorId)?.label,
    [doctorOptions, selectedDoctorId],
  )
  const selectedSlotLabel = useMemo(
    () => flatSlotOptions.find((item) => item.value === selectedTimeSlotId)?.label,
    [flatSlotOptions, selectedTimeSlotId],
  )

  useEffect(() => {
    let active = true

    async function loadBookedSlots() {
      if (!selectedDoctorId || !selectedDate) {
        setSlotAvailabilities([])
        return
      }

      try {
        const response = await getBookedTimeSlotIds({
          doctorId: selectedDoctorId,
          appointmentDate: selectedDate.format('YYYY-MM-DD'),
        })
        if (!active) return
        setSlotAvailabilities(response?.result || [])
      } catch {
        if (!active) return
        setSlotAvailabilities([])
      }
    }

    loadBookedSlots()

    return () => {
      active = false
    }
  }, [selectedDate, selectedDoctorId])

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
        paymentMethod: values.paymentMethod,
      }

      const response = await bookAppointment(payload)
      const result = response?.result || null

      if (result?.paymentStatus === 'SUCCESS') {
        message.success('Đặt lịch và thanh toán thành công')
      } else if (result?.paymentStatus === 'PENDING') {
        message.info('Đã tạo lịch hẹn và khởi tạo thanh toán. Vui lòng hoàn tất để xác nhận lịch.')
      } else {
        message.warning('Đã tạo lịch hẹn nhưng khởi tạo thanh toán chưa thành công. Vui lòng thử lại.')
      }

      if (result?.paymentMethod === PAYMENT_METHODS.MOMO && result?.paymentUrl) {
        window.open(result.paymentUrl, '_blank', 'noopener,noreferrer')
      }

      form.resetFields(['reason', 'note', 'paymentMethod'])
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Space orientation="vertical" size={4}>
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
            <Form
              form={form}
              layout="vertical"
              onValuesChange={handleValuesChange}
              initialValues={{ paymentMethod: PAYMENT_METHODS.MOMO }}
            >
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

              <Form.Item label="Số tiền đặt cọc (VND)">
                <Input
                  value={new Intl.NumberFormat('vi-VN').format(depositAmount || 0)}
                  suffix="VND"
                  disabled
                />
              </Form.Item>

              <Form.Item
                label="Phương thức thanh toán"
                name="paymentMethod"
                rules={[{ required: true, message: 'Vui lòng chọn phương thức thanh toán' }]}
              >
                <Select
                  options={[{ value: PAYMENT_METHODS.MOMO, label: 'MoMo (thanh toán online)' }]}
                  disabled
                />
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
            <Space orientation="vertical" size={8}>
              <Text>
                <ClusterOutlined /> Khoa:{' '}
                {specialtyOptions.find((item) => item.value === selectedSpecialtyId)?.label || '-'}
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
              <Text>
                Tiền đặt cọc:{' '}
                {`${new Intl.NumberFormat('vi-VN').format(depositAmount || 0)} VND`}
              </Text>
            </Space>
          </Card>
        </Col>
      </Row>
    </Space>
  )
}

