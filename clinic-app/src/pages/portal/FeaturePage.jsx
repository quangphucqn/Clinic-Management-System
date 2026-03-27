import { Card, Typography } from 'antd'

const { Title, Paragraph } = Typography

export default function FeaturePage({ title, description }) {
  return (
    <Card>
      <Title level={3}>{title}</Title>
      <Paragraph>{description}</Paragraph>
    </Card>
  )
}

