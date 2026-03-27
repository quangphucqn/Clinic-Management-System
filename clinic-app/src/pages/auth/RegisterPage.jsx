import {
  App,
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  Row,
  Select,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import 'dayjs/locale/vi'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ROUTES } from '../../constants/routes.js'
import { getErrorMessage } from '../../utils/httpError.js'
import { registerPatient } from '../../services/authService.js'
import './AuthPages.css'

dayjs.locale('vi')

const { Text } = Typography

const GENDER = [
  { value: 'MALE', label: 'Nam' },
  { value: 'FEMALE', label: 'Nữ' },
  { value: 'OTHER', label: 'Khác' },
]

export default function RegisterPage() {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)

  async function onFinish(values) {
    setLoading(true)
    try {
      const payload = {
        username: values.username.trim(),
        password: values.password,
        fullName: values.fullName.trim(),
        email: values.email.trim(),
        phoneNumber: values.phoneNumber?.trim() || undefined,
        gender: values.gender || undefined,
        dateOfBirth: values.dateOfBirth
          ? values.dateOfBirth.format('YYYY-MM-DD')
          : undefined,
        address: values.address?.trim() || undefined,
        emergencyContactName: values.emergencyContactName?.trim() || undefined,
        emergencyContactPhone:
          values.emergencyContactPhone?.trim() || undefined,
      }

      await registerPatient(payload)
      message.success('Đăng ký thành công. Vui lòng đăng nhập.')
      navigate(ROUTES.login, { replace: true })
    } catch (e) {
      message.error(getErrorMessage(e))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card style={{ width: '100%', maxWidth: 520 }} title="Đăng ký tài khoản bệnh nhân">
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        scrollToFirstError
        autoComplete="on"
      >
        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Form.Item
              label="Tên đăng nhập"
              name="username"
              rules={[
                { required: true, message: 'Bắt buộc' },
                { min: 4, max: 50, message: '4–50 ký tự' },
              ]}
            >
              <Input placeholder="username" autoComplete="username" />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item
              label="Email"
              name="email"
              rules={[
                { required: true, message: 'Bắt buộc' },
                { type: 'email', message: 'Email không hợp lệ' },
              ]}
            >
              <Input placeholder="you@email.com" autoComplete="email" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Form.Item
              label="Mật khẩu"
              name="password"
              rules={[
                { required: true, message: 'Bắt buộc' },
                { min: 8, max: 100, message: '8–100 ký tự' },
              ]}
            >
              <Input.Password
                placeholder="••••••••"
                autoComplete="new-password"
              />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item
              label="Nhập lại mật khẩu"
              name="confirmPassword"
              dependencies={['password']}
              rules={[
                { required: true, message: 'Bắt buộc' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve()
                    }
                    return Promise.reject(
                      new Error('Mật khẩu nhập lại không khớp'),
                    )
                  },
                }),
              ]}
            >
              <Input.Password
                placeholder="••••••••"
                autoComplete="new-password"
              />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          label="Họ và tên"
          name="fullName"
          rules={[
            { required: true, message: 'Bắt buộc' },
            { max: 100, message: 'Tối đa 100 ký tự' },
          ]}
        >
          <Input placeholder="Nguyễn Văn A" />
        </Form.Item>

        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Form.Item label="Số điện thoại" name="phoneNumber">
              <Input placeholder="0xxxxxxxxx" maxLength={15} />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item label="Giới tính" name="gender">
              <Select allowClear placeholder="Chọn" options={GENDER} />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item label="Ngày sinh" name="dateOfBirth">
          <DatePicker
            style={{ width: '100%' }}
            format="DD/MM/YYYY"
            disabledDate={(current) =>
              current && current > dayjs().endOf('day')
            }
          />
        </Form.Item>

        <Form.Item label="Địa chỉ" name="address">
          <Input.TextArea rows={2} placeholder="Tùy chọn" maxLength={255} />
        </Form.Item>

        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Form.Item
              label="Liên hệ khẩn cấp (tên)"
              name="emergencyContactName"
            >
              <Input maxLength={100} />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item
              label="Liên hệ khẩn cấp (SĐT)"
              name="emergencyContactPhone"
            >
              <Input maxLength={15} />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            block
            size="large"
            className="auth-page__button"
            loading={loading}
          >
            Đăng ký
          </Button>
        </Form.Item>
      </Form>
      <Text type="secondary">
        Đã có tài khoản? <Link to={ROUTES.login}>Đăng nhập</Link>
      </Text>
    </Card>
  )
}
