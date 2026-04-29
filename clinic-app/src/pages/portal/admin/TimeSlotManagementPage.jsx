import {
  App,
  Button,
  Card,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Space,
  Switch,
  Table,
  Tag,
  TimePicker,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useEffect, useState } from 'react'
import {
  createTimeSlot,
  deleteTimeSlot,
  getTimeSlots,
  updateTimeSlot,
} from '../../../services/timeSlotService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title } = Typography

function toTimeValue(time) {
  if (!time) return null
  return dayjs(`1970-01-01T${time}`)
}

function toTimeString(timeValue) {
  if (!timeValue) return null
  return timeValue.format('HH:mm:ss')
}

export default function TimeSlotManagementPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [timeSlots, setTimeSlots] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [searchSlotCode, setSearchSlotCode] = useState('')
  const [debouncedSlotCode, setDebouncedSlotCode] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingTimeSlot, setEditingTimeSlot] = useState(null)

  async function loadTimeSlots({
    nextPage = page,
    nextPageSize = pageSize,
    slotCode = debouncedSlotCode,
  } = {}) {
    setLoading(true)
    try {
      const response = await getTimeSlots({
        page: nextPage - 1,
        size: nextPageSize,
        slotCode: slotCode || undefined,
      })
      const pageData = response?.result
      setTimeSlots(pageData?.content || [])
      setTotal(pageData?.totalElements || 0)
      setPage((pageData?.number ?? 0) + 1)
      setPageSize(pageData?.size || nextPageSize)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedSlotCode(searchSlotCode.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [searchSlotCode])

  useEffect(() => {
    setPage(1)
    loadTimeSlots({
      nextPage: 1,
      nextPageSize: pageSize,
      slotCode: debouncedSlotCode,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedSlotCode])

  function openCreateModal() {
    setEditingTimeSlot(null)
    form.resetFields()
    form.setFieldsValue({ enabled: true })
    setModalOpen(true)
  }

  function openEditModal(timeSlot) {
    setEditingTimeSlot(timeSlot)
    form.setFieldsValue({
      slotCode: timeSlot.slotCode,
      startTime: toTimeValue(timeSlot.startTime),
      endTime: toTimeValue(timeSlot.endTime),
      maxPatientsPerSlot: timeSlot.maxPatientsPerSlot,
      enabled: Boolean(timeSlot.enabled),
    })
    setModalOpen(true)
  }

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)

      const payload = {
        startTime: toTimeString(values.startTime),
        endTime: toTimeString(values.endTime),
        maxPatientsPerSlot: values.maxPatientsPerSlot,
        enabled: Boolean(values.enabled),
      }

      if (editingTimeSlot) {
        await updateTimeSlot(editingTimeSlot.id, payload)
        message.success('Cập nhật giờ khám thành công')
      } else {
        await createTimeSlot({
          ...payload,
          slotCode: values.slotCode.trim(),
        })
        message.success('Thêm giờ khám thành công')
      }

      setModalOpen(false)
      await loadTimeSlots()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(timeSlotId) {
    setDeletingId(timeSlotId)
    try {
      await deleteTimeSlot(timeSlotId)
      message.success('Xóa giờ khám thành công')
      await loadTimeSlots({ slotCode: debouncedSlotCode })
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  const columns = [
    {
      title: 'Mã giờ khám',
      dataIndex: 'slotCode',
      key: 'slotCode',
      width: 210,
    },
    {
      title: 'Bắt đầu',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 120,
    },
    {
      title: 'Kết thúc',
      dataIndex: 'endTime',
      key: 'endTime',
      width: 120,
    },
    {
      title: 'Số BN tối đa',
      dataIndex: 'maxPatientsPerSlot',
      key: 'maxPatientsPerSlot',
      width: 140,
    },
    {
      title: 'Trạng thái',
      key: 'enabled',
      width: 150,
      render: (_, record) => (
        <Tag color={record.enabled ? 'green' : 'default'}>
          {record.enabled ? 'Đang bật' : 'Đang tắt'}
        </Tag>
      ),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 210,
      render: (_, record) => (
        <Space size={4}>
          <Button type="link" onClick={() => openEditModal(record)}>
            Cập nhật
          </Button>
          <Popconfirm
            title="Xóa giờ khám"
            description="Bạn chắc chắn muốn xóa giờ khám này?"
            okText="Xóa"
            cancelText="Hủy"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger loading={deletingId === record.id}>
              Xóa
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
        <Space
          style={{ width: '100%', justifyContent: 'space-between' }}
          align="start"
          wrap
        >
          <Title level={3} style={{ margin: 0 }}>
            Quản lý giờ khám
          </Title>
          <Space wrap>
            <Input
              placeholder="Tìm theo mã giờ khám"
              value={searchSlotCode}
              onChange={(e) => setSearchSlotCode(e.target.value)}
              allowClear
              style={{ width: 250 }}
            />
            <Button type="primary" onClick={openCreateModal}>
              Thêm giờ khám
            </Button>
          </Space>
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={timeSlots}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (value) => `Tổng ${value} giờ khám`,
          }}
          onChange={(pagination) => {
            const nextPage = pagination.current || 1
            const nextPageSize = pagination.pageSize || 10
            setPage(nextPage)
            setPageSize(nextPageSize)
            loadTimeSlots({ nextPage, nextPageSize })
          }}
        />
      </Space>

      <Modal
        title={editingTimeSlot ? 'Cập nhật giờ khám' : 'Thêm giờ khám'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSave}
        confirmLoading={saving}
        okText={editingTimeSlot ? 'Lưu' : 'Tạo'}
        cancelText="Hủy"
        forceRender
        destroyOnHidden
      >
        <Form form={form} layout="vertical" initialValues={{ enabled: true }}>
          <Form.Item
            label="Mã giờ khám"
            name="slotCode"
            rules={[
              { required: !editingTimeSlot, message: 'Vui lòng nhập mã giờ khám' },
              { max: 30, message: 'Tối đa 30 ký tự' },
            ]}
          >
            <Input
              placeholder="AFTERNOON_1300_1500"
              disabled={Boolean(editingTimeSlot)}
            />
          </Form.Item>

          <Space style={{ width: '100%' }} size="middle" wrap>
            <Form.Item
              label="Giờ bắt đầu"
              name="startTime"
              rules={[{ required: true, message: 'Vui lòng chọn giờ bắt đầu' }]}
            >
              <TimePicker format="HH:mm:ss" />
            </Form.Item>
            <Form.Item
              label="Giờ kết thúc"
              name="endTime"
              rules={[{ required: true, message: 'Vui lòng chọn giờ kết thúc' }]}
            >
              <TimePicker format="HH:mm:ss" />
            </Form.Item>
          </Space>

          <Form.Item
            label="Số bệnh nhân tối đa"
            name="maxPatientsPerSlot"
            rules={[{ required: true, message: 'Vui lòng nhập số bệnh nhân tối đa' }]}
          >
            <InputNumber style={{ width: '100%' }} min={1} precision={0} />
          </Form.Item>

          <Form.Item
            label="Bật giờ khám"
            name="enabled"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

