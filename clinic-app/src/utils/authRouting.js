import { ROUTES } from '../constants/routes.js'
import { ROLES } from '../constants/roles.js'

export function getDefaultRouteByRole(role) {
  switch (role) {
    case ROLES.ADMIN:
      return ROUTES.adminTimeslots
    case ROLES.DOCTOR:
      return ROUTES.doctorSchedule
    case ROLES.PATIENT:
      return ROUTES.patientBook
    default:
      return ROUTES.home
  }
}

