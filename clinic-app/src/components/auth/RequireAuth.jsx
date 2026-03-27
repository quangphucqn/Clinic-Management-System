import { Navigate, useLocation } from 'react-router-dom'
import { Spin } from 'antd'
import { ROUTES } from '../../constants/routes.js'
import { useAuth } from '../../hooks/useAuth.js'

export default function RequireAuth({ children }) {
  const location = useLocation()
  const { authLoading, isAuthenticated, currentUser } = useAuth()

  if (authLoading) {
    return (
      <div style={{ minHeight: '60vh', display: 'grid', placeItems: 'center' }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!isAuthenticated || !currentUser) {
    return <Navigate to={ROUTES.home} state={{ from: location }} replace />
  }

  return children
}

