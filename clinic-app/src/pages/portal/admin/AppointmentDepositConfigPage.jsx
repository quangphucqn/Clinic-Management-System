import { App, Button, Card, Form, InputNumber, Space, Typography } from 'antd'
import { useEffect, useState } from 'react'
import {
  getAppointmentDepositConfig,
  updateAppointmentDepositConfig,
} from '../../../services/appointmentDepositConfigService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Text } = Typography

function formatCurrency(value) {
  const numericValue = Number(value || 0)
  return `${new Intl.NumberFormat('vi-VN').format(numericValue)} VND`
}

export default function AppointmentDepositConfigPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [currentAmount, setCurrentAmount] = useState(0)

  async function loadConfig() {
    setLoading(true)
    try {
      const response = await getAppointmentDepositConfig()
      const amount = Number(response?.result?.amount || 0)
      setCurrentAmount(amount)
      form.setFieldsValue({ amount })
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadConfig()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)
      const response = await updateAppointmentDepositConfig({ amount: values.amount })
      const amount = Number(response?.result?.amount || values.amount)
      setCurrentAmount(amount)
      form.setFieldsValue({ amount })
      message.success('Cập nhật tiền đặt cọc thành công')
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Card loading={loading}>
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <Title level={3} style={{ margin: 0 }}>
          Cấu hình tiền đặt cọc khám bệnh
        </Title>
        <Text type="secondary">
          Chỉ admin có quyền chỉnh sửa. Số tiền này sẽ được áp dụng cho toàn bộ lượt đặt khám của bệnh nhân.
        </Text>
        <Text>Giá hiện tại: {formatCurrency(currentAmount)}</Text>

        <Form form={form} layout="vertical">
          <Form.Item
            label="Tiền đặt cọc (VND)"
            name="amount"
            rules={[
              { required: true, message: 'Vui lòng nhập tiền đặt cọc' },
              {
                validator: (_, value) => {
                  if (value === undefined || value === null || Number(value) <= 0) {
                    return Promise.reject(new Error('Tiền đặt cọc phải lớn hơn 0'))
                  }
                  return Promise.resolve()
                },
              },
            ]}
          >
            <InputNumber
              style={{ width: '100%' }}
              min={1000}
              step={1000}
              formatter={(value) =>
                value ? `${String(value).replace(/\B(?=(\d{3})+(?!\d))/g, ',')} VND` : ''
              }
              parser={(value) => value?.replace(/[^\d]/g, '') || ''}
              placeholder="Nhập số tiền đặt cọc"
            />
          </Form.Item>
          <Button type="primary" loading={saving} onClick={handleSave}>
            Lưu cấu hình
          </Button>
        </Form>
      </Space>
    </Card>
  )
}
