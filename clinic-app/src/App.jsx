import { ConfigProvider } from 'antd'
import viVN from 'antd/locale/vi_VN'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import RequireAuth from './components/auth/RequireAuth.jsx'
import RequireRole from './components/auth/RequireRole.jsx'
import RoleHomeRedirect from './components/auth/RoleHomeRedirect.jsx'
import { ROUTES } from './constants/routes.js'
import { ROLES } from './constants/roles.js'
import { AuthProvider } from './context/AuthContext.jsx'
import AuthLayout from './layouts/AuthLayout.jsx'
import PortalLayout from './layouts/PortalLayout.jsx'
import HomePage from './pages/HomePage.jsx'
import LoginPage from './pages/auth/LoginPage.jsx'
import RegisterPage from './pages/auth/RegisterPage.jsx'
import MedicineManagementPage from './pages/portal/admin/MedicineManagementPage.jsx'
import DoctorManagementPage from './pages/portal/admin/DoctorManagementPage.jsx'
import NotificationManagementPage from './pages/portal/admin/NotificationManagementPage.jsx'
import SpecialtyManagementPage from './pages/portal/admin/SpecialtyManagementPage.jsx'
import TimeSlotManagementPage from './pages/portal/admin/TimeSlotManagementPage.jsx'
import UnitManagementPage from './pages/portal/admin/UnitManagementPage.jsx'
import AppointmentBookingPage from './pages/portal/patient/AppointmentBookingPage.jsx'
import PatientPrescriptionPage from './pages/portal/patient/PatientPrescriptionPage.jsx'
import FeaturePage from './pages/portal/FeaturePage.jsx'
import ProfilePage from './pages/portal/ProfilePage.jsx'

export default function App() {
  return (
    <ConfigProvider
      locale={viVN}
      theme={{
        token: {
          fontFamily: "'Quicksand', system-ui, sans-serif",
          colorPrimary: '#6cc5d8',
        },
      }}
    >
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path={ROUTES.home} element={<HomePage />} />
            <Route element={<AuthLayout />}>
              <Route path={ROUTES.login} element={<LoginPage />} />
              <Route path={ROUTES.register} element={<RegisterPage />} />
            </Route>

            <Route
              path={ROUTES.app}
              element={
                <RequireAuth>
                  <PortalLayout />
                </RequireAuth>
              }
            >
              <Route index element={<RoleHomeRedirect />} />
              <Route path="profile" element={<ProfilePage />} />

              <Route
                path="patient/bookings/new"
                element={
                  <RequireRole roles={[ROLES.PATIENT]}>
                    <AppointmentBookingPage />
                  </RequireRole>
                }
              />
              <Route
                path="patient/appointments"
                element={
                  <RequireRole roles={[ROLES.PATIENT]}>
                    <FeaturePage
                      title="Lịch khám của tôi"
                      description="Danh sách lịch hẹn sắp tới của bệnh nhân."
                    />
                  </RequireRole>
                }
              />
              <Route
                path="patient/history"
                element={
                  <RequireRole roles={[ROLES.PATIENT]}>
                    <PatientPrescriptionPage />
                  </RequireRole>
                }
              />

              <Route
                path="doctor/schedule"
                element={
                  <RequireRole roles={[ROLES.DOCTOR]}>
                    <FeaturePage
                      title="Lịch khám bác sĩ"
                      description="Bác sĩ theo dõi các lịch hẹn bệnh nhân theo từng ngày."
                    />
                  </RequireRole>
                }
              />

              <Route
                path="admin/timeslots"
                element={
                  <RequireRole roles={[ROLES.ADMIN]}>
                    <TimeSlotManagementPage />
                  </RequireRole>
                }
              />
              <Route
                path="admin/specialties"
                element={
                  <RequireRole roles={[ROLES.ADMIN]}>
                    <SpecialtyManagementPage />
                  </RequireRole>
                }
              />
              <Route
                path="admin/doctors"
                element={
                  <RequireRole roles={[ROLES.ADMIN]}>
                    <DoctorManagementPage />
                  </RequireRole>
                }
              />
              <Route
                path="admin/notifications"
                element={
                  <RequireRole roles={[ROLES.ADMIN]}>
                    <NotificationManagementPage />
                  </RequireRole>
                }
              />
              <Route
                path="admin/units"
                element={
                  <RequireRole roles={[ROLES.ADMIN]}>
                    <UnitManagementPage />
                  </RequireRole>
                }
              />
              <Route
                path="admin/medicines"
                element={
                  <RequireRole roles={[ROLES.ADMIN]}>
                    <MedicineManagementPage />
                  </RequireRole>
                }
              />
            </Route>

            <Route path="*" element={<Navigate to={ROUTES.home} replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ConfigProvider>
  )
}
