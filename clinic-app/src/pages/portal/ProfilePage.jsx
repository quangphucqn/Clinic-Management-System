import { Card, Descriptions, Space, Typography } from 'antd'
import { useAuth } from '../../hooks/useAuth.js'

const { Title } = Typography

export default function ProfilePage() {
  const { currentUser } = useAuth()

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="middle">
      <Card>
        <Title level={4}>Thông tin tài khoản</Title>
        <Descriptions column={1} bordered size="small">
          <Descriptions.Item label="Tên đăng nhập">
            {currentUser?.username || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Họ và tên">
            {currentUser?.fullName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Email">
            {currentUser?.email || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Số điện thoại">
            {currentUser?.phoneNumber || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Vai trò">
            {currentUser?.role || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {currentUser?.patientProfile ? (
        <Card>
          <Title level={5}>Hồ sơ bệnh nhân</Title>
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="Giới tính">
              {currentUser.patientProfile.gender || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Ngày sinh">
              {currentUser.patientProfile.dateOfBirth || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Địa chỉ">
              {currentUser.patientProfile.address || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Liên hệ khẩn cấp">
              {currentUser.patientProfile.emergencyContactName || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="SĐT khẩn cấp">
              {currentUser.patientProfile.emergencyContactPhone || '-'}
            </Descriptions.Item>
          </Descriptions>
        </Card>
      ) : null}

      {currentUser?.doctorProfile ? (
        <Card>
          <Title level={5}>Hồ sơ bác sĩ</Title>
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="Chuyên khoa">
              {currentUser.doctorProfile.specialtyName || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Phòng khám">
              {currentUser.doctorProfile.roomNumber || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Số năm kinh nghiệm">
              {currentUser.doctorProfile.yearsOfExperience ?? '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Số giấy phép">
              {currentUser.doctorProfile.licenseNumber || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Tiểu sử">
              {currentUser.doctorProfile.biography || '-'}
            </Descriptions.Item>
          </Descriptions>
        </Card>
      ) : null}
    </Space>
  )
}

