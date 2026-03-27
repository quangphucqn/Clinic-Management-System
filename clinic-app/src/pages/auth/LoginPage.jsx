import { App, Button, Card, Form, Input, Typography } from 'antd'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth.js'
import { ROUTES } from '../../constants/routes.js'
import { getErrorMessage } from '../../utils/httpError.js'
import { getDefaultRouteByRole } from '../../utils/authRouting.js'
import './AuthPages.css'

const { Text } = Typography

export default function LoginPage() {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const { loginAndLoadProfile } = useAuth()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)

  async function onFinish(values) {
    setLoading(true)
    try {
      const profile = await loginAndLoadProfile(values)
      message.success('Đăng nhập thành công')
      navigate(getDefaultRouteByRole(profile?.role), { replace: true })
    } catch (e) {
      /** @type {{ response?: { status?: number, data?: { code?: number, message?: string } } }} */
      const errorResponse = e
      const status = errorResponse?.response?.status
      const apiCode = errorResponse?.response?.data?.code
      const apiMessage = errorResponse?.response?.data?.message

      if (
        status === 401 ||
        apiCode === 401 ||
        apiMessage === 'Unauthenticated'
      ) {
        message.error('Tên đăng nhập hoặc mật khẩu không đúng')
      } else {
        message.error(getErrorMessage(e))
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card style={{ width: '100%', maxWidth: 400 }} title="Đăng nhập">
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        requiredMark="optional"
        autoComplete="on"
      >
        <Form.Item
          label="Tên đăng nhập"
          name="username"
          rules={[{ required: true, message: 'Nhập tên đăng nhập' }]}
        >
          <Input size="large" placeholder="username" autoComplete="username" />
        </Form.Item>
        <Form.Item
          label="Mật khẩu"
          name="password"
          rules={[{ required: true, message: 'Nhập mật khẩu' }]}
        >
          <Input.Password
            size="large"
            placeholder="••••••••"
            autoComplete="current-password"
          />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            block
            size="large"
            className="auth-page__button"
            loading={loading}
          >
            Đăng nhập
          </Button>
        </Form.Item>
      </Form>
      <Text type="secondary">
        Chưa có tài khoản? <Link to={ROUTES.register}>Đăng ký</Link>
      </Text>
    </Card>
  )
}
