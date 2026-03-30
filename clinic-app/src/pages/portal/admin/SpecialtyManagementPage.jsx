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
  createSpecialty,
  deleteSpecialty,
  getSpecialties,
  updateSpecialty,
} from '../../../services/specialtyService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title } = Typography

export default function SpecialtyManagementPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [specialties, setSpecialties] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [searchName, setSearchName] = useState('')
  const [debouncedName, setDebouncedName] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingSpecialty, setEditingSpecialty] = useState(null)

  async function loadSpecialties({
    nextPage = page,
    nextPageSize = pageSize,
    name = debouncedName,
  } = {}) {
    setLoading(true)
    try {
      const response = await getSpecialties({
        page: nextPage - 1,
        size: nextPageSize,
        name: name || undefined,
      })
      const pageData = response?.result
      setSpecialties(pageData?.content || [])
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
    loadSpecialties({ nextPage: 1, nextPageSize: pageSize, name: debouncedName })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedName])

  function openCreateModal() {
    setEditingSpecialty(null)
    form.resetFields()
    setModalOpen(true)
  }

  function openEditModal(specialty) {
    setEditingSpecialty(specialty)
    form.setFieldsValue({
      name: specialty.name,
      description: specialty.description,
    })
    setModalOpen(true)
  }

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)

      const payload = {
        name: values.name.trim(),
        description: values.description?.trim() || null,
      }

      if (editingSpecialty) {
        await updateSpecialty(editingSpecialty.id, payload)
        message.success('Cập nhật chuyên khoa thành công')
      } else {
        await createSpecialty(payload)
        message.success('Thêm chuyên khoa thành công')
      }

      setModalOpen(false)
      await loadSpecialties()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(specialtyId) {
    setDeletingId(specialtyId)
    try {
      await deleteSpecialty(specialtyId)
      message.success('Xóa chuyên khoa thành công')
      await loadSpecialties({ name: debouncedName })
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  const columns = [
    {
      title: 'Tên chuyên khoa',
      dataIndex: 'name',
      key: 'name',
      width: 280,
    },
    {
      title: 'Mô tả',
      dataIndex: 'description',
      key: 'description',
      render: (value) => value || '-',
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
            title="Xóa chuyên khoa"
            description="Bạn chắc chắn muốn xóa chuyên khoa này?"
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
            Quản lý chuyên khoa
          </Title>
          <Space wrap>
            <Input
              placeholder="Tìm theo tên chuyên khoa"
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              allowClear
              style={{ width: 260 }}
            />
            <Button type="primary" onClick={openCreateModal}>
              Thêm chuyên khoa
            </Button>
          </Space>
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={specialties}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (value) => `Tổng ${value} chuyên khoa`,
          }}
          onChange={(pagination) => {
            const nextPage = pagination.current || 1
            const nextPageSize = pagination.pageSize || 10
            setPage(nextPage)
            setPageSize(nextPageSize)
            loadSpecialties({ nextPage, nextPageSize })
          }}
        />
      </Space>

      <Modal
        title={editingSpecialty ? 'Cập nhật chuyên khoa' : 'Thêm chuyên khoa'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSave}
        confirmLoading={saving}
        okText={editingSpecialty ? 'Lưu' : 'Tạo'}
        cancelText="Hủy"
        forceRender
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Tên chuyên khoa"
            name="name"
            rules={[
              { required: true, message: 'Vui lòng nhập tên chuyên khoa' },
              { max: 100, message: 'Tối đa 100 ký tự' },
            ]}
          >
            <Input placeholder="Nhập tên chuyên khoa" maxLength={100} />
          </Form.Item>
          <Form.Item
            label="Mô tả"
            name="description"
          >
            <Input.TextArea rows={3} placeholder="Nhập mô tả (tùy chọn)" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

