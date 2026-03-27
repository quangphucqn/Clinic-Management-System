import { Layout, Typography } from 'antd'
import {
  HeartOutlined,
  MedicineBoxOutlined,
  PlusCircleOutlined,
} from '@ant-design/icons'
import { Outlet } from 'react-router-dom'
import './AuthLayout.css'

const { Content } = Layout
const { Title, Text } = Typography

export default function AuthLayout() {
  return (
    <Layout className="auth-layout">
      <div className="auth-layout__bg-icon auth-layout__bg-icon--plus">
        <PlusCircleOutlined />
      </div>
      <div className="auth-layout__bg-icon auth-layout__bg-icon--heart">
        <HeartOutlined />
      </div>
      <div className="auth-layout__bg-icon auth-layout__bg-icon--medical">
        <MedicineBoxOutlined />
      </div>

      <Content className="auth-layout__content">
        <div className="auth-layout__header">
          <Title level={2} style={{ marginBottom: 8, color: '#f7fbfd' }}>
            Hệ Thống Quản Lý Phòng Khám
          </Title>
          <Text style={{ color: '#e8f6fb' }}>Đặt khám trực tuyến</Text>
        </div>
        <Outlet />
      </Content>
    </Layout>
  )
}
