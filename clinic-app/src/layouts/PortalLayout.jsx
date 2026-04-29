import {
  BellFilled,
  AppstoreOutlined,
  BarChartOutlined,
  BellOutlined,
  CalendarOutlined,
  FileSearchOutlined,
  ClusterOutlined,
  ClockCircleOutlined,
  DownOutlined,
  FileTextOutlined,
  MedicineBoxOutlined,
  MoneyCollectOutlined,
  StarOutlined,
  TeamOutlined,
} from '@ant-design/icons'
import { Client } from '@stomp/stompjs'
import { App, Button, Dropdown, Layout, Menu, Modal, Space, Table, Typography } from 'antd'
import { useEffect, useRef, useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { API_BASE_URL } from '../config/env.js'
import { ROLES } from '../constants/roles.js'
import { ROUTES } from '../constants/routes.js'
import { useAuth } from '../hooks/useAuth.js'
import { getMyNotifications } from '../services/notificationService.js'
import { getErrorMessage } from '../utils/httpError.js'
import './PortalLayout.css'

const { Header, Sider, Content } = Layout
const { Text } = Typography

function buildWebSocketUrl() {
  const envWebSocketUrl = import.meta.env.VITE_WS_URL
  if (envWebSocketUrl) return envWebSocketUrl

  const url = API_BASE_URL.startsWith('http://') || API_BASE_URL.startsWith('https://')
    ? new URL(API_BASE_URL)
    : new URL(API_BASE_URL, window.location.origin)
  const protocol = url.protocol === 'https:' ? 'wss:' : 'ws:'
  const basePath = url.pathname.replace(/\/+$/, '')
  return `${protocol}//${url.host}${basePath}/ws`
}

function mergeNotification(list, item) {
  const existed = list.some((value) => value.id === item.id)
  if (existed) {
    return list.map((value) => (value.id === item.id ? { ...value, ...item } : value))
  }
  return [item, ...list]
}

function getMenuItemsByRole(role) {
  if (role === ROLES.PATIENT) {
    return [
      {
        key: ROUTES.patientBook,
        icon: <CalendarOutlined />,
        label: 'Đặt lịch khám online',
      },
      {
        key: ROUTES.patientDeposit,
        icon: <MoneyCollectOutlined />,
        label: 'Thanh toán đặt cọc',
      },
      {
        key: ROUTES.patientHistory,
        icon: <FileTextOutlined />,
        label: 'Xem lịch sử khám bệnh',
      },
      {
        key: ROUTES.patientLabResults,
        icon: <FileSearchOutlined />,
        label: 'Xem kết quả xét nghiệm',
      },
      {
        key: ROUTES.patientReviews,
        icon: <StarOutlined />,
        label: 'Đánh giá bác sĩ',
      },
    ]
  }

  if (role === ROLES.DOCTOR) {
    return [
      {
        key: ROUTES.doctorSchedule,
        icon: <CalendarOutlined />,
        label: 'Lịch khám và khám bệnh',
      },
      {
        key: ROUTES.doctorPatientHistory,
        icon: <FileTextOutlined />,
        label: 'Lịch sử khám bệnh nhân',
      },
    ]
  }

  if (role === ROLES.ADMIN) {
    return [
      { key: ROUTES.adminStatistics, icon: <BarChartOutlined />, label: 'Thống kê' },
      { key: ROUTES.adminDepositConfig, icon: <MoneyCollectOutlined />, label: 'Tiền đặt cọc khám' },
      { key: ROUTES.adminNotifications, icon: <BellOutlined />, label: 'Quản lý thông báo' },
      { key: ROUTES.adminTimeslots, icon: <ClockCircleOutlined />, label: 'Quản lý giờ khám' },
      { key: ROUTES.adminSpecialties, icon: <ClusterOutlined />, label: 'Quản lý chuyên khoa' },
      { key: ROUTES.adminDoctors, icon: <TeamOutlined />, label: 'Quản lý bác sĩ' },
      {
        key: ROUTES.adminUnits,
        icon: <AppstoreOutlined />,
        label: 'Quản lý danh mục thuốc',
      },
      {
        key: ROUTES.adminMedicines,
        icon: <MedicineBoxOutlined />,
        label: 'Quản lý thuốc',
      },
      { key: ROUTES.adminStatistics, icon: <BarChartOutlined />, label: 'Thống kê' },
    ]
  }

  return []
}

export default function PortalLayout() {
  const { message } = App.useApp()
  const { token, currentUser, role, logoutAndClear } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const bellAnimationTimeoutRef = useRef(null)
  const items = getMenuItemsByRole(role)
  const [notificationModalOpen, setNotificationModalOpen] = useState(false)
  const [notificationLoading, setNotificationLoading] = useState(false)
  const [notifications, setNotifications] = useState([])
  const [notificationPage, setNotificationPage] = useState(1)
  const [notificationPageSize, setNotificationPageSize] = useState(10)
  const [notificationTotal, setNotificationTotal] = useState(0)
  const [bellRinging, setBellRinging] = useState(false)

  const selectedKey =
    items.find((item) => location.pathname.startsWith(item.key))?.key ||
    items[0]?.key

  function handleLogout() {
    logoutAndClear()
    navigate(ROUTES.login, { replace: true })
  }

  function formatDateTime(value) {
    if (!value) return '-'
    const date = new Date(value)
    if (Number.isNaN(date.getTime())) return value
    return date.toLocaleString('vi-VN')
  }

  async function loadMyNotifications({ nextPage = 1, nextPageSize = 10 } = {}) {
    setNotificationLoading(true)
    try {
      const response = await getMyNotifications({
        page: nextPage - 1,
        size: nextPageSize,
      })
      const pageData = response?.result
      setNotifications(pageData?.content || [])
      setNotificationPage((pageData?.number ?? 0) + 1)
      setNotificationPageSize(pageData?.size || nextPageSize)
      setNotificationTotal(pageData?.totalElements || 0)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setNotificationLoading(false)
    }
  }

  function openNotificationModal() {
    setNotificationModalOpen(true)
    loadMyNotifications({ nextPage: 1, nextPageSize: notificationPageSize })
  }

  function triggerBellRing() {
    if (bellAnimationTimeoutRef.current) {
      window.clearTimeout(bellAnimationTimeoutRef.current)
    }
    setBellRinging(false)
    window.requestAnimationFrame(() => {
      setBellRinging(true)
      bellAnimationTimeoutRef.current = window.setTimeout(() => {
        setBellRinging(false)
      }, 650)
    })
  }

  useEffect(() => {
    if (!token || !role) return undefined

    const client = new Client({
      brokerURL: buildWebSocketUrl(),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 3000,
    })

    client.onStompError = (frame) => {
      console.error('STOMP broker error:', frame.headers?.message, frame.body)
    }

    client.onWebSocketError = () => {
      // Usually indicates transport/connect failure; client will auto-reconnect.
    }

    client.onConnect = () => {
      client.subscribe('/user/queue/notifications', (payload) => {
        const notification = JSON.parse(payload.body)
        setNotifications((prev) => {
          const next = mergeNotification(prev, notification)
          if (next.length !== prev.length) {
            setNotificationTotal((value) => value + 1)
            triggerBellRing()
            message.info(`Thông báo mới: ${notification.title}`)
          }
          return next
        })
      })

      client.subscribe(`/topic/notifications/role/${role}`, (payload) => {
        const notification = JSON.parse(payload.body)
        setNotifications((prev) => {
          const next = mergeNotification(prev, notification)
          if (next.length !== prev.length) {
            setNotificationTotal((value) => value + 1)
            triggerBellRing()
            message.info(`Thông báo mới: ${notification.title}`)
          }
          return next
        })
      })
    }

    client.activate()

    return () => {
      if (bellAnimationTimeoutRef.current) {
        window.clearTimeout(bellAnimationTimeoutRef.current)
      }
      client.deactivate()
    }
  }, [message, role, token])

  const namePrefix = role === ROLES.DOCTOR ? 'Bác sĩ ' : ''
  const displayName = currentUser?.fullName || currentUser?.username || ''
  const profileMenuItems = [
    { key: 'profile', label: 'Quản lý tài khoản' },
    { key: 'change-password', label: 'Đổi mật khẩu' },
  ]

  function handleProfileMenuClick({ key }) {
    if (key === 'profile') {
      navigate(ROUTES.profile)
      return
    }
    if (key === 'change-password') {
      navigate(ROUTES.changePassword)
    }
  }

  return (
    <Layout className="portal-layout">
      <Sider theme="light" width={260} breakpoint="lg" collapsedWidth={0}>
        <div className="portal-layout__brand">
          <span className="portal-layout__brand-mark">
            <img
              src="/white-1.png"
              alt="CMS logo"
              className="portal-layout__brand-logo"
            />
          </span>
          <span className="portal-layout__brand-text">CMS</span>
        </div>
        <Menu
          mode="inline"
          selectedKeys={selectedKey ? [selectedKey] : []}
          items={items}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header className="portal-layout__header">
          <Space>
            <Button
              type="text"
              aria-label="Thông báo"
              title="Thông báo"
              className={`portal-layout__notification-button${
                bellRinging ? ' portal-layout__notification-button--ringing' : ''
              }`}
              icon={<BellFilled />}
              onClick={openNotificationModal}
            />
            <Text type="secondary">Xin chào,</Text>
            <Dropdown
              trigger={['hover']}
              menu={{ items: profileMenuItems, onClick: handleProfileMenuClick }}
            >
              <Button type="link" className="portal-layout__profile-link">
                <Space size={6}>
                  {`${namePrefix}${displayName}`}
                  <DownOutlined style={{ fontSize: 12 }} />
                </Space>
              </Button>
            </Dropdown>
            <Button onClick={handleLogout}>Đăng xuất</Button>
          </Space>
        </Header>
        <Content className="portal-layout__content">
          <Outlet />
        </Content>
      </Layout>
      <Modal
        title="Thông báo của tôi"
        open={notificationModalOpen}
        onCancel={() => setNotificationModalOpen(false)}
        footer={null}
        width={640}
      >
        <Table
          rowKey="id"
          loading={notificationLoading}
          dataSource={notifications}
          columns={[
            {
              title: 'Tiêu đề',
              dataIndex: 'title',
              key: 'title',
              render: (value) => (
                <Space size={8}>
                  <BellOutlined style={{ color: '#6cc5d8' }} />
                  <span>{value}</span>
                </Space>
              ),
            },
            {
              title: 'Thời gian',
              dataIndex: 'createdAt',
              key: 'createdAt',
              width: 220,
              render: (value) => formatDateTime(value),
            },
          ]}
          locale={{ emptyText: 'Chưa có thông báo' }}
          pagination={{
            current: notificationPage,
            pageSize: notificationPageSize,
            total: notificationTotal,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50'],
            onChange: (nextPage, nextPageSize) => {
              loadMyNotifications({ nextPage, nextPageSize })
            },
            showTotal: (value) => `Tổng ${value} thông báo`,
          }}
        />
      </Modal>
    </Layout>
  )
}

