import { App, Button, Card, DatePicker, Form, Input, Select, Space, Typography } from 'antd'
import dayjs from 'dayjs'
import { useMemo, useState } from 'react'
import { bookAppointment } from '../services/appointmentService.js'
import { getErrorMessage } from '../utils/httpError.js'

const { Title } = Typography

const DOCTORS = [
  { value: 'doctor-1', label: 'Dr. Nguyen Van A' },
  { value: 'doctor-2', label: 'Dr. Tran Thi B' },
]

const TIME_SLOTS = [
  { value: 'slot-1', label: '08:00 - 09:00' },
  { value: 'slot-2', label: '09:00 - 10:00' },
  { value: 'slot-3', label: '13:00 - 14:00' },
]

export default function AppointmentPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [lastResult, setLastResult] = useState(null)

  const selectedDoctor = Form.useWatch('doctorId', form)
  const selectedDate = Form.useWatch('appointmentDate', form)
  const selectedSlot = Form.useWatch('timeSlotId', form)

  const bookingPreview = useMemo(
    () => ({
      doctor: DOCTORS.find((item) => item.value === selectedDoctor)?.label || '-',
      date: selectedDate ? selectedDate.format('DD/MM/YYYY') : '-',
      timeSlot: TIME_SLOTS.find((item) => item.value === selectedSlot)?.label || '-',
    }),
    [selectedDate, selectedDoctor, selectedSlot],
  )

  async function handleSubmit() {
    try {
      const values = await form.validateFields()
      setSubmitting(true)

      const payload = {
        doctorId: values.doctorId,
        appointmentDate: values.appointmentDate.format('YYYY-MM-DD'),
        timeSlotId: values.timeSlotId,
        reason: values.reason.trim(),
      }

      const response = await bookAppointment(payload)
      setLastResult(response?.result || null)
      message.success('Appointment booked successfully')
      form.resetFields(['reason'])
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
        <Title level={3} style={{ margin: 0 }}>
          Appointment Booking
        </Title>
      </Card>

      <Card>
        <Form form={form} layout="vertical">
          <Form.Item
            name="doctorId"
            label="Doctor"
            rules={[{ required: true, message: 'Doctor is required' }]}
          >
            <Select
              placeholder="Select doctor"
              options={DOCTORS}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>

          <Form.Item
            name="appointmentDate"
            label="Date"
            rules={[{ required: true, message: 'Date is required' }]}
          >
            <DatePicker
              style={{ width: '100%' }}
              format="DD/MM/YYYY"
              disabledDate={(current) =>
                current ? current.isBefore(dayjs().startOf('day')) : false
              }
            />
          </Form.Item>

          <Form.Item
            name="timeSlotId"
            label="Time Slot"
            rules={[{ required: true, message: 'Time slot is required' }]}
          >
            <Select placeholder="Select time slot" options={TIME_SLOTS} />
          </Form.Item>

          <Form.Item
            name="reason"
            label="Reason"
            rules={[
              { required: true, message: 'Reason is required' },
              { max: 500, message: 'Reason must be less than 500 characters' },
            ]}
          >
            <Input.TextArea rows={4} placeholder="Describe reason for appointment" />
          </Form.Item>

          <Space>
            <Button type="primary" loading={submitting} onClick={handleSubmit}>
              Submit Booking
            </Button>
            <Button onClick={() => form.resetFields()}>Reset</Button>
          </Space>
        </Form>
      </Card>

      <Card title="Booking Preview">
        <div>Doctor: {bookingPreview.doctor}</div>
        <div>Date: {bookingPreview.date}</div>
        <div>Time Slot: {bookingPreview.timeSlot}</div>
      </Card>

      {lastResult ? (
        <Card title="Latest Result">
          <div>Appointment ID: {lastResult.appointmentId || '-'}</div>
          <div>Status: {lastResult.status || '-'}</div>
        </Card>
      ) : null}
    </Space>
  )
}

