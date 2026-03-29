import {
  App,
  Button,
  Card,
  Col,
  Descriptions,
  Drawer,
  Form,
  Input,
  InputNumber,
  Modal,
  Pagination,
  Popconfirm,
  Row,
  Select,
  Space,
  Spin,
  Tag,
  Typography,
  Upload,
} from 'antd'
import { useEffect, useMemo, useState } from 'react'
import {
  createMedicine,
  deleteMedicine,
  getMedicines,
  updateMedicine,
} from '../../../services/medicineService.js'
import { getUnits } from '../../../services/unitService.js'
import { getErrorMessage } from '../../../utils/httpError.js'
import './MedicineManagementPage.css'

const { Title, Text, Paragraph } = Typography

function formatCurrency(value) {
  if (value == null) return '-'
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(value)
}

function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('vi-VN')
}

export default function MedicineManagementPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState(null)
  const [medicines, setMedicines] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [searchName, setSearchName] = useState('')
  const [debouncedName, setDebouncedName] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editingMedicine, setEditingMedicine] = useState(null)
  const [selectedMedicine, setSelectedMedicine] = useState(null)
  const [unitOptions, setUnitOptions] = useState([])
  const [unitLoading, setUnitLoading] = useState(false)
  const [unitPage, setUnitPage] = useState(1)
  const [unitHasMore, setUnitHasMore] = useState(true)
  const [unitKeyword, setUnitKeyword] = useState('')
  const [debouncedUnitKeyword, setDebouncedUnitKeyword] = useState('')
  const [uploadFileList, setUploadFileList] = useState([])

  async function loadMedicines({
    nextPage = page,
    nextPageSize = pageSize,
    name = debouncedName,
  } = {}) {
    setLoading(true)
    try {
      const response = await getMedicines({
        page: nextPage - 1,
        size: nextPageSize,
        name: name || undefined,
      })
      const pageData = response?.result
      setMedicines(pageData?.content || [])
      setTotal(pageData?.totalElements || 0)
      setPage((pageData?.number ?? 0) + 1)
      setPageSize(pageData?.size || nextPageSize)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedName(searchName.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [searchName])

  useEffect(() => {
    setPage(1)
    loadMedicines({ nextPage: 1, nextPageSize: pageSize, name: debouncedName })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedName])

  async function loadUnitOptions({
    nextPage = 1,
    append = false,
    keyword = unitKeyword,
  } = {}) {
    if (unitLoading) return
    setUnitLoading(true)
    try {
      const response = await getUnits({
        page: nextPage - 1,
        size: 20,
        name: keyword || undefined,
      })
      const pageData = response?.result
      const items = pageData?.content || []
      const mapped = items.map((item) => ({
        label: item.name,
        value: item.name,
      }))

      if (append) {
        const seen = new Set(unitOptions.map((item) => item.value))
        const merged = [...unitOptions]
        mapped.forEach((item) => {
          if (!seen.has(item.value)) merged.push(item)
        })
        setUnitOptions(merged)
      } else {
        setUnitOptions(mapped)
      }

      setUnitPage((pageData?.number ?? 0) + 1)
      setUnitHasMore(!pageData?.last)
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setUnitLoading(false)
    }
  }

  function handleUnitSearch(value) {
    setUnitKeyword(value)
    setUnitPage(1)
    setUnitHasMore(true)
    setUnitOptions([])
  }

  function handleUnitPopupScroll(event) {
    const target = event.target
    if (!target || unitLoading || !unitHasMore) return
    const nearBottom =
      target.scrollTop + target.clientHeight >= target.scrollHeight - 24
    if (nearBottom) {
      loadUnitOptions({
        nextPage: unitPage + 1,
        append: true,
        keyword: unitKeyword,
      })
    }
  }

  function handleUnitDropdownOpen(open) {
    if (!open) return
    if (unitOptions.length === 0) {
      loadUnitOptions({ nextPage: 1, append: false, keyword: unitKeyword })
    }
  }

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedUnitKeyword(unitKeyword.trim())
    }, 500)
    return () => window.clearTimeout(timer)
  }, [unitKeyword])

  useEffect(() => {
    if (!modalOpen) return
    loadUnitOptions({
      nextPage: 1,
      append: false,
      keyword: debouncedUnitKeyword,
    })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedUnitKeyword, modalOpen])

  function openCreateModal() {
    setEditingMedicine(null)
    form.resetFields()
    setUploadFileList([])
    setUnitKeyword('')
    setUnitPage(1)
    setUnitHasMore(true)
    setUnitOptions([])
    setDebouncedUnitKeyword('')
    setModalOpen(true)
  }

  function openEditModal(medicine) {
    setEditingMedicine(medicine)
    form.setFieldsValue({
      code: medicine.code,
      name: medicine.name,
      unitName: medicine.unitName,
      ingredient: medicine.ingredient,
      manufacturer: medicine.manufacturer,
      price: medicine.price,
      stockQuantity: medicine.stockQuantity,
      description: medicine.description,
    })
    setUploadFileList([])
    setUnitKeyword('')
    setUnitPage(1)
    setUnitHasMore(true)
    setUnitOptions([])
    setDebouncedUnitKeyword('')
    setModalOpen(true)
  }

  async function handleSave() {
    try {
      const values = await form.validateFields()
      setSaving(true)

      const payload = {
        name: values.name?.trim(),
        unitName: values.unitName?.trim(),
        ingredient: values.ingredient?.trim() || null,
        manufacturer: values.manufacturer?.trim() || null,
        price: values.price,
        stockQuantity: values.stockQuantity,
        description: values.description?.trim() || null,
      }

      const selectedFile = uploadFileList[0]?.originFileObj

      if (editingMedicine) {
        await updateMedicine(editingMedicine.id, payload, selectedFile)
        message.success('Cập nhật thuốc thành công')
      } else {
        await createMedicine(
          {
          ...payload,
          code: values.code?.trim(),
          },
          selectedFile,
        )
        message.success('Thêm thuốc thành công')
      }

      setModalOpen(false)
      setUploadFileList([])
      await loadMedicines()
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(medicineId) {
    setDeletingId(medicineId)
    try {
      await deleteMedicine(medicineId)
      message.success('Xóa thuốc thành công')
      await loadMedicines({ name: debouncedName })
      if (selectedMedicine?.id === medicineId) {
        setSelectedMedicine(null)
      }
    } catch (error) {
      message.error(getErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  const canShowEmpty = !loading && medicines.length === 0
  const title = useMemo(() => 'Quản lý thuốc', [])

  return (
    <Card>
      <Space orientation="vertical" size="middle" className="medicine-grid">
        <Space
          style={{ width: '100%', justifyContent: 'space-between' }}
          align="start"
          wrap
        >
          <Title level={3} style={{ margin: 0 }}>
            {title}
          </Title>
          <Space wrap>
            <Input
              placeholder="Tìm theo tên thuốc"
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              allowClear
              style={{ width: 240 }}
            />
            <Button type="primary" onClick={openCreateModal}>
              Thêm thuốc mới
            </Button>
          </Space>
        </Space>

        {canShowEmpty ? (
          <Card>
            <Text type="secondary">Không có dữ liệu thuốc.</Text>
          </Card>
        ) : null}

        <Row gutter={[16, 16]}>
          {medicines.map((medicine) => (
            <Col xs={24} sm={12} lg={8} xl={6} key={medicine.id}>
              <Card
                hoverable
                className="medicine-grid__card"
                loading={loading}
                cover={
                  <div className="medicine-grid__cover">
                    {medicine.imageUrl ? (
                      <img src={medicine.imageUrl} alt={medicine.name} />
                    ) : (
                      <span className="medicine-grid__cover-fallback">
                        Không có ảnh
                      </span>
                    )}
                  </div>
                }
                onClick={() => setSelectedMedicine(medicine)}
              >
                <div className="medicine-grid__meta">
                  <Title level={5} style={{ margin: 0 }}>
                    {medicine.name}
                  </Title>
                  <Text type="secondary">{medicine.code}</Text>
                  <Paragraph
                    className="medicine-grid__unit"
                    ellipsis={{ rows: 3, tooltip: medicine.unitName }}
                  >
                    {medicine.unitName}
                  </Paragraph>
                  <Text strong>{formatCurrency(medicine.price)}</Text>
                </div>

                <div className="medicine-grid__actions">
                  <Button
                    type="link"
                    onClick={(e) => {
                      e.stopPropagation()
                      openEditModal(medicine)
                    }}
                  >
                    Cập nhật thuốc
                  </Button>
                  <Popconfirm
                    title="Xóa thuốc"
                    description="Bạn chắc chắn muốn xóa thuốc này?"
                    okText="Xóa"
                    cancelText="Hủy"
                    onConfirm={(e) => {
                      e?.stopPropagation()
                      return handleDelete(medicine.id)
                    }}
                  >
                    <Button
                      type="link"
                      danger
                      loading={deletingId === medicine.id}
                      onClick={(e) => e.stopPropagation()}
                    >
                      Xóa
                    </Button>
                  </Popconfirm>
                </div>
              </Card>
            </Col>
          ))}
        </Row>

        <div className="medicine-grid__pagination">
          <Pagination
            current={page}
            pageSize={pageSize}
            total={total}
            showSizeChanger
            showTotal={(value) => `Tổng ${value} thuốc`}
            onChange={(nextPage, nextPageSize) => {
              setPage(nextPage)
              setPageSize(nextPageSize)
              loadMedicines({ nextPage, nextPageSize })
            }}
          />
        </div>
      </Space>

      <Modal
        title={editingMedicine ? 'Cập nhật thuốc' : 'Thêm thuốc mới'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSave}
        confirmLoading={saving}
        okText={editingMedicine ? 'Cập nhật' : 'Thêm mới'}
        cancelText="Hủy"
        width={720}
        forceRender
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item
                label="Mã thuốc"
                name="code"
                rules={[
                  { required: !editingMedicine, message: 'Vui lòng nhập mã thuốc' },
                  { max: 50, message: 'Tối đa 50 ký tự' },
                ]}
              >
                <Input placeholder="VD-xxxx" disabled={Boolean(editingMedicine)} />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item
                label="Tên thuốc"
                name="name"
                rules={[
                  { required: true, message: 'Vui lòng nhập tên thuốc' },
                  { max: 150, message: 'Tối đa 150 ký tự' },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="Danh mục thuốc"
            name="unitName"
            rules={[
              { required: true, message: 'Vui lòng chọn danh mục thuốc' },
              { max: 255, message: 'Tối đa 255 ký tự' },
            ]}
          >
            <Select
              showSearch
              filterOption={false}
              options={unitOptions}
              placeholder="Chọn danh mục thuốc"
              onSearch={handleUnitSearch}
              onPopupScroll={handleUnitPopupScroll}
              onDropdownVisibleChange={handleUnitDropdownOpen}
              notFoundContent={unitLoading ? <Spin size="small" /> : null}
            />
          </Form.Item>

          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item label="Hoạt chất" name="ingredient">
                <Input />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item
                label="Nhà sản xuất"
                name="manufacturer"
                rules={[{ max: 150, message: 'Tối đa 150 ký tự' }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item
                label="Giá"
                name="price"
                rules={[{ required: true, message: 'Vui lòng nhập giá' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  precision={2}
                  placeholder="0"
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item
                label="Tồn kho"
                name="stockQuantity"
                rules={[{ required: true, message: 'Vui lòng nhập tồn kho' }]}
              >
                <InputNumber style={{ width: '100%' }} min={0} precision={0} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="Mô tả"
            name="description"
          >
            <Input.TextArea rows={3} />
          </Form.Item>

          <Form.Item label="Ảnh thuốc">
            <Upload
              accept="image/*"
              maxCount={1}
              beforeUpload={() => false}
              fileList={uploadFileList}
              onChange={({ fileList }) => setUploadFileList(fileList)}
            >
              <Button>Chọn ảnh từ máy</Button>
            </Upload>
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title={selectedMedicine?.name || 'Chi tiết thuốc'}
        width={520}
        open={Boolean(selectedMedicine)}
        onClose={() => setSelectedMedicine(null)}
      >
        {selectedMedicine ? (
          <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
            {selectedMedicine.imageUrl ? (
              <img
                src={selectedMedicine.imageUrl}
                alt={selectedMedicine.name}
                style={{ width: '100%', borderRadius: 10 }}
              />
            ) : null}
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="Mã thuốc">
                {selectedMedicine.code}
              </Descriptions.Item>
              <Descriptions.Item label="Tên thuốc">
                {selectedMedicine.name}
              </Descriptions.Item>
              <Descriptions.Item label="Loại thuốc">
                {selectedMedicine.unitName || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Hoạt chất">
                {selectedMedicine.ingredient || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Nhà sản xuất">
                {selectedMedicine.manufacturer || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Giá">
                {formatCurrency(selectedMedicine.price)}
              </Descriptions.Item>
              <Descriptions.Item label="Tồn kho">
                {selectedMedicine.stockQuantity ?? '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Mô tả">
                <Paragraph style={{ margin: 0 }}>
                  {selectedMedicine.description || '-'}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="Image public id">
                {selectedMedicine.imagePublicId || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Trạng thái">
                <Tag color={selectedMedicine.active ? 'green' : 'default'}>
                  {selectedMedicine.active ? 'Đang hoạt động' : 'Ngưng hoạt động'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Ngày tạo">
                {formatDateTime(selectedMedicine.createdAt)}
              </Descriptions.Item>
              <Descriptions.Item label="Cập nhật gần nhất">
                {formatDateTime(selectedMedicine.updatedAt)}
              </Descriptions.Item>
            </Descriptions>
          </Space>
        ) : null}
      </Drawer>
    </Card>
  )
}

