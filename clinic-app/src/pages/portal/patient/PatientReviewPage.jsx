import { ReloadOutlined, StarOutlined } from '@ant-design/icons'
import {
  App,
  Button,
  Card,
  Descriptions,
  Drawer,
  Empty,
  Form,
  Input,
  Modal,
  Rate,
  Space,
  Table,
  Tag,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  createDoctorReview,
  getMyAppointmentHistory,
  getMyDoctorReviews,
} from '../../../services/appointmentPatientService.js'
import { getErrorMessage } from '../../../utils/httpError.js'

const { Title, Paragraph } = Typography

export default function PatientReviewPage() {
  const { message } = App.useApp()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [appointments, setAppointments] = useState([])
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [total, setTotal] = useState(0)
  const [selectedAppointment, setSelectedAppointment] = useState(null)
  const [reviewsLoading, setReviewsLoading] = useState(false)
  const [myReviews, setMyReviews] = useState([])
  const [reviewsPage, setReviewsPage] = useState(1)
  const [reviewsPageSize, setReviewsPageSize] = useState(10)
  const [reviewsTotal, setReviewsTotal] = useState(0)
  const [selectedReview, setSelectedReview] = useState(null)

  const loadCompletedAppointments = useCallback(
    async ({ nextPage = 1, nextPageSize = pageSize } = {}) => {
      try {
        setLoading(true)
        const response = await getMyAppointmentHistory({
          status: 'COMPLETED',
          page: nextPage - 1,
          size: nextPageSize,
        })
        const pageData = response?.result
        setAppointments(pageData?.content || [])
        setPage((pageData?.page ?? 0) + 1)
        setPageSize(pageData?.size || nextPageSize)
        setTotal(pageData?.totalElements || 0)
      } catch (error) {
        message.error(getErrorMessage(error))
      } finally {
        setLoading(false)
      }
    },
    [message, pageSize],
  )

  useEffect(() => {
    loadCompletedAppointments({ nextPage: 1 })
  }, [loadCompletedAppointments])

  const loadMyReviews = useCallback(
    async ({ nextPage = 1, nextPageSize = reviewsPageSize } = {}) => {
      try {
        setReviewsLoading(true)
        const response = await getMyDoctorReviews({
          page: nextPage - 1,
          size: nextPageSize,
        })
        const pageData = response?.result
        setMyReviews(pageData?.content || [])
        setReviewsPage((pageData?.page ?? 0) + 1)
        setReviewsPageSize(pageData?.size || nextPageSize)
        setReviewsTotal(pageData?.totalElements || 0)
      } catch (error) {
        message.error(getErrorMessage(error))
      } finally {
        setReviewsLoading(false)
      }
    },
    [message, reviewsPageSize],
  )

  useEffect(() => {
    loadMyReviews({ nextPage: 1 })
  }, [loadMyReviews])

  function openReviewModal(appointment) {
    setSelectedAppointment(appointment)
    form.resetFields()
    form.setFieldsValue({ rating: 5, comment: '' })
  }

  async function handleSubmitReview() {
    if (!selectedAppointment?.appointmentId) return
    try {
      const values = await form.validateFields()
      setSubmitting(true)
      await createDoctorReview({
        appointmentId: selectedAppointment.appointmentId,
        rating: values.rating,
        comment: values.comment?.trim() || undefined,
      })
      message.success('Đánh giá bác sĩ thành công')
      setSelectedAppointment(null)
      form.resetFields()
      await loadCompletedAppointments()
      await loadMyReviews({ nextPage: 1 })
    } catch (error) {
      if (error?.errorFields) return
      message.error(getErrorMessage(error))
    } finally {
      setSubmitting(false)
    }
  }

  const columns = useMemo(
    () => [
      {
        title: 'Ngày khám',
        dataIndex: 'appointmentDate',
        key: 'appointmentDate',
        width: 130,
        render: (value) => (value ? dayjs(value).format('DD/MM/YYYY') : '-'),
      },
      {
        title: 'Bác sĩ',
        dataIndex: 'doctorName',
        key: 'doctorName',
        width: 220,
        render: (value) => value || '-',
      },
      {
        title: 'Khung giờ',
        dataIndex: 'timeSlot',
        key: 'timeSlot',
        width: 160,
        render: (value) => value || '-',
      },
      {
        title: 'Trạng thái đánh giá',
        dataIndex: 'reviewed',
        key: 'reviewed',
        width: 170,
        render: (reviewed) =>
          reviewed ? <Tag color="success">Đã đánh giá</Tag> : <Tag color="warning">Chưa đánh giá</Tag>,
      },
      {
        title: 'Thao tác',
        key: 'actions',
        width: 160,
        render: (_, record) =>
          record.reviewed ? (
            <Tag color="default">Bác sĩ này đã được đánh giá</Tag>
          ) : (
            <Button type="primary" size="small" icon={<StarOutlined />} onClick={() => openReviewModal(record)}>
              Đánh giá
            </Button>
          ),
      },
    ],
    [],
  )

  const reviewColumns = useMemo(
    () => [
      {
        title: 'Ngày đánh giá',
        dataIndex: 'reviewedAt',
        key: 'reviewedAt',
        width: 170,
        render: (value) => (value ? dayjs(value).format('HH:mm DD/MM/YYYY') : '-'),
      },
      {
        title: 'Bác sĩ',
        dataIndex: 'doctorName',
        key: 'doctorName',
        width: 220,
        render: (value) => value || '-',
      },
      {
        title: 'Ngày khám',
        dataIndex: 'appointmentDate',
        key: 'appointmentDate',
        width: 130,
        render: (value) => (value ? dayjs(value).format('DD/MM/YYYY') : '-'),
      },
      {
        title: 'Số sao',
        dataIndex: 'rating',
        key: 'rating',
        width: 110,
        render: (value) => <Rate value={value || 0} disabled />,
      },
      {
        title: 'Nhận xét',
        dataIndex: 'comment',
        key: 'comment',
        render: (value) => value || '-',
      },
    ],
    [],
  )

  return (
    <Space direction="vertical" size="middle" style={{ width: '100%' }}>
      <Card>
        <Space align="start" style={{ width: '100%', justifyContent: 'space-between' }} wrap>
          <div>
            <Title level={3} style={{ margin: 0 }}>
              Đánh giá bác sĩ
            </Title>
            <Paragraph type="secondary" style={{ marginTop: 8, marginBottom: 0 }}>
              Bạn chỉ có thể đánh giá bác sĩ đã khám cho mình và mỗi bác sĩ chỉ đánh giá một lần.
            </Paragraph>
          </div>
          <Button icon={<ReloadOutlined />} onClick={() => loadCompletedAppointments({ nextPage: 1 })}>
            Tải lại
          </Button>
        </Space>
      </Card>

      <Card loading={loading}>
        {appointments.length ? (
          <Table
            rowKey="appointmentId"
            columns={columns}
            dataSource={appointments}
            pagination={{
              current: page,
              pageSize,
              total,
              showSizeChanger: true,
              showTotal: (value) => `Tổng ${value} lần khám đã hoàn tất`,
            }}
            scroll={{ x: 920 }}
            onChange={(pagination) => {
              const nextPage = pagination.current || 1
              const nextPageSize = pagination.pageSize || 10
              setPage(nextPage)
              setPageSize(nextPageSize)
              loadCompletedAppointments({ nextPage, nextPageSize })
            }}
          />
        ) : (
          <Empty description="Bạn chưa có lần khám hoàn tất để đánh giá" />
        )}
      </Card>

      <Card loading={reviewsLoading} title={`Đánh giá của tôi (${reviewsTotal})`}>
        {myReviews.length ? (
          <Table
            rowKey="reviewId"
            columns={reviewColumns}
            dataSource={myReviews}
            pagination={{
              current: reviewsPage,
              pageSize: reviewsPageSize,
              total: reviewsTotal,
              showSizeChanger: true,
              showTotal: (value) => `Tổng ${value} đánh giá`,
            }}
            scroll={{ x: 920 }}
            onRow={(record) => ({
              onClick: () => setSelectedReview(record),
              style: { cursor: 'pointer' },
            })}
            onChange={(pagination) => {
              const nextPage = pagination.current || 1
              const nextPageSize = pagination.pageSize || 10
              setReviewsPage(nextPage)
              setReviewsPageSize(nextPageSize)
              loadMyReviews({ nextPage, nextPageSize })
            }}
          />
        ) : (
          <Empty description="Bạn chưa gửi đánh giá nào" />
        )}
      </Card>

      <Modal
        title="Đánh giá bác sĩ"
        open={Boolean(selectedAppointment)}
        onCancel={() => {
          setSelectedAppointment(null)
          form.resetFields()
        }}
        onOk={handleSubmitReview}
        okText="Gửi đánh giá"
        cancelText="Hủy"
        confirmLoading={submitting}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="Bác sĩ">
            <Input value={selectedAppointment?.doctorName || '-'} disabled />
          </Form.Item>
          <Form.Item
            label="Số sao"
            name="rating"
            rules={[{ required: true, message: 'Vui lòng chọn số sao đánh giá' }]}
          >
            <Rate />
          </Form.Item>
          <Form.Item
            label="Nhận xét"
            name="comment"
            rules={[{ max: 1000, message: 'Tối đa 1000 ký tự' }]}
          >
            <Input.TextArea rows={4} placeholder="Chia sẻ trải nghiệm của bạn (không bắt buộc)" />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="Chi tiết đánh giá của tôi"
        width={520}
        open={Boolean(selectedReview)}
        onClose={() => setSelectedReview(null)}
      >
        {selectedReview ? (
          <Descriptions bordered column={1} size="small">
            <Descriptions.Item label="Bác sĩ">{selectedReview.doctorName || '-'}</Descriptions.Item>
            <Descriptions.Item label="Ngày khám">
              {selectedReview.appointmentDate
                ? dayjs(selectedReview.appointmentDate).format('DD/MM/YYYY')
                : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Khung giờ">{selectedReview.timeSlot || '-'}</Descriptions.Item>
            <Descriptions.Item label="Ngày đánh giá">
              {selectedReview.reviewedAt
                ? dayjs(selectedReview.reviewedAt).format('HH:mm DD/MM/YYYY')
                : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="Số sao">
              <Rate value={selectedReview.rating || 0} disabled />
            </Descriptions.Item>
            <Descriptions.Item label="Nhận xét">
              {selectedReview.comment || '-'}
            </Descriptions.Item>
          </Descriptions>
        ) : null}
      </Drawer>
    </Space>
  )
}

