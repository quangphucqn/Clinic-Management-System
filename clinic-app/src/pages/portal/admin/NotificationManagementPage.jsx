import {
  App,
  Button,
  Card,
  Descriptions,
  Drawer,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Spin,
  Switch,
  Table,
  Tag,
  Typography,
} from 'antd'
import { useEffect, useState } from 'react'
import {
  createNotification,
  deleteNotification,
  getNotificationById,
  getNotifications,
  updateNotification,
} from '../../../services/notificationService.js'
import { getUsers } from '../../../services/userService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph } = Typography

const ROLE_OPTIONS = [
  { value: 'ADMIN', label: 'ADMIN' },
  { value: 'DOCTOR', label: 'DOCTOR' },
  { value: 'PATIENT', label: 'PATIENT' },
]

function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('vi-VN')
}

export default function NotificationManagementPage() {
  const USER_PAGE_SIZE = 10
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [notifications, setNotifications] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [searchTitle, setSearchTitle] = useState('')
  const [debouncedTitle, setDebouncedTitle] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingItem, setEditingItem] = useState(null)
  const [selectedItem, setSelectedItem] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [userOptions, setUserOptions] = useState([])
  const [usersLoading, setUsersLoading] = useState(false)
  const [userSearch, setUserSearch] = useState('')
  const [debouncedUserSearch, setDebouncedUserSearch] = useState('')
  const [usersPage, setUsersPage] = useState(0)
  const [usersHasMore, setUsersHasMore] = useState(true)

  function mapUserOption(user) {
    const labelParts = []
    if (user.fullName) labelParts.push(user.fullName)
    if (user.username) labelParts.push(`@${user.username}`)
    if (user.email) labelParts.push(user.email)

    return {
      value: user.id,
      label: labelParts.join(' - ') || user.id,
    }
  }

  async function loadUsers({ nextPage = 0, reset = false, keyword = debouncedUserSearch } = {}) {
    if (usersLoading) return
    setUsersLoading(true)
    try {
      const response = await getUsers({
        page: nextPage,
        size: USER_PAGE_SIZE,
        username: keyword || undefined,
      })
      const pageData = response?.result
      const items = (pageData?.content || []).map(mapUserOption)
      setUserOptions((prev) => {
        if (reset) return items
        const existed = new Set(prev.map((item) => item.value))
        const merged = [...prev]
        items.forEach((item) => {
          if (!existed.has(item.value)) merged.push(item)
        })
        return merged
      })
      setUsersPage(pageData?.number ?? nextPage)
      setUsersHasMore(!pageData?.last)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setUsersLoading(false)
    }
  }

  async function loadNotifications({
    nextPage = page,
    nextPageSize = pageSize,
    title = debouncedTitle,
  } = {}) {
    setLoading(true)
    try {
      const response = await getNotifications({
        page: nextPage - 1,
        size: nextPageSize,
        title: title || undefined,
      })
      const pageData = response?.result
      setNotifications(pageData?.content || [])
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
      setDebouncedTitle(searchTitle.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [searchTitle])

  useEffect(() => {
    setPage(1)
    loadNotifications({ nextPage: 1, nextPageSize: pageSize, title: debouncedTitle })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedTitle])

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedUserSearch(userSearch.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [userSearch])

  useEffect(() => {
    if (!modalOpen) return
    loadUsers({ nextPage: 0, reset: true, keyword: debouncedUserSearch })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [modalOpen, debouncedUserSearch])

  function openCreateModal() {
    setEditingItem(null)
    form.resetFields()
    form.setFieldsValue({ sentEmail: false })
    setUserSearch('')
    setDebouncedUserSearch('')
    setUserOptions([])
    setUsersPage(0)
    setUsersHasMore(true)
    setModalOpen(true)
  }

  async function openDetail(notificationId) {
    setDetailLoading(true)
    setSelectedItem(null)
    try {
      const response = await getNotificationById(notificationId)
      setSelectedItem(response?.result || null)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDetailLoading(false)
    }
  }

  async function openEditModal(notificationId) {
    try {
      const response = await getNotificationById(notificationId)
      const item = response?.result
      if (!item) return
      setEditingItem(item)
      setUserSearch('')
      setDebouncedUserSearch('')
      setUserOptions([])
      setUsersPage(0)
      setUsersHasMore(true)
      form.setFieldsValue({
        title: item.title,
        content: item.content,
        targetRole: item.targetRole,
        targetUserId: item.targetUserId,
        sentEmail: Boolean(item.emailSent),
      })
      if (item.targetUserId) {
        setUserOptions([
          {
            value: item.targetUserId,
            label: item.targetUsername
              ? `${item.targetUsername}${item.targetUserEmail ? ` - ${item.targetUserEmail}` : ''}`
              : item.targetUserId,
          },
        ])
      }
      setModalOpen(true)
    } catch (error) {
      message.error(getErrorMessage(error))
    }
  }

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)
      const payload = {
        title: values.title?.trim(),
        content: values.content?.trim(),
        targetRole: values.targetRole || undefined,
        targetUserId: values.targetUserId || undefined,
        sentEmail: Boolean(values.sentEmail),
      }

      if (editingItem) {
        await updateNotification(editingItem.id, payload)
        message.success('Cập nhật thông báo thành công')
      } else {
        await createNotification(payload)
        message.success('Tạo thông báo thành công')
      }
      setModalOpen(false)
      await loadNotifications()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(notificationId) {
    setDeletingId(notificationId)
    try {
      await deleteNotification(notificationId)
      message.success('Xóa thông báo thành công')
      if (selectedItem?.id === notificationId) {
        setSelectedItem(null)
      }
      await loadNotifications()
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  const columns = [
    {
      title: 'Tiêu đề',
      dataIndex: 'title',
      key: 'title',
      render: (_, record) => (
        <Button
          type="link"
          style={{ paddingInline: 0 }}
          onClick={() => openDetail(record.id)}
        >
          {record.title}
        </Button>
      ),
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 200,
      render: (value) => formatDateTime(value),
    },
    {
      title: 'Thao tác',
      key: 'actions',
      width: 220,
      render: (_, record) => (
        <Space size={4}>
          <Button type="link" onClick={() => openEditModal(record.id)}>
            Cập nhật
          </Button>
          <Popconfirm
            title="Xóa thông báo"
            description="Bạn chắc chắn muốn xóa thông báo này?"
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
            Quản lý thông báo
          </Title>
          <Space size="small" wrap>
            <Input
              placeholder="Tìm theo tiêu đề"
              value={searchTitle}
              onChange={(e) => setSearchTitle(e.target.value)}
              allowClear
              style={{ width: 260 }}
            />
            <Button type="primary" onClick={openCreateModal}>
              Tạo thông báo
            </Button>
          </Space>
        </Space>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={notifications}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (value) => `Tổng ${value} thông báo`,
          }}
          onChange={(pagination) => {
            const nextPage = pagination.current || 1
            const nextPageSize = pagination.pageSize || 10
            setPage(nextPage)
            setPageSize(nextPageSize)
            loadNotifications({ nextPage, nextPageSize })
          }}
        />
      </Space>

      <Drawer
        title={selectedItem ? `Chi tiết: ${selectedItem.title}` : 'Chi tiết thông báo'}
        width={560}
        open={Boolean(selectedItem)}
        onClose={() => setSelectedItem(null)}
      >
        {detailLoading ? (
          <div style={{ display: 'grid', placeItems: 'center', minHeight: 160 }}>
            <Spin />
          </div>
        ) : selectedItem ? (
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="Tiêu đề">{selectedItem.title}</Descriptions.Item>
            <Descriptions.Item label="Nội dung">
              <Paragraph style={{ margin: 0 }}>{selectedItem.content || '-'}</Paragraph>
            </Descriptions.Item>
            <Descriptions.Item label="Vai trò nhận">
              {selectedItem.targetRole || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="User nhận">
              {selectedItem.targetUsername || selectedItem.targetUserId || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Email nhận">
              {selectedItem.targetUserEmail || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Gửi email">
              <Tag color={selectedItem.emailSent ? 'green' : 'default'}>
                {selectedItem.emailSent ? 'Có' : 'Không'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Trạng thái">
              <Tag color={selectedItem.active ? 'green' : 'default'}>
                {selectedItem.active ? 'Đang hoạt động' : 'Ngưng'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Ngày tạo">
              {formatDateTime(selectedItem.createdAt)}
            </Descriptions.Item>
            <Descriptions.Item label="Cập nhật gần nhất">
              {formatDateTime(selectedItem.updatedAt)}
            </Descriptions.Item>
          </Descriptions>
        ) : null}
      </Drawer>

      <Modal
        title={editingItem ? 'Cập nhật thông báo' : 'Tạo thông báo'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSave}
        confirmLoading={saving}
        okText={editingItem ? 'Cập nhật' : 'Tạo'}
        cancelText="Hủy"
        forceRender
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="Tiêu đề"
            name="title"
            rules={[
              { required: true, message: 'Vui lòng nhập tiêu đề' },
              { max: 150, message: 'Tối đa 150 ký tự' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Nội dung"
            name="content"
            rules={[{ required: true, message: 'Vui lòng nhập nội dung' }]}
          >
            <Input.TextArea rows={4} />
          </Form.Item>
          <Form.Item label="Vai trò nhận" name="targetRole">
            <Select allowClear options={ROLE_OPTIONS} placeholder="Chọn vai trò" />
          </Form.Item>
          <Form.Item label="Người nhận cụ thể (User ID)" name="targetUserId">
            <Select
              allowClear
              showSearch
              filterOption={false}
              placeholder="Tìm theo username để chọn user"
              options={userOptions}
              loading={usersLoading}
              onSearch={setUserSearch}
              onPopupScroll={(event) => {
                const target = event.target
                const reachedBottom =
                  target.scrollTop + target.clientHeight >= target.scrollHeight - 12
                if (!reachedBottom || usersLoading || !usersHasMore) return
                loadUsers({ nextPage: usersPage + 1, reset: false })
              }}
            />
          </Form.Item>
          <Form.Item label="Gửi email" name="sentEmail" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

