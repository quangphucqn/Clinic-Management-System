import { Navigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth.js'
import { getDefaultRouteByRole } from '../../utils/authRouting.js'

export default function RequireRole({ roles, children }) {
  const { role } = useAuth()

  if (!roles.includes(role)) {
    return <Navigate to={getDefaultRouteByRole(role)} replace />
  }

  return children
}

