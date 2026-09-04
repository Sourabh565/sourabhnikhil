# College Attendance Management System — Frontend Technical Document

## 1. Project Overview
Frontend: React + Vite + React Router + Axios.
The UI has two role-based experiences:
- Admin dashboard
- User/student dashboard

The frontend communicates with the Spring Boot backend through REST APIs.

## 2. Suggested Technology Stack
- React
- Vite
- React Router
- Axios
- JavaScript or TypeScript (TypeScript recommended)
- React Hook Form
- Yup/Zod for validation
- CSS Modules/Tailwind/Material UI (choose one, do not mix unnecessarily)
- Recharts or another chart library for dashboard charts

## 3. Frontend Structure

src/
├── api/
│   ├── axiosClient.js
│   ├── authApi.js
│   ├── adminApi.js
│   └── attendanceApi.js
├── assets/
├── components/
│   ├── common/
│   ├── forms/
│   ├── tables/
│   └── camera/
├── layouts/
│   ├── AdminLayout.jsx
│   └── UserLayout.jsx
├── pages/
│   ├── auth/
│   ├── admin/
│   └── user/
├── routes/
│   ├── AppRoutes.jsx
│   └── ProtectedRoute.jsx
├── hooks/
├── context/
│   └── AuthContext.jsx
├── utils/
├── App.jsx
└── main.jsx

## 4. UI Pages

### Public
- Login
- Forgot password (optional)

### Admin
- Dashboard
- Users
- Students
- Faculty
- Departments
- Subjects
- Class Sections
- Subject Assignments
- Attendance Sessions
- Pending Attendance
- Attendance Records
- Reports
- Audit Logs
- Admin Profile

### User
- Dashboard
- My Profile
- My Subjects
- Active Attendance
- Selfie Capture
- Attendance History
- Attendance Summary

## 5. Admin Dashboard UI
Cards:
- Total Students
- Total Faculty
- Today's Sessions
- Pending Approvals

Charts:
- Attendance percentage by subject
- Present/Absent trend
- Department/section attendance

Tables:
- Pending approvals
- Today's attendance sessions

## 6. User Dashboard UI
Show:
- Today's classes
- Active attendance sessions
- Overall attendance percentage
- Subject-wise attendance
- Pending/rejected submissions

Use clear status badges:
- SUBMITTED
- APPROVED
- REJECTED
- PRESENT
- ABSENT

## 7. Selfie Attendance UI
Create a dedicated camera component.

Flow:
1. Ask for camera permission.
2. Show live camera preview.
3. Show capture button.
4. Capture image.
5. Show preview.
6. Allow Retake.
7. Submit selfie with attendance.
8. Show upload/progress state.
9. Show success/failure result.

Use browser MediaDevices/getUserMedia.
Do not upload continuously; capture only the image needed for the attendance submission.

## 8. Admin Attendance Approval UI
Table columns:
- Student
- Enrollment No.
- Subject
- Date
- Time
- Selfie preview
- Submission status
- Actions

Actions:
- View
- Approve
- Reject
- Mark manually

Reject should open a modal requiring a reason.

## 9. CRUD UI Pattern
For Students, Subjects, Faculty, etc.:
- Search bar
- Filter
- Add button
- Data table
- Edit
- Delete/deactivate
- Pagination
- Confirmation modal
- Loading state
- Empty state
- Error state

## 10. Routing

/
 /login
 /admin
 /admin/users
 /admin/students
 /admin/faculty
 /admin/departments
 /admin/subjects
 /admin/sections
 /admin/assignments
 /admin/attendance-sessions
 /admin/attendance/pending
 /admin/attendance
 /admin/reports
 /admin/audit-logs
 /user
 /user/profile
 /user/subjects
 /user/attendance
 /user/attendance/history
 /user/attendance/summary

ProtectedRoute checks:
- Is authenticated?
- Is the required role allowed?

Do not rely only on hidden frontend routes for security. Backend authorization remains mandatory.

## 11. Axios Configuration
Create one Axios client:
- baseURL from environment variable
- Authorization header interceptor
- common error interceptor
- timeout
- optional refresh-token handling

Example environment:
VITE_API_BASE_URL=http://localhost:8080/api

Never hard-code production secrets into the frontend.

## 12. API Integration

Login:
POST /auth/login

Admin:
GET/POST/PUT/DELETE /admin/users
GET/POST/PUT/DELETE /admin/students
GET/POST/PUT/DELETE /admin/faculty
GET/POST/PUT/DELETE /admin/subjects
GET/POST/PUT/DELETE /admin/sections

Attendance:
GET /user/attendance/sessions
POST /user/attendance/{sessionId}/submit
GET /user/attendance/history
GET /user/attendance/summary

Approval:
GET /admin/attendance/pending
PATCH /admin/attendance/{id}/approve
PATCH /admin/attendance/{id}/reject
PATCH /admin/attendance/{id}/mark

## 13. Selfie Multipart Request
Use FormData:
- selfie: captured image file
- optional attendance metadata if the backend explicitly requires it

Do not send a base64 image in JSON unless there is a specific reason. Multipart upload is preferable.

## 14. State Management
Start simple:
- AuthContext for authentication/user/role.
- Local component state for forms and tables.
- Add React Query/TanStack Query if API caching and server-state management becomes important.

## 15. Responsive UI
Desktop:
- Sidebar + top navigation + content area.

Tablet/mobile:
- Collapsible sidebar.
- Responsive tables.
- Camera view should fit the device viewport.
- Buttons must be touch-friendly.

## 16. UX Requirements
Every API action should have:
- Loading indicator
- Success feedback
- Error feedback
- Empty state

For destructive operations:
- Confirmation dialog.
- Prefer deactivate/soft delete for important records.

## 17. Accessibility
- Labels for form controls.
- Keyboard-accessible buttons.
- Visible focus states.
- Alt text for images.
- Meaningful error messages.
- Do not communicate status using color alone.

## 18. Frontend Implementation Order
1. Create Vite React project.
2. Set up folder structure.
3. Build global layout/components.
4. Build Login.
5. Connect authentication API.
6. Add protected routes.
7. Build Admin Dashboard.
8. Build admin CRUD pages.
9. Build User Dashboard.
10. Build attendance session UI.
11. Build camera/selfie component.
12. Integrate multipart upload.
13. Build admin approval UI.
14. Build reports.
15. Add loading/error/empty states.
16. Test all flows.

## 19. AI Coding Prompt
Use this as a starting prompt:

"Build the frontend of a college attendance management system using React + Vite. Use the attached frontend technical document as the source of truth. Create the project incrementally, starting with authentication and layouts. Do not invent APIs: use the documented REST endpoints. Create reusable components, role-based protected routes, Axios API services, responsive admin and user dashboards, CRUD screens, attendance history, admin approval workflow, and a browser camera selfie capture component. After each milestone, explain changed files and how to test them."

Do not generate everything in one response. Generate one milestone at a time and run/test the application after each milestone.
