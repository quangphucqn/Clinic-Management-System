# Clinic App Coding Rules

Use these rules for every future code change in this project.

## Project Scope

- This project is a React single-page application for a clinic management system.
- The frontend depends on a backend API and assumes API responses usually use the shape `{ result: ... }`.
- UI text is written in Vietnamese and should stay consistent with the existing wording.

## Tech Stack

- Use React function components with hooks.
- Use Vite for development and build.
- Use Ant Design as the primary UI framework.
- Use React Router for routing.
- Use Axios through the shared API client in `src/services/api.js`.
- Use the existing auth context instead of introducing a new auth/state pattern.
- React Compiler is enabled. Do not add `useMemo` or `useCallback` by default unless there is a clear need and it matches the existing style.

## Folder Responsibilities

- `src/pages`: page-level screens and page-local state.
- `src/pages/portal/admin`: admin-only portal screens.
- `src/pages/portal/doctor`: doctor-only portal screens.
- `src/layouts`: shared page shells such as auth layout and portal layout.
- `src/components`: reusable UI or route-guard components.
- `src/context`: global app state, currently focused on authentication.
- `src/hooks`: thin custom hooks such as `useAuth`.
- `src/services`: all backend communication, grouped by domain.
- `src/services/doctor`: doctor-facing service modules.
- `src/constants`: shared constants such as routes and roles.
- `src/utils`: small pure helpers.
- `public`: static assets referenced by absolute public paths.

## Architecture Rules

- Keep API calls inside `src/services`. Do not call Axios directly from pages, layouts, or components.
- Reuse the shared Axios instance from `src/services/api.js` so auth headers and multipart handling stay centralized.
- Keep route definitions aligned with `src/constants/routes.js`.
- Keep role checks aligned with `src/constants/roles.js`, `RequireAuth`, `RequireRole`, and `getDefaultRouteByRole`.
- Prefer local component state with hooks. Do not introduce Redux, Zustand, or a new global store unless explicitly requested.
- Put screen-specific CSS next to the screen or layout that owns it.
- Keep components focused. If a page grows too large, extract a local subcomponent under `src/components` or a nearby folder only when it improves clarity.

## Data And API Conventions

- Assume paginated API requests use zero-based `page` values when calling the backend.
- Keep Ant Design pagination one-based in the UI and translate when calling services.
- Read server data from `response?.result` unless the backend contract for that endpoint clearly differs.
- Send optional fields as `undefined` or `null` consistently with surrounding code.
- Trim user-entered strings before submit when the surrounding code already follows that pattern.
- Use `FormData` only when needed, such as file upload flows like medicine management.

## Error Handling Rules

- Use `App.useApp()` from Ant Design for toast feedback.
- Use `getErrorMessage` from `src/utils/httpError.js` for generic request failures.
- Preserve the existing pattern where validation errors from Ant Design forms short-circuit with `if (error?.errorFields) return`.
- Keep success and error messages in Vietnamese.

## UI And Styling Rules

- Build new screens with Ant Design first, matching the existing admin pages.
- Reuse existing layout patterns: `Card`, `Table`, `Drawer`, `Modal`, `Form`, `Descriptions`, `Space`, `Input`, `Select`, `Button`.
- Keep spacing and sizing close to existing screens instead of inventing a new visual system.
- Keep the Quicksand-based look and current color direction unless a redesign is requested.
- Prefer responsive Ant Design grid props already used in the project.

## Routing And Auth Rules

- Protected portal pages must remain under `/app` and be wrapped by the existing auth flow.
- New doctor-only pages must use `RequireRole roles={[ROLES.DOCTOR]}`.
- If a new doctor route is added, update both the route tree in `src/App.jsx` and the doctor menu in `src/layouts/PortalLayout.jsx`.
- If a new feature becomes a role landing page, update `getDefaultRouteByRole` in `src/utils/authRouting.js`.

## Naming And Code Style

- Match the existing codebase style: JavaScript with `.jsx` and `.js`, not TypeScript.
- Use descriptive names. Avoid one-letter names except in tiny local callbacks.
- Follow the current import style with explicit relative paths and file extensions.
- Preserve the existing formatting style and semicolon-free style.
- Keep inline comments rare. Add them only when logic is not obvious.

## Doctor Module Rules

- The next development focus is the doctor area. Prefer extending the current structure instead of creating a parallel structure.
- Put doctor-facing pages under `src/pages/portal/doctor`.
- Reuse the existing portal shell and role-routing model.
- If a doctor feature needs API integration, add it under `src/services/doctor` instead of mixing it with admin-facing services.
- Do not move or refactor existing admin-owned service files unless explicitly requested.
- Treat the current admin area as owned by another team member and avoid touching admin code unless the task truly requires it.
- Keep doctor flows consistent with the current admin CRUD patterns: load data, show table or cards, open detail drawer or modal, submit through Ant Design forms, refresh list after mutation.
- When a doctor feature overlaps with admin-managed data such as specialties, schedules, notifications, or profile data, reuse existing services and helpers before creating new ones.

## Change Discipline

- Make the smallest change that fits the current architecture.
- Do not refactor unrelated modules while implementing a feature.
- Prefer consistency with the existing codebase over introducing a newer pattern.
- When adding a new domain capability, first check whether an existing page, service, helper, route constant, or role helper should be extended.
- After changes, verify lint or build behavior when practical.

## Default Implementation Checklist

- Add or update route constants if a new route is needed.
- Add or update service methods before wiring page logic.
- Use Ant Design form validation rules on user input.
- Normalize request payload values before submit.
- Handle loading, saving, and deleting states explicitly.
- Show user feedback with Ant Design messages.
- Keep Vietnamese labels and messages consistent with the existing project.