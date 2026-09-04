# College Attendance Management System — Integration & UI Architecture Document

## 1. Recommended Full Architecture

React Frontend
      |
      | HTTPS / REST + JSON
      v
Spring Boot REST API
      |
      +---- Spring Security / JWT
      |
      +---- Service Layer
      |
      +---- JPA/Hibernate
      |
      v
MySQL Database

React Camera
      |
      | multipart/form-data
      v
Spring Boot File Upload Service
      |
      v
Secure File/Object Storage

## 2. Role Model

ADMIN
- Full management access.
- CRUD master data.
- Manage users.
- Create attendance sessions.
- Approve/reject attendance.
- Manually mark/correct attendance.
- View reports and audit logs.

USER
For a college implementation, the USER role can represent students (and optionally faculty-facing screens if faculty are later added).
- View own data.
- Submit own attendance.
- Capture selfie when required.
- View own attendance.

Important: "admin can perform CRUD on user" does not mean the admin should be able to edit attendance history without trace. Attendance changes should create audit records.

## 3. Main Use Cases

### UC-01 Login
User → React Login → POST /auth/login → Backend → JWT → React stores auth state → Redirect by role.

### UC-02 Admin Creates Student
Admin → Student form → POST /admin/students → Backend validates → DB insert → Response → Refresh student table.

### UC-03 Admin Creates Attendance Session
Admin → Select subject/class/date/time → POST /admin/attendance-sessions → Backend creates session.

### UC-04 Student Takes Attendance
Student → Active session → Camera permission → Capture selfie → Preview → Submit → Backend validates → Store selfie → Create SUBMITTED attendance.

### UC-05 Admin Approves
Admin → Pending list → Open submission → Review student/session/selfie → Approve → Backend changes status → Audit log → Student sees APPROVED.

### UC-06 Admin Rejects
Admin → Pending list → Reject → Enter reason → Backend changes status to REJECTED → Audit log → Student sees reason.

## 4. Database Relationship Summary

users 1---1 students
users 1---1 faculty
departments 1---N students
departments 1---N faculty
departments 1---N subjects
departments 1---N class_sections
subjects 1---N subject_assignments
faculty 1---N subject_assignments
class_sections 1---N subject_assignments
subject_assignments 1---N attendance_sessions
attendance_sessions 1---N attendance
students 1---N attendance
attendance 1---0..1 selfies
users 1---N audit_logs

## 5. API Contract Rules
All APIs should:
- Return consistent JSON.
- Use HTTP status codes correctly.
- Validate input.
- Return a stable error code.
- Never return password hashes.
- Paginate large admin lists.
- Use DTOs instead of exposing entities directly.

Suggested success response:
{
  "success": true,
  "message": "Attendance approved successfully",
  "data": { ... }
}

Suggested error:
{
  "success": false,
  "code": "FORBIDDEN",
  "message": "You do not have permission to perform this action."
}

## 6. CORS
During development:
Frontend: http://localhost:5173
Backend: http://localhost:8080

Allow only the required frontend origin.

Production:
Use the real frontend domain and HTTPS.

## 7. Authentication Flow
1. Login request.
2. Backend verifies credentials.
3. Backend returns access token (and refresh token if implemented).
4. Frontend stores auth state safely.
5. Axios attaches access token.
6. Backend validates token on protected endpoints.
7. Backend checks role/ownership.
8. On expiry, refresh or redirect to login.

For high-security production deployment, consider an HttpOnly secure cookie approach instead of exposing long-lived tokens to JavaScript.

## 8. UI Design System

### Admin
Style: professional college ERP dashboard.
- Left sidebar.
- Top header.
- Breadcrumbs.
- Cards.
- Data tables.
- Modal forms.
- Confirmation dialogs.
- Charts.
- Search/filter controls.

### User
Style: simple mobile-first student dashboard.
- Today's classes.
- Attendance action card.
- Large "Take Attendance" button.
- Attendance percentage.
- Subject cards.
- History list.

## 9. Suggested Admin Navigation
Dashboard
Users
Students
Faculty
Departments
Subjects
Class Sections
Assignments
Attendance Sessions
Pending Approvals
Attendance Records
Reports
Audit Logs
Settings

## 10. Suggested User Navigation
Dashboard
My Profile
My Subjects
Take Attendance
Attendance History
Attendance Summary

## 11. Selfie Feature Design

### Basic version
The selfie is evidence attached to an attendance submission.

Recommended metadata:
- attendanceId
- capturedAt
- file type
- file size
- storage key
- verification status

### Optional future version
Face verification can be added later as a separate service. It should not be mixed into the first implementation because it increases privacy, security, infrastructure and testing complexity.

Do not describe the basic selfie feature as automatic identity verification unless a verified face-matching mechanism is actually implemented.

## 12. Security and Privacy
Selfies are sensitive personal data in many environments. Before production:
- Define retention period.
- Restrict who can view selfies.
- Encrypt storage where appropriate.
- Use signed/private URLs.
- Log administrative access.
- Delete data according to institutional policy.
- Obtain required notices/consent where applicable.
- Do not expose selfie URLs publicly.

## 13. Development Environments

Backend:
http://localhost:8080

Frontend:
http://localhost:5173

Database:
localhost:3306/college_attendance

Use environment variables:
Backend:
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
FILE_STORAGE_PATH

Frontend:
VITE_API_BASE_URL

Never commit passwords, JWT secrets or production credentials to Git.

## 14. Recommended Repository Structure

college-attendance-system/
├── backend/
│   ├── pom.xml
│   └── src/
├── frontend/
│   ├── package.json
│   └── src/
└── docs/
    ├── backend.md
    ├── frontend.md
    └── integration.md

## 15. AI Development Strategy

Use AI as a coding assistant, not as a replacement for testing.

Prompt sequence:
1. "Read backend.md and create the Spring Boot skeleton."
2. "Implement entities and enums from backend.md."
3. "Create repositories and DTOs."
4. "Implement authentication and authorization."
5. "Implement admin CRUD."
6. "Implement attendance session and attendance workflow."
7. "Implement secure selfie upload."
8. "Implement approval and audit logging."
9. "Read frontend.md and create the React skeleton."
10. "Implement authentication and role routing."
11. "Implement admin UI."
12. "Implement user UI."
13. "Implement camera/selfie UI."
14. "Connect frontend to backend APIs."
15. "Run integration tests and fix errors."

At every step ask the AI:
- Which files changed?
- What API endpoints are required?
- How do I run it?
- What should I test?
- Are there any security concerns?

## 16. MVP Scope
Build these first:
- Login
- Admin/user roles
- Admin student CRUD
- Subject/class management
- Attendance session
- Student selfie attendance
- Pending approval
- Admin approve/reject
- Attendance history
- Attendance percentage

Later:
- Notifications
- Excel/PDF reports
- QR attendance
- Face verification
- Geofencing
- Multiple colleges/campuses
- Advanced analytics
- Mobile app

## 17. Definition of Done
A feature is complete only when:
- Backend endpoint works.
- Database data is correct.
- Frontend UI works.
- Role authorization works.
- Validation works.
- Error handling works.
- Happy-path test passes.
- Unauthorized access test passes.
- Documentation is updated.
