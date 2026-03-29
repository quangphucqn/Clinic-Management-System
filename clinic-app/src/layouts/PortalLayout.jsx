import {
  AppstoreOutlined,
  BellOutlined,
  CalendarOutlined,
  ClusterOutlined,
  ClockCircleOutlined,
  FileTextOutlined,
  MedicineBoxOutlined,
  TeamOutlined,
} from '@ant-design/icons'
import { Button, Layout, Menu, Space, Typography } from 'antd'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { ROLES } from '../constants/roles.js'
import { ROUTES } from '../constants/routes.js'
import { useAuth } from '../hooks/useAuth.js'
import './PortalLayout.css'

const { Header, Sider, Content } = Layout
const { Text } = Typography

function getMenuItemsByRole(role) {
  if (role === ROLES.PATIENT) {
    return [
      {
        key: ROUTES.patientBook,
        icon: <CalendarOutlined />,
        label: 'Đặt khám',
      },
      {
        key: ROUTES.patientAppointments,
        icon: <ClockCircleOutlined />,
        label: 'Lịch khám',
      },
      {
        key: ROUTES.patientHistory,
        icon: <FileTextOutlined />,
        label: 'Lịch sử khám',
      },
    ]
  }

  if (role === ROLES.DOCTOR) {
    return [
      {
        key: ROUTES.doctorSchedule,
        icon: <CalendarOutlined />,
        label: 'Lịch khám bác sĩ',
      },
    ]
  }

  if (role === ROLES.ADMIN) {
    return [
      { key: ROUTES.adminTimeslots, icon: <ClockCircleOutlined />, label: 'Quản lý giờ khám' },
      { key: ROUTES.adminSpecialties, icon: <ClusterOutlined />, label: 'Quản lý chuyên khoa' },
      { key: ROUTES.adminDoctors, icon: <TeamOutlined />, label: 'Quản lý bác sĩ' },
      { key: ROUTES.adminNotifications, icon: <BellOutlined />, label: 'Quản lý thông báo' },
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
    ]
  }

  return []
}

export default function PortalLayout() {
  const { currentUser, role, logoutAndClear } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const items = getMenuItemsByRole(role)

  const selectedKey =
    items.find((item) => location.pathname.startsWith(item.key))?.key ||
    items[0]?.key

  function handleLogout() {
    logoutAndClear()
    navigate(ROUTES.login, { replace: true })
  }

  const namePrefix = role === ROLES.DOCTOR ? 'Bác sĩ ' : ''
  const displayName = currentUser?.fullName || currentUser?.username || ''

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
            <Text type="secondary">Xin chào,</Text>
            <Button
              type="link"
              className="portal-layout__profile-link"
              onClick={() => navigate(ROUTES.profile)}
            >
              {`${namePrefix}${displayName}`}
            </Button>
            <Button onClick={handleLogout}>Đăng xuất</Button>
          </Space>
        </Header>
        <Content className="portal-layout__content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

