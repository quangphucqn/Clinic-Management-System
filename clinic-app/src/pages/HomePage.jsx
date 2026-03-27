import { Button, Card, Col, Row, Space, Typography } from 'antd'
import {
  HeartOutlined,
  MedicineBoxOutlined,
  PlusCircleOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { ROUTES } from '../constants/routes.js'
import { useAuth } from '../hooks/useAuth.js'
import { getDefaultRouteByRole } from '../utils/authRouting.js'
import './HomePage.css'

const { Title, Paragraph } = Typography

export default function HomePage() {
  const navigate = useNavigate()
  const { role, isAuthenticated, logoutAndClear } = useAuth()

  function handleLogout() {
    logoutAndClear()
    navigate(ROUTES.login, { replace: true })
  }

  return (
    <main className="home-page">
      <section className="hero">
        <div className="hero__topbar">
          <div className="hero__brand">
            <img
              src="/white-1.png"
              alt="Clinic logo"
              className="hero__logo"
            />
            <span className="hero__brand-text">CMS</span>
          </div>
          {!isAuthenticated ? (
            <Space>
              <Button type="text" className="hero__ghost-btn">
                Liên hệ
              </Button>
              <Button
                className="hero__portal-btn"
                onClick={() => navigate(ROUTES.login)}
              >
                Đăng nhập
              </Button>
            </Space>
          ) : (
            <Button danger onClick={handleLogout}>
              Đăng xuất
            </Button>
          )}
        </div>

        <div className="hero__content">
          <Title className="hero__title">Clinic Management System</Title>
          <Paragraph className="hero__subtitle">
            Chăm sóc sức khỏe tận tâm cho cả gia đình bạn.
          </Paragraph>
          <Button
            type="primary"
            size="large"
            className="hero__cta"
            onClick={() =>
              navigate(
                isAuthenticated ? getDefaultRouteByRole(role) : ROUTES.login,
              )
            }
          >
            Đặt khám ngay
          </Button>
        </div>
      </section>

      <section className="services">
        <Row gutter={[16, 16]}>
          <Col xs={24} md={8}>
            <Card className="services__card">
              <div className="services__icon">
                <MedicineBoxOutlined />
              </div>
              <Title level={5}>Khám tổng quát</Title>
              <Paragraph>Đánh giá sức khỏe toàn diện định kỳ.</Paragraph>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card className="services__card">
              <div className="services__icon">
                <PlusCircleOutlined />
              </div>
              <Title level={5}>Khám chuyên khoa</Title>
              <Paragraph>Đội ngũ bác sĩ giàu kinh nghiệm theo chuyên ngành.</Paragraph>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card className="services__card">
              <div className="services__icon">
                <HeartOutlined />
              </div>
              <Title level={5}>Dự phòng và theo dõi</Title>
              <Paragraph>Lộ trình chăm sóc sức khỏe liên tục cho gia đình.</Paragraph>
            </Card>
          </Col>
        </Row>
      </section>
    </main>
  )
}
