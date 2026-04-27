import {
  App,
  Button,
  Card,
  DatePicker,
  Descriptions,
  Form,
  Input,
  Modal,
  Select,
  Space,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useState } from 'react'
import { updateMyDoctorProfile } from '../../services/doctorService.js'
import { updateMyPatientProfile } from '../../services/patientService.js'
import { getErrorMessage } from '../../utils/httpError.js'
import { useAuth } from '../../hooks/useAuth.js'

const { Title } = Typography
const GENDER_OPTIONS = [
  { value: 'MALE', label: 'Nam' },
  { value: 'FEMALE', label: 'Nữ' },
  { value: 'OTHER', label: 'Khác' },
]

export default function ProfilePage() {
  const { message } = App.useApp()
  const { currentUser, refreshCurrentUser } = useAuth()
  const [patientForm] = Form.useForm()
  const [doctorForm] = Form.useForm()
  const [editPatientProfileOpen, setEditPatientProfileOpen] = useState(false)
  const [editDoctorProfileOpen, setEditDoctorProfileOpen] = useState(false)
  const [savingPatientProfile, setSavingPatientProfile] = useState(false)
  const [savingDoctorProfile, setSavingDoctorProfile] = useState(false)

  function openPatientProfileModal() {
    patientForm.setFieldsValue({
      email: currentUser?.email || '',
      phoneNumber: currentUser?.phoneNumber || '',
      gender: currentUser?.patientProfile?.gender || undefined,
      dateOfBirth: currentUser?.patientProfile?.dateOfBirth
        ? dayjs(currentUser.patientProfile.dateOfBirth)
        : null,
      address: currentUser?.patientProfile?.address || '',
      emergencyContactName: currentUser?.patientProfile?.emergencyContactName || '',
      emergencyContactPhone: currentUser?.patientProfile?.emergencyContactPhone || '',
    })
    setEditPatientProfileOpen(true)
  }

  function openDoctorProfileModal() {
    doctorForm.setFieldsValue({
      email: currentUser?.email || '',
      phoneNumber: currentUser?.phoneNumber || '',
      licenseNumber: currentUser?.doctorProfile?.licenseNumber || '',
      biography: currentUser?.doctorProfile?.biography || '',
    })
    setEditDoctorProfileOpen(true)
  }

  async function handleUpdateDoctorProfile() {
    try {
      const values = await doctorForm.validateFields()
      setSavingDoctorProfile(true)
      await updateMyDoctorProfile({
        email: values.email?.trim() || undefined,
        phoneNumber: values.phoneNumber?.trim() || undefined,
        licenseNumber: values.licenseNumber?.trim() || undefined,
        biography: values.biography?.trim() || undefined,
      })
      await refreshCurrentUser()
      message.success('Cập nhật hồ sơ bác sĩ thành công')
      setEditDoctorProfileOpen(false)
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSavingDoctorProfile(false)
    }
  }

  async function handleUpdatePatientProfile() {
    try {
      const values = await patientForm.validateFields()
      setSavingPatientProfile(true)
      await updateMyPatientProfile({
        email: values.email?.trim() || undefined,
        phoneNumber: values.phoneNumber?.trim() || undefined,
        gender: values.gender || undefined,
        dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : undefined,
        address: values.address?.trim() || undefined,
        emergencyContactName: values.emergencyContactName?.trim() || undefined,
        emergencyContactPhone: values.emergencyContactPhone?.trim() || undefined,
      })
      await refreshCurrentUser()
      message.success('Cập nhật hồ sơ bệnh nhân thành công')
      setEditPatientProfileOpen(false)
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSavingPatientProfile(false)
    }
  }

  return (
    <Space orientation="vertical" style={{ width: '100%' }} size="middle">
      <Card>
        <Space
          style={{ width: '100%', justifyContent: 'space-between', marginBottom: 8 }}
          align="center"
        >
          <Title level={4} style={{ margin: 0 }}>
            Thông tin cá nhân
          </Title>
          {currentUser?.patientProfile ? (
            <Button type="primary" onClick={openPatientProfileModal}>
              Cập nhật thông tin
            </Button>
          ) : null}
          {currentUser?.doctorProfile ? (
            <Button type="primary" onClick={openDoctorProfileModal}>
              Cập nhật thông tin
            </Button>
          ) : null}
        </Space>

        <Descriptions column={1} bordered size="small">
          <Descriptions.Item label="Họ và tên">{currentUser?.fullName || '-'}</Descriptions.Item>
          <Descriptions.Item label="Email">{currentUser?.email || '-'}</Descriptions.Item>
          <Descriptions.Item label="Số điện thoại">{currentUser?.phoneNumber || '-'}</Descriptions.Item>

          {currentUser?.patientProfile ? (
            <>
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
            </>
          ) : null}

          {currentUser?.doctorProfile ? (
            <>
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
            </>
          ) : null}
        </Descriptions>
      </Card>

      <Modal
        title="Cập nhật hồ sơ bệnh nhân"
        open={editPatientProfileOpen}
        onCancel={() => setEditPatientProfileOpen(false)}
        onOk={handleUpdatePatientProfile}
        confirmLoading={savingPatientProfile}
        okText="Cập nhật"
        cancelText="Hủy"
        forceRender
        destroyOnHidden
      >
        <Form form={patientForm} layout="vertical">
          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: 'Vui lòng nhập email' },
              { type: 'email', message: 'Email không hợp lệ' },
              { max: 100, message: 'Tối đa 100 ký tự' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Số điện thoại"
            name="phoneNumber"
            rules={[{ max: 15, message: 'Tối đa 15 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Giới tính" name="gender">
            <Select allowClear options={GENDER_OPTIONS} placeholder="Chọn giới tính" />
          </Form.Item>
          <Form.Item label="Ngày sinh" name="dateOfBirth">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item
            label="Địa chỉ"
            name="address"
            rules={[{ max: 255, message: 'Tối đa 255 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Liên hệ khẩn cấp"
            name="emergencyContactName"
            rules={[{ max: 100, message: 'Tối đa 100 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="SĐT khẩn cấp"
            name="emergencyContactPhone"
            rules={[{ max: 15, message: 'Tối đa 15 ký tự' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Cập nhật hồ sơ bác sĩ"
        open={editDoctorProfileOpen}
        onCancel={() => setEditDoctorProfileOpen(false)}
        onOk={handleUpdateDoctorProfile}
        confirmLoading={savingDoctorProfile}
        okText="Cập nhật"
        cancelText="Hủy"
        forceRender
        destroyOnHidden
      >
        <Form form={doctorForm} layout="vertical">
          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: 'Vui lòng nhập email' },
              { type: 'email', message: 'Email không hợp lệ' },
              { max: 100, message: 'Tối đa 100 ký tự' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Số điện thoại"
            name="phoneNumber"
            rules={[{ max: 15, message: 'Tối đa 15 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Số giấy phép"
            name="licenseNumber"
            rules={[{ max: 50, message: 'Tối đa 50 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="Tiểu sử" name="biography">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  )
}

