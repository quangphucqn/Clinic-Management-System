import { App, Button, Card, Form, Input, Space, Typography } from 'antd'
import { changeMyPassword } from '../../services/userService.js'
import { getErrorMessage } from '../../utils/httpError.js'

const { Title, Text } = Typography

export default function ChangePasswordPage() {
  const [form] = Form.useForm()
  const { message } = App.useApp()

  async function handleSubmit() {
    try {
      const values = await form.validateFields()
      await changeMyPassword({
        currentPassword: values.currentPassword,
        newPassword: values.newPassword,
      })
      message.success('Đổi mật khẩu thành công')
      form.resetFields()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    }
  }

  return (
    <Card
      style={{ width: '100%', minHeight: 'calc(100vh - 140px)' }}
      styles={{ body: { maxWidth: 640, margin: '0 auto' } }}
    >
      <div style={{ width: '100%', display: 'flex', justifyContent: 'center' }}>
        <Space orientation="vertical" size="middle" style={{ width: '100%', maxWidth: 640 }}>
          <div>
            <Title level={4} style={{ marginBottom: 4 }}>
              Đổi mật khẩu
            </Title>
            <Text type="secondary">
              Mật khẩu mới phải có độ dài từ 8 đến 100 ký tự.
            </Text>
          </div>

          <Form form={form} layout="vertical">
            <Form.Item
              label="Mật khẩu hiện tại"
              name="currentPassword"
              rules={[
                { required: true, message: 'Vui lòng nhập mật khẩu hiện tại' },
                { min: 8, max: 100, message: 'Độ dài 8-100 ký tự' },
              ]}
            >
              <Input.Password placeholder="Nhập mật khẩu hiện tại" />
            </Form.Item>

            <Form.Item
              label="Mật khẩu mới"
              name="newPassword"
              rules={[
                { required: true, message: 'Vui lòng nhập mật khẩu mới' },
                { min: 8, max: 100, message: 'Độ dài 8-100 ký tự' },
              ]}
            >
              <Input.Password placeholder="Nhập mật khẩu mới" />
            </Form.Item>

            <Form.Item
              label="Xác nhận mật khẩu mới"
              name="confirmPassword"
              dependencies={['newPassword']}
              rules={[
                { required: true, message: 'Vui lòng xác nhận mật khẩu mới' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve()
                    }
                    return Promise.reject(new Error('Mật khẩu xác nhận không khớp'))
                  },
                }),
              ]}
            >
              <Input.Password placeholder="Nhập lại mật khẩu mới" />
            </Form.Item>

            <Form.Item style={{ marginBottom: 0 }}>
              <Button type="primary" onClick={handleSubmit}>
                Cập nhật mật khẩu
              </Button>
            </Form.Item>
          </Form>
        </Space>
      </div>
    </Card>
  )
}

