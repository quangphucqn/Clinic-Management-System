import { App, Button, Card, Descriptions, Drawer, Empty, Space, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  getMyAppointmentHistory,
  retryAppointmentPayment,
} from '../../../services/appointmentPatientService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph } = Typography
const PAYMENT_METHODS = {
  MOMO: 'MOMO',
}

function formatCurrency(value) {
  if (value === undefined || value === null || Number.isNaN(Number(value))) return '-'
  return Number(value).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
}

function renderPaymentStatus(status) {
  if (status === 'SUCCESS') return <Tag color="success">Đã thanh toán</Tag>
  if (status === 'PENDING') return <Tag color="processing">Chờ thanh toán</Tag>
  if (status === 'FAILED') return <Tag color="error">Thanh toán thất bại</Tag>
  return <Tag>{status || 'Chưa có giao dịch'}</Tag>
}

function getPaymentMethodLabel(method) {
  if (method === PAYMENT_METHODS.MOMO) return 'MoMo'
  return method || '-'
}

function isPastAppointmentDate(appointmentDate) {
  if (!appointmentDate) return false
  const appointmentDay = dayjs(appointmentDate)
  if (!appointmentDay.isValid()) return false
  return appointmentDay.isBefore(dayjs(), 'day')
}

function renderAppointmentStatus(status) {
  if (status === 'CONFIRMED') return <Tag color="success">Đã xác nhận</Tag>
  if (status === 'PENDING') return <Tag color="warning">Chờ xác nhận</Tag>
  if (status === 'COMPLETED') return <Tag color="blue">Đã khám</Tag>
  if (status === 'CANCELLED') return <Tag color="default">Đã hủy</Tag>
  return <Tag>{status || '-'}</Tag>
}

