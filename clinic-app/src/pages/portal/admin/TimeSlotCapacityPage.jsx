import { App, Button, Card, Form, InputNumber, Modal, Space, Table, Tag, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { getTimeSlots, updateTimeSlot } from '../../../services/timeSlotService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Text } = Typography

export default function TimeSlotCapacityPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [slots, setSlots] = useState([])
  const [editingSlot, setEditingSlot] = useState(null)
  const [modalOpen, setModalOpen] = useState(false)

  async function loadSlots() {
    setLoading(true)
    try {
      const response = await getTimeSlots({ page: 0, size: 200 })
      setSlots(response?.result?.content || [])
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadSlots()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function openEditModal(slot) {
    setEditingSlot(slot)
    form.setFieldsValue({ maxPatientsPerSlot: slot.maxPatientsPerSlot })
    setModalOpen(true)
  }

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)
      await updateTimeSlot(editingSlot.id, { maxPatientsPerSlot: values.maxPatientsPerSlot })
      message.success('Cập nhật số slot thành công')
      setModalOpen(false)
      await loadSlots()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Card>
      <Space direction="vertical" size="middle" style={{ width: '100%' }}>
        <Title level={3} style={{ margin: 0 }}>
          Quản lý số slot theo khung giờ
        </Title>
        <Text type="secondary">
          Admin chỉnh số bệnh nhân tối đa cho từng khung giờ. Hệ thống sẽ khóa khi đạt đủ slot.
        </Text>

        <Table
          rowKey="id"
          loading={loading}
          dataSource={slots}
          pagination={{ pageSize: 10, showSizeChanger: false }}
          columns={[
            {
              title: 'Mã khung giờ',
              dataIndex: 'slotCode',
              key: 'slotCode',
            },
            {
              title: 'Thời gian',
              key: 'time',
              render: (_, row) => `${row.startTime?.slice(0, 5)} - ${row.endTime?.slice(0, 5)}`,
            },
            {
              title: 'Số slot tối đa',
              dataIndex: 'maxPatientsPerSlot',
              key: 'maxPatientsPerSlot',
              width: 160,
            },
            {
              title: 'Trạng thái',
              key: 'enabled',
              width: 140,
              render: (_, row) => (
                <Tag color={row.enabled ? 'green' : 'default'}>
                  {row.enabled ? 'Đang bật' : 'Đang tắt'}
                </Tag>
              ),
            },
            {
              title: 'Thao tác',
              key: 'actions',
              width: 160,
              render: (_, row) => (
                <Button type="primary" size="small" onClick={() => openEditModal(row)}>
                  Chỉnh số slot
                </Button>
              ),
            },
          ]}
        />
      </Space>

      <Modal
        title={`Chỉnh số slot - ${editingSlot?.slotCode || ''}`}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSave}
        confirmLoading={saving}
        okText="Lưu"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="maxPatientsPerSlot"
            label="Số slot tối đa"
            rules={[{ required: true, message: 'Vui lòng nhập số slot tối đa' }]}
          >
            <InputNumber style={{ width: '100%' }} min={1} precision={0} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
