import {
  App,
  Button,
  Card,
  Form,
  Input,
  Modal,
  Popconfirm,
  Space,
  Table,
  Typography,
} from 'antd'
import { useEffect, useState } from 'react'
import {
  createUnit,
  deleteUnit,
  getUnits,
  updateUnit,
} from '../../../services/unitService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title } = Typography

export default function UnitManagementPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [units, setUnits] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [searchName, setSearchName] = useState('')
  const [debouncedName, setDebouncedName] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingUnit, setEditingUnit] = useState(null)
  const [deletingId, setDeletingId] = useState(null)

  async function loadUnits({
    nextPage = page,
    nextPageSize = pageSize,
    name = debouncedName,
  } = {}) {
    setLoading(true)
    try {
      const response = await getUnits({
        page: nextPage - 1,
        size: nextPageSize,
        name: name || undefined,
      })
      const pageData = response?.result
      setUnits(pageData?.content || [])
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
      setDebouncedName(searchName.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [searchName])

  useEffect(() => {
    setPage(1)
    loadUnits({ nextPage: 1, nextPageSize: pageSize, name: debouncedName })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedName])

  function openCreateModal() {
    setEditingUnit(null)
    form.resetFields()
    setModalOpen(true)
  }

  function openEditModal(unit) {
    setEditingUnit(unit)
    form.setFieldsValue({ name: unit.name })
    setModalOpen(true)
  }

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)
      const payload = { name: values.name.trim() }

      if (editingUnit) {
        await updateUnit(editingUnit.id, payload)
        message.success('Cập nhật loại thuốc thành công')
      } else {
        await createUnit(payload)
        message.success('Thêm loại thuốc thành công')
      }

      setModalOpen(false)
      await loadUnits()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(unitId) {
    setDeletingId(unitId)
    try {
      await deleteUnit(unitId)
      message.success('Xóa loại thuốc thành công')
      await loadUnits({ name: debouncedName })
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  const columns = [
    {
      title: 'Tên loại thuốc',
      dataIndex: 'name',
      key: 'name',
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
            title="Xóa loại thuốc"
            description="Bạn chắc chắn muốn xóa loại thuốc này?"
            okText="Xóa"
            cancelText="Hủy"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button
              type="link"
              danger
              loading={deletingId === record.id}
            >
              Xóa
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <Space
        orientation="vertical"
        size="middle"
        style={{ width: '100%' }}
      >
        <Space
          style={{ width: '100%', justifyContent: 'space-between' }}
          align="start"
          wrap
        >
          <Title level={3} style={{ margin: 0 }}>
            Quản lý danh mục thuốc
          </Title>
          <Space wrap>
            <Input
              placeholder="Tìm theo tên loại thuốc"
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              allowClear
              style={{ width: 240 }}
            />
            <Button type="primary" onClick={openCreateModal}>
              Thêm danh mục thuốc
            </Button>
          </Space>
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={units}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (value) => `Tổng ${value} danh mục thuốc`,
          }}
          onChange={(pagination) => {
            const nextPage = pagination.current || 1
            const nextPageSize = pagination.pageSize || 10
            setPage(nextPage)
            setPageSize(nextPageSize)
            loadUnits({ nextPage, nextPageSize })
          }}
        />
      </Space>

      <Modal
        title={editingUnit ? 'Cập nhật danh mục thuốc' : 'Thêm danh mục thuốc'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSave}
        confirmLoading={saving}
        okText={editingUnit ? 'Lưu' : 'Tạo'}
        cancelText="Hủy"
        forceRender
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Tên loại thuốc"
            name="name"
            rules={[
              { required: true, message: 'Vui lòng nhập tên loại thuốc' },
              { max: 255, message: 'Tối đa 255 ký tự' },
            ]}
          >
            <Input placeholder="Nhập tên loại thuốc" maxLength={255} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

