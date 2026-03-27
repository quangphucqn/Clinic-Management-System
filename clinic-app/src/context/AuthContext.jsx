import { useEffect, useMemo, useState } from 'react'
import {
  getAuthToken,
  getCurrentUserProfile,
  getCurrentUserProfileFromStorage,
  login,
  logout,
  setAuthToken,
  setCurrentUserProfile,
} from '../services/authService.js'
import { AuthContext } from './auth-context.js'

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getAuthToken())
  const [currentUser, setCurrentUser] = useState(() =>
    getCurrentUserProfileFromStorage(),
  )
  const [authLoading, setAuthLoading] = useState(Boolean(getAuthToken()))

  useEffect(() => {
    let active = true

    async function bootstrap() {
      const savedToken = getAuthToken()
      if (!savedToken) {
        if (active) setAuthLoading(false)
        return
      }

      if (active) {
        setToken(savedToken)
      }

      try {
        const meResponse = await getCurrentUserProfile()
        const profile = meResponse?.result || null
        if (active) {
          setCurrentUser(profile)
          setCurrentUserProfile(profile)
        }
      } catch {
        if (active) {
          logout()
          setToken(null)
          setCurrentUser(null)
        }
      } finally {
        if (active) setAuthLoading(false)
      }
    }

    bootstrap()
    return () => {
      active = false
    }
  }, [])

  async function loginAndLoadProfile(credentials) {
    const authResponse = await login(credentials)
    const accessToken = authResponse?.result?.token
    if (!accessToken) {
      throw new Error('Phản hồi đăng nhập không hợp lệ')
    }

    setAuthToken(accessToken)
    setToken(accessToken)

    const meResponse = await getCurrentUserProfile()
    const profile = meResponse?.result || null
    setCurrentUser(profile)
    setCurrentUserProfile(profile)
    return profile
  }

  function logoutAndClear() {
    logout()
    setToken(null)
    setCurrentUser(null)
  }

  const value = useMemo(
    () => ({
      token,
      currentUser,
      role: currentUser?.role || null,
      authLoading,
      isAuthenticated: Boolean(token),
      loginAndLoadProfile,
      logoutAndClear,
    }),
    [authLoading, currentUser, token],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