export default function PatientDepositPage() {
  const { message } = App.useApp()
  const [loading, setLoading] = useState(false)
  const [retryingKey, setRetryingKey] = useState(null)
  const [appointments, setAppointments] = useState([])
  const [selectedAppointment, setSelectedAppointment] = useState(null)

  const loadAppointments = useCallback(async () => {
    try {
      setLoading(true)
      const response = await getMyAppointmentHistory({ page: 0, size: 100 })
      setAppointments(response?.result?.content || [])
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [message])

  useEffect(() => {
    loadAppointments()
  }, [loadAppointments])

  const handleRetryPayment = useCallback(async (record) => {
    if (isPastAppointmentDate(record?.appointmentDate)) {
      message.warning('Lịch khám đã qua ngày hiện tại, không thể thanh toán đặt cọc.')
      return
    }

    try {
      setRetryingKey(record.appointmentId)
      const response = await retryAppointmentPayment(record.appointmentId, PAYMENT_METHODS.MOMO)
      const result = response?.result
      if (result?.paymentStatus === 'SUCCESS') {
        message.success('Thanh toán thành công, lịch hẹn đã được xác nhận.')
      } else if (result?.paymentStatus === 'PENDING') {
        message.info('Đã tạo giao dịch mới. Vui lòng hoàn tất thanh toán để xác nhận lịch.')
      } else {
        message.warning('Khởi tạo lại thanh toán chưa thành công, vui lòng thử lại.')
      }
      if (result?.paymentUrl) {
        window.open(result.paymentUrl, '_blank', 'noopener,noreferrer')
      }
      await loadAppointments()
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setRetryingKey(null)
    }
  }, [loadAppointments, message])

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
        width: 260,
        render: (value) => value || '-',
      },
      {
        title: 'Lịch khám',
        dataIndex: 'timeSlot',
        key: 'timeSlot',
        width: 170,
        render: (value, record) => (
          <Space size={6}>
            <span>{value || '-'}</span>
            {renderAppointmentStatus(record.status)}
          </Space>
        ),
      },
      {
        title: 'Thanh toán',
        dataIndex: 'paymentStatus',
        key: 'paymentStatus',
        width: 150,
        render: renderPaymentStatus,
      },
      {
        title: 'Đặt cọc',
        dataIndex: 'depositAmount',
        key: 'depositAmount',
        width: 160,
        render: (value) => formatCurrency(value),
      },
      {
        title: 'Thao tác',
        key: 'action',
        width: 170,
        fixed: 'right',
        render: (_, record) => {
          const isPastDate = isPastAppointmentDate(record.appointmentDate)
          const canRetry =
            record.status !== 'CANCELLED' && record.paymentStatus !== 'SUCCESS' && !isPastDate
          if (isPastDate) return <Tag>Quá ngày khám</Tag>
          if (!canRetry) return '-'
          return (
            <Button
              type="primary"
              size="small"
              loading={retryingKey === record.appointmentId}
              onClick={(event) => {
                event.stopPropagation()
                handleRetryPayment(record)
              }}
            >
              Thanh toán MoMo
            </Button>
          )
        },
      },
    ],
    [handleRetryPayment, retryingKey],
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
              Thanh toán đặt cọc
            </Title>
            <Paragraph type="secondary" style={{ marginTop: 8, marginBottom: 0 }}>
              Danh sách lịch khám gọn theo trạng thái thanh toán. Nhấn vào mỗi dòng để xem chi tiết.
            </Paragraph>
          </div>
        </Space>
      </Card>

      <Card loading={loading}>
        {appointments.length ? (
          <Table
            rowKey="appointmentId"
            columns={columns}
            dataSource={appointments}
            pagination={{ pageSize: 10, showSizeChanger: false }}
            scroll={{ x: 900 }}
            onRow={(record) => ({
              onClick: () => setSelectedAppointment(record),
              style: { cursor: 'pointer' },
            })}
          />
        ) : (
          <Empty description="Bạn chưa có lịch hẹn nào" />
        )}
      </Card>

      <Drawer
        title="Chi tiết lịch khám"
        width={520}
        open={Boolean(selectedAppointment)}
        onClose={() => setSelectedAppointment(null)}
        extra={
          selectedAppointment?.status !== 'CANCELLED' &&
          selectedAppointment?.paymentStatus !== 'SUCCESS' &&
          !isPastAppointmentDate(selectedAppointment?.appointmentDate) ? (
            <Button
              type="primary"
              loading={retryingKey === selectedAppointment?.appointmentId}
              onClick={() => handleRetryPayment(selectedAppointment)}
            >
              Thanh toán MoMo
            </Button>
          ) : null
        }
      >
        {selectedAppointment ? (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="Mã lịch hẹn">
              {selectedAppointment.appointmentId}
            </Descriptions.Item>
            <Descriptions.Item label="Bác sĩ">{selectedAppointment.doctorName || '-'}</Descriptions.Item>
            <Descriptions.Item label="Ngày khám">
              {selectedAppointment.appointmentDate
                ? dayjs(selectedAppointment.appointmentDate).format('DD/MM/YYYY')
                : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Khung giờ">{selectedAppointment.timeSlot || '-'}</Descriptions.Item>
            <Descriptions.Item label="Trạng thái lịch">
              {renderAppointmentStatus(selectedAppointment.status)}
            </Descriptions.Item>
            <Descriptions.Item label="Trạng thái thanh toán">
              {renderPaymentStatus(selectedAppointment.paymentStatus)}
            </Descriptions.Item>
            <Descriptions.Item label="Phương thức">
              {getPaymentMethodLabel(selectedAppointment.paymentMethod)}
            </Descriptions.Item>
            <Descriptions.Item label="Tiền đặt cọc">
              {formatCurrency(selectedAppointment.depositAmount)}
            </Descriptions.Item>
            <Descriptions.Item label="Mã giao dịch">
              {selectedAppointment.transactionCode || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Lý do khám">{selectedAppointment.reason || '-'}</Descriptions.Item>
            <Descriptions.Item label="Ghi chú">{selectedAppointment.note || '-'}</Descriptions.Item>
          </Descriptions>
        ) : null}
      </Drawer>
    </Space>
  )
}
