import { SearchOutlined } from '@ant-design/icons'
import {
  Alert,
  App,
  Button,
  Card,
  Descriptions,
  Empty,
  Form,
  Input,
  Segmented,
  Space,
  Table,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useMemo, useState } from 'react'
import {
  getPrescriptionById,
  getPrescriptionByMedicalRecordId,
} from '../../../services/prescriptionService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph, Text } = Typography

const LOOKUP_TYPE = {
  PRESCRIPTION: 'prescription',
  MEDICAL_RECORD: 'medicalRecord',
}

const UUID_REGEX =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i

export default function PatientPrescriptionPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [lookupType, setLookupType] = useState(LOOKUP_TYPE.MEDICAL_RECORD)
  const [loading, setLoading] = useState(false)
  const [prescription, setPrescription] = useState(null)

  const lookupLabel = useMemo(
    () =>
      lookupType === LOOKUP_TYPE.PRESCRIPTION ? 'Prescription ID' : 'Medical Record ID',
    [lookupType],
  )

  const tableColumns = [
    {
      title: 'Mã thuốc',
      dataIndex: 'medicineCode',
      key: 'medicineCode',
      width: 140,
      render: (value) => value || '-',
    },
    {
      title: 'Tên thuốc',
      dataIndex: 'medicineName',
      key: 'medicineName',
      width: 220,
      render: (value) => value || '-',
    },
    {
      title: 'Đơn vị',
      dataIndex: 'unitName',
      key: 'unitName',
      width: 120,
      render: (value) => value || '-',
    },
    {
      title: 'Số lượng',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 110,
      render: (value) => value ?? '-',
    },
    {
      title: 'Liều dùng',
      dataIndex: 'dosage',
      key: 'dosage',
      width: 150,
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
  ]

  async function handleLookup(values) {
    try {
      const lookupId = values.lookupId?.trim()
      if (!lookupId) return

      setLoading(true)
      const response =
        lookupType === LOOKUP_TYPE.PRESCRIPTION
          ? await getPrescriptionById(lookupId)
          : await getPrescriptionByMedicalRecordId(lookupId)

      setPrescription(response?.result || null)
      message.success('Tải đơn thuốc thành công')
    } catch (error) {
      if (error?.errorFields) return
      setPrescription(null)
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Title level={3} style={{ margin: 0 }}>
          Xem đơn thuốc
        </Title>
        <Paragraph type="secondary" style={{ marginTop: 8, marginBottom: 0 }}>
          Tra cứu đơn thuốc theo Medical Record ID hoặc Prescription ID.
        </Paragraph>
      </Card>

      <Card loading={loading}>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Segmented
            value={lookupType}
            options={[
              { value: LOOKUP_TYPE.MEDICAL_RECORD, label: 'Theo hồ sơ bệnh án' },
              { value: LOOKUP_TYPE.PRESCRIPTION, label: 'Theo đơn thuốc' },
            ]}
            onChange={(value) => {
              setLookupType(value)
              setPrescription(null)
              form.resetFields()
            }}
          />

          <Form form={form} layout="vertical" onFinish={handleLookup}>
            <Form.Item
              label={lookupLabel}
              name="lookupId"
              rules={[
                { required: true, message: `Vui lòng nhập ${lookupLabel}` },
                {
                  validator: (_, value) => {
                    if (!value || UUID_REGEX.test(value.trim())) return Promise.resolve()
                    return Promise.reject(new Error('ID không đúng định dạng UUID'))
                  },
                },
              ]}
            >
              <Input placeholder={`Nhập ${lookupLabel} (UUID)`} allowClear />
            </Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                Xem đơn thuốc
              </Button>
              <Button
                onClick={() => {
                  form.resetFields()
                  setPrescription(null)
                }}
              >
                Xóa kết quả
              </Button>
            </Space>
          </Form>
        </Space>
      </Card>

      {prescription ? (
        <>
          <Card>
            <Descriptions bordered size="small" column={1}>
              <Descriptions.Item label="Đơn thuốc ID">{prescription.id}</Descriptions.Item>
              <Descriptions.Item label="Medical Record ID">
                {prescription.medicalRecordId}
              </Descriptions.Item>
              <Descriptions.Item label="Bệnh nhân">
                {prescription.patientName || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Bác sĩ">{prescription.doctorName || '-'}</Descriptions.Item>
              <Descriptions.Item label="Ngày kê đơn">
                {prescription.issuedAt
                  ? dayjs(prescription.issuedAt).format('DD/MM/YYYY HH:mm')
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Hướng dẫn">
                {prescription.instructions || '-'}
              </Descriptions.Item>
            </Descriptions>
          </Card>

          <Card
            title={`Danh sách thuốc (${prescription.items?.length || 0})`}
            extra={<Text type="secondary">Chi tiết theo đơn thuốc đã kê</Text>}
          >
            {prescription.items?.length ? (
              <Table
                rowKey="id"
                columns={tableColumns}
                dataSource={prescription.items}
                pagination={false}
                scroll={{ x: 1100 }}
              />
            ) : (
              <Alert type="info" showIcon message="Đơn thuốc chưa có chi tiết thuốc." />
            )}
          </Card>
        </>
      ) : (
        <Card>
          <Empty description="Nhập ID để tra cứu đơn thuốc" />
        </Card>
      )}
    </Space>
  )
}

