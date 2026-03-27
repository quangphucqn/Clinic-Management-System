import { Navigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth.js'
import { getDefaultRouteByRole } from '../../utils/authRouting.js'

export default function RoleHomeRedirect() {
  const { role } = useAuth()
  return <Navigate to={getDefaultRouteByRole(role)} replace />
}

