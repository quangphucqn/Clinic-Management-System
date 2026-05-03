import {
  App,
  Button,
  Card,
  Descriptions,
  Drawer,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Space,
  Spin,
  Table,
  Typography,
} from 'antd'
import { useEffect, useState } from 'react'
import {
  createDoctor,
  deleteDoctor,
  getDoctorById,
  getDoctors,
  updateDoctor,
} from '../../../services/doctorService.js'
import { getSpecialties } from '../../../services/specialtyService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title } = Typography

export default function DoctorManagementPage() {
  const { message } = App.useApp()
  const [updateForm] = Form.useForm()
  const [createForm] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [detailLoading, setDetailLoading] = useState(false)
  const [doctors, setDoctors] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [keyword, setKeyword] = useState('')
  const [debouncedKeyword, setDebouncedKeyword] = useState('')
  const [filterSpecialtyId, setFilterSpecialtyId] = useState(undefined)
  const [selectedDoctor, setSelectedDoctor] = useState(null)
  const [updateModalOpen, setUpdateModalOpen] = useState(false)
  const [createModalOpen, setCreateModalOpen] = useState(false)
  const [specialtyOptions, setSpecialtyOptions] = useState([])
  const [specialtyLoading, setSpecialtyLoading] = useState(false)

  async function loadDoctors({
    nextPage = page,
    nextPageSize = pageSize,
    nextKeyword = debouncedKeyword,
    nextSpecialtyId = filterSpecialtyId,
  } = {}) {
    setLoading(true)
    try {
      const response = await getDoctors({
        page: nextPage - 1,
        size: nextPageSize,
        keyword: nextKeyword || undefined,
        specialtyId: nextSpecialtyId || undefined,
      })
      const pageData = response?.result
      setDoctors(pageData?.content || [])
      setTotal(pageData?.totalElements || 0)
      setPage((pageData?.number ?? 0) + 1)
      setPageSize(pageData?.size || nextPageSize)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  async function loadSpecialties() {
    if (specialtyLoading || specialtyOptions.length > 0) return
    setSpecialtyLoading(true)
    try {
      const response = await getSpecialties({ page: 0, size: 200 })
      const items = response?.result?.content || []
      setSpecialtyOptions(
        items.map((item) => ({ label: item.name, value: item.id })),
      )
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setSpecialtyLoading(false)
    }
  }

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedKeyword(keyword.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [keyword])

  useEffect(() => {
    setPage(1)
    loadDoctors({
      nextPage: 1,
      nextPageSize: pageSize,
      nextKeyword: debouncedKeyword,
      nextSpecialtyId: filterSpecialtyId,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedKeyword, filterSpecialtyId])

  useEffect(() => {
    loadSpecialties()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function openDoctorDetail(doctorId) {
    setDetailLoading(true)
    try {
      const response = await getDoctorById(doctorId)
      setSelectedDoctor(response?.result || null)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDetailLoading(false)
    }
  }

  function openUpdateModal() {
    if (!selectedDoctor) return
    updateForm.setFieldsValue({
      specialtyId: selectedDoctor.specialtyId,
      licenseNumber: selectedDoctor.licenseNumber,
      roomNumber: selectedDoctor.roomNumber,
      yearsOfExperience: selectedDoctor.yearsOfExperience,
      biography: selectedDoctor.biography,
    })
    setUpdateModalOpen(true)
    loadSpecialties()
  }

  function openCreateModal() {
    createForm.resetFields()
    createForm.setFieldsValue({ yearsOfExperience: 0 })
    setCreateModalOpen(true)
    loadSpecialties()
  }

  async function handleUpdateDoctor() {
    if (!selectedDoctor) return
    try {
      const values = await updateForm.validateFields()
      setSaving(true)
      const payload = {
        specialtyId: values.specialtyId || undefined,
        licenseNumber: values.licenseNumber?.trim() || undefined,
        roomNumber: values.roomNumber?.trim() || undefined,
        yearsOfExperience: values.yearsOfExperience,
        biography: values.biography?.trim() || undefined,
      }
      await updateDoctor(selectedDoctor.id, payload)
      message.success('Cập nhật bác sĩ thành công')
      setUpdateModalOpen(false)
      await openDoctorDetail(selectedDoctor.id)
      await loadDoctors()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleCreateDoctor() {
    try {
      const values = await createForm.validateFields()
      setSaving(true)
      const payload = {
        username: values.username.trim(),
        password: values.password,
        fullName: values.fullName.trim(),
        email: values.email.trim(),
        phoneNumber: values.phoneNumber?.trim() || undefined,
        specialtyId: values.specialtyId,
        licenseNumber: values.licenseNumber.trim(),
        roomNumber: values.roomNumber?.trim() || undefined,
        yearsOfExperience: values.yearsOfExperience,
        biography: values.biography?.trim() || undefined,
      }
      await createDoctor(payload)
      message.success('Thêm bác sĩ thành công')
      setCreateModalOpen(false)
      await loadDoctors({ nextPage: 1, nextPageSize: pageSize })
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDeleteDoctor() {
    if (!selectedDoctor) return
    setDeleting(true)
    try {
      await deleteDoctor(selectedDoctor.id)
      message.success('Xóa bác sĩ thành công')
      setSelectedDoctor(null)
      await loadDoctors()
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDeleting(false)
    }
  }

  const columns = [
    {
      title: 'Họ tên',
      dataIndex: 'fullName',
      key: 'fullName',
      render: (_, record) => (
        <Button
          type="link"
          style={{ paddingInline: 0 }}
          onClick={() => openDoctorDetail(record.id)}
        >
          {record.fullName}
        </Button>
      ),
    },
    {
      title: 'Phòng khám',
      dataIndex: 'roomNumber',
      key: 'roomNumber',
      width: 140,
      render: (value) => value || '-',
    },
    {
      title: 'Chuyên khoa',
      dataIndex: 'specialtyName',
      key: 'specialtyName',
    },
    {
      title: 'Chứng chỉ hành nghề',
      dataIndex: 'licenseNumber',
      key: 'licenseNumber',
    },
  ]

  return (
    <Card>
      <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
        <div
          style={{
            width: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: 12,
          }}
        >
          <Title level={3} style={{ margin: 0 }}>
            Quản lý bác sĩ
          </Title>
          <Space size="small" style={{ marginLeft: 'auto' }}>
            <Input
              placeholder="Tìm theo tên, chứng chỉ hành nghề..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              allowClear
              style={{ width: 280 }}
            />
            <Select
              placeholder="Lọc theo chuyên khoa"
              allowClear
              showSearch
              optionFilterProp="label"
              options={specialtyOptions}
              loading={specialtyLoading}
              value={filterSpecialtyId}
              onChange={(value) => setFilterSpecialtyId(value)}
              style={{ width: 240 }}
            />
            <Button type="primary" onClick={openCreateModal}>
              Thêm bác sĩ
            </Button>
          </Space>
        </div>

        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={doctors}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (value) => `Tổng ${value} bác sĩ`,
          }}
          onChange={(pagination) => {
            const nextPage = pagination.current || 1
            const nextPageSize = pagination.pageSize || 10
            setPage(nextPage)
            setPageSize(nextPageSize)
            loadDoctors({
              nextPage,
              nextPageSize,
              nextSpecialtyId: filterSpecialtyId,
            })
          }}
        />
      </Space>

      <Drawer
        title={selectedDoctor ? `Chi tiết bác sĩ: ${selectedDoctor.fullName}` : 'Chi tiết bác sĩ'}
        size="large"
        open={Boolean(selectedDoctor)}
        onClose={() => setSelectedDoctor(null)}
        extra={
          <Space>
            <Button type="primary" onClick={openUpdateModal} disabled={!selectedDoctor}>
              Cập nhật thông tin
            </Button>
            <Popconfirm
              title="Xóa bác sĩ"
              description="Bạn chắc chắn muốn xóa bác sĩ này?"
              okText="Xóa"
              cancelText="Hủy"
              onConfirm={handleDeleteDoctor}
            >
              <Button danger loading={deleting} disabled={!selectedDoctor}>
                Xóa bác sĩ
              </Button>
            </Popconfirm>
          </Space>
        }
      >
        {detailLoading ? (
          <div style={{ display: 'grid', placeItems: 'center', minHeight: 180 }}>
            <Spin />
          </div>
        ) : selectedDoctor ? (
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="Username">{selectedDoctor.username}</Descriptions.Item>
            <Descriptions.Item label="Họ tên">{selectedDoctor.fullName}</Descriptions.Item>
            <Descriptions.Item label="Email">{selectedDoctor.email}</Descriptions.Item>
            <Descriptions.Item label="Số điện thoại">{selectedDoctor.phoneNumber}</Descriptions.Item>
            <Descriptions.Item label="Chuyên khoa">{selectedDoctor.specialtyName}</Descriptions.Item>
            <Descriptions.Item label="Số giấy phép">{selectedDoctor.licenseNumber || '-'}</Descriptions.Item>
            <Descriptions.Item label="Phòng khám">{selectedDoctor.roomNumber || '-'}</Descriptions.Item>
            <Descriptions.Item label="Kinh nghiệm">{selectedDoctor.yearsOfExperience ?? '-'}</Descriptions.Item>
            <Descriptions.Item label="Tiểu sử">{selectedDoctor.biography || '-'}</Descriptions.Item>
          </Descriptions>
        ) : null}
      </Drawer>

      <Modal
        title="Cập nhật thông tin bác sĩ"
        open={updateModalOpen}
        onCancel={() => setUpdateModalOpen(false)}
        onOk={handleUpdateDoctor}
        confirmLoading={saving}
        okText="Cập nhật"
        cancelText="Hủy"
        zIndex={1300}
        forceRender
        destroyOnHidden
      >
        <Form form={updateForm} layout="vertical">
          <Form.Item label="Chuyên khoa" name="specialtyId">
            <Select
              placeholder="Chọn chuyên khoa"
              options={specialtyOptions}
              loading={specialtyLoading}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item
            label="Số giấy phép"
            name="licenseNumber"
            rules={[{ max: 50, message: 'Tối đa 50 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Phòng khám"
            name="roomNumber"
            rules={[{ max: 20, message: 'Tối đa 20 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Số năm kinh nghiệm"
            name="yearsOfExperience"
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={0} />
          </Form.Item>
          <Form.Item label="Tiểu sử" name="biography">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Thêm bác sĩ"
        open={createModalOpen}
        onCancel={() => setCreateModalOpen(false)}
        onOk={handleCreateDoctor}
        confirmLoading={saving}
        okText="Thêm mới"
        cancelText="Hủy"
        zIndex={1300}
        forceRender
        destroyOnHidden
      >
        <Form form={createForm} layout="vertical">
          <Form.Item
            label="Tên đăng nhập"
            name="username"
            rules={[
              { required: true, message: 'Vui lòng nhập tên đăng nhập' },
              { min: 4, max: 50, message: '4-50 ký tự' },
            ]}
          >
            <Input placeholder="doctor_a" />
          </Form.Item>
          <Form.Item
            label="Mật khẩu"
            name="password"
            rules={[
              { required: true, message: 'Vui lòng nhập mật khẩu' },
              { min: 8, max: 100, message: '8-100 ký tự' },
            ]}
          >
            <Input.Password placeholder="********" />
          </Form.Item>
          <Form.Item
            label="Họ tên"
            name="fullName"
            rules={[
              { required: true, message: 'Vui lòng nhập họ tên' },
              { max: 100, message: 'Tối đa 100 ký tự' },
            ]}
          >
            <Input placeholder="Nguyen Van A" />
          </Form.Item>
          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: 'Vui lòng nhập email' },
              { type: 'email', message: 'Email không hợp lệ' },
            ]}
          >
            <Input placeholder="doctor.a@mail.com" />
          </Form.Item>
          <Form.Item
            label="Số điện thoại"
            name="phoneNumber"
            rules={[{ max: 15, message: 'Tối đa 15 ký tự' }]}
          >
            <Input placeholder="0901234567" />
          </Form.Item>
          <Form.Item
            label="Chuyên khoa"
            name="specialtyId"
            rules={[{ required: true, message: 'Vui lòng chọn chuyên khoa' }]}
          >
            <Select
              placeholder="Chọn chuyên khoa"
              options={specialtyOptions}
              loading={specialtyLoading}
              showSearch
              optionFilterProp="label"
            />
          </Form.Item>
          <Form.Item
            label="Số giấy phép"
            name="licenseNumber"
            rules={[
              { required: true, message: 'Vui lòng nhập số giấy phép' },
              { max: 50, message: 'Tối đa 50 ký tự' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Phòng khám"
            name="roomNumber"
            rules={[{ max: 20, message: 'Tối đa 20 ký tự' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Số năm kinh nghiệm"
            name="yearsOfExperience"
            rules={[{ required: true, message: 'Vui lòng nhập số năm kinh nghiệm' }]}
          >
            <InputNumber style={{ width: '100%' }} min={0} precision={0} />
          </Form.Item>
          <Form.Item label="Tiểu sử" name="biography">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}

