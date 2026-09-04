# College Attendance Management System — Backend Technical Document

## 1. Project Overview
Backend: Java 17 + Spring Boot + Spring Security + JPA/Hibernate + MySQL.
The system has two roles:
- ADMIN: manage users, students, faculty, courses, classes, attendance and approvals.
- USER: student/faculty-facing operations. A student can view timetable, take attendance when allowed, capture a selfie, and view attendance history.

Recommended architecture:
Controller → Service → Repository → Database
                         ↓
                 File/Object Storage
                         ↓
                   Selfie metadata

## 2. Core Functional Requirements

### Admin
- Login/logout.
- CRUD users.
- Activate/deactivate users.
- CRUD students.
- CRUD faculty.
- CRUD departments.
- CRUD courses/subjects.
- CRUD class sections.
- Assign faculty to subjects/classes.
- Create/manage timetable or attendance sessions.
- View attendance.
- Approve/reject attendance submissions.
- Manually mark/correct attendance with audit trail.
- Search/filter attendance.
- Export reports.
- View dashboard statistics.

### User / Student
- Login.
- View profile.
- View assigned subjects/classes.
- View current attendance sessions.
- Submit attendance.
- Capture selfie during attendance.
- View pending/approved/rejected attendance.
- View attendance percentage.
- View attendance history.

## 3. Suggested Technology Stack
- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Security
- JWT-based authentication
- Spring Data JPA
- Hibernate
- MySQL 8+
- Bean Validation
- Lombok (optional)
- Maven
- Swagger/OpenAPI
- JUnit + Mockito
- Flyway or Liquibase for database migrations
- Object storage/local storage for selfie files; database should store only file URL/key and metadata.

## 4. Package Structure

com.college.attendance
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
├── service
│   └── impl
├── mapper
└── util

## 5. Main Entities

### User
- id
- username/email
- passwordHash
- role
- status
- createdAt
- updatedAt

### Student
- id
- userId
- enrollmentNo
- firstName
- lastName
- phone
- departmentId
- semester
- section
- admissionYear

### Faculty
- id
- userId
- employeeCode
- name
- departmentId

### Department
- id
- code
- name

### Subject
- id
- code
- name
- departmentId
- semester
- credits

### ClassSection
- id
- name
- departmentId
- semester
- academicYear

### SubjectAssignment
- id
- subjectId
- facultyId
- sectionId

### AttendanceSession
- id
- subjectAssignmentId
- date
- startTime
- endTime
- sessionStatus
- createdBy
- createdAt

### Attendance
- id
- sessionId
- studentId
- status
- submittedAt
- approvedAt
- approvedBy
- rejectionReason
- selfieId
- remarks

### Selfie
- id
- attendanceId
- storageKey
- fileUrl
- capturedAt
- mimeType
- fileSize
- verificationStatus
- verificationMessage

### AuditLog
- id
- actorUserId
- action
- entityType
- entityId
- oldValueSummary
- newValueSummary
- createdAt

## 6. Attendance State Machine
SUBMITTED → APPROVED
SUBMITTED → REJECTED
REJECTED → RESUBMITTED
APPROVED → (normally immutable; admin correction creates an audit record)

For manual admin marking:
ADMIN_MARKED → APPROVED

## 7. Database Schema

users(
  id BIGINT PK,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
)

departments(
  id BIGINT PK,
  code VARCHAR(30) UNIQUE NOT NULL,
  name VARCHAR(120) NOT NULL
)

students(
  id BIGINT PK,
  user_id BIGINT UNIQUE NOT NULL FK users(id),
  enrollment_no VARCHAR(50) UNIQUE NOT NULL,
  first_name VARCHAR(80) NOT NULL,
  last_name VARCHAR(80),
  phone VARCHAR(20),
  department_id BIGINT FK departments(id),
  semester INT,
  section VARCHAR(30),
  admission_year INT
)

faculty(
  id BIGINT PK,
  user_id BIGINT UNIQUE NOT NULL FK users(id),
  employee_code VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(120) NOT NULL,
  department_id BIGINT FK departments(id)
)

subjects(
  id BIGINT PK,
  code VARCHAR(30) UNIQUE NOT NULL,
  name VARCHAR(120) NOT NULL,
  department_id BIGINT FK departments(id),
  semester INT,
  credits INT
)

class_sections(
  id BIGINT PK,
  name VARCHAR(50) NOT NULL,
  department_id BIGINT FK departments(id),
  semester INT,
  academic_year VARCHAR(20)
)

subject_assignments(
  id BIGINT PK,
  subject_id BIGINT NOT NULL FK subjects(id),
  faculty_id BIGINT NOT NULL FK faculty(id),
  section_id BIGINT NOT NULL FK class_sections(id),
  UNIQUE(subject_id, faculty_id, section_id)
)

attendance_sessions(
  id BIGINT PK,
  subject_assignment_id BIGINT NOT NULL FK subject_assignments(id),
  attendance_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  session_status VARCHAR(30) NOT NULL,
  created_by BIGINT NOT NULL FK users(id),
  created_at DATETIME NOT NULL
)

selfies(
  id BIGINT PK,
  storage_key VARCHAR(255) NOT NULL,
  file_url VARCHAR(500),
  captured_at DATETIME NOT NULL,
  mime_type VARCHAR(100),
  file_size BIGINT,
  verification_status VARCHAR(30),
  verification_message VARCHAR(255)
)

attendance(
  id BIGINT PK,
  session_id BIGINT NOT NULL FK attendance_sessions(id),
  student_id BIGINT NOT NULL FK students(id),
  status VARCHAR(30) NOT NULL,
  selfie_id BIGINT FK selfies(id),
  submitted_at DATETIME,
  approved_at DATETIME,
  approved_by BIGINT FK users(id),
  rejection_reason VARCHAR(500),
  remarks VARCHAR(500),
  UNIQUE(session_id, student_id)
)

audit_logs(
  id BIGINT PK,
  actor_user_id BIGINT NOT NULL FK users(id),
  action VARCHAR(50) NOT NULL,
  entity_type VARCHAR(80) NOT NULL,
  entity_id BIGINT NOT NULL,
  old_value_summary TEXT,
  new_value_summary TEXT,
  created_at DATETIME NOT NULL
)

## 8. REST API Design

### Authentication
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout

### Admin Users
GET    /api/admin/users
GET    /api/admin/users/{id}
POST   /api/admin/users
PUT    /api/admin/users/{id}
PATCH  /api/admin/users/{id}/status
DELETE /api/admin/users/{id}

### Students
GET    /api/admin/students
GET    /api/admin/students/{id}
POST   /api/admin/students
PUT    /api/admin/students/{id}
DELETE /api/admin/students/{id}

### Subjects / Classes
GET/POST/PUT/DELETE /api/admin/subjects
GET/POST/PUT/DELETE /api/admin/sections
GET/POST/PUT/DELETE /api/admin/assignments

### Attendance Sessions
POST /api/admin/attendance-sessions
GET  /api/admin/attendance-sessions
GET  /api/admin/attendance-sessions/{id}

### Attendance Approval
GET   /api/admin/attendance/pending
PATCH /api/admin/attendance/{id}/approve
PATCH /api/admin/attendance/{id}/reject
PATCH /api/admin/attendance/{id}/mark

### Student Attendance
GET  /api/user/attendance/sessions
POST /api/user/attendance/{sessionId}/submit
GET  /api/user/attendance/history
GET  /api/user/attendance/summary

### Selfie
POST /api/user/attendance/{sessionId}/selfie
GET  /api/admin/selfies/{id}

## 9. Submit Attendance Flow
1. Student logs in.
2. Frontend requests active sessions.
3. Backend verifies that the student belongs to the session's section.
4. Frontend opens camera and captures a selfie.
5. Frontend sends multipart/form-data with selfie.
6. Backend validates file type, size, session timing and duplicate submission.
7. Backend stores the file securely.
8. Backend creates/updates selfie metadata.
9. Backend creates attendance with SUBMITTED status.
10. Admin sees it in pending approvals.
11. Admin approves/rejects.
12. Student sees the updated status.

## 10. Security Requirements
- Store passwords only as BCrypt/Argon2 hashes.
- Use JWT or secure server-side sessions.
- Enforce role-based authorization with Spring Security.
- Never trust role/studentId values sent by the frontend.
- Derive the logged-in user from the authenticated principal.
- Validate ownership of attendance sessions.
- Limit selfie upload size and MIME types.
- Do not store sensitive biometric data unless the college has a clear legal/privacy basis.
- If face matching is later added, treat it as a separate consent-controlled service.
- Add rate limiting for login and attendance submission.
- Audit all admin attendance changes.
- Use HTTPS in production.
- Do not expose filesystem paths or internal exception details.

## 11. Error Response
Use a common format:
{
  "timestamp": "...",
  "status": 400,
  "code": "ATTENDANCE_ALREADY_SUBMITTED",
  "message": "Attendance has already been submitted.",
  "path": "/api/user/attendance/12/submit"
}

## 12. Validation Rules
- One attendance record per student per session.
- Submission allowed only during the configured session window, unless admin overrides.
- Only students assigned to the class section can submit.
- Selfie is required when selfie attendance is enabled.
- Admin approval is required when the session is configured for approval.
- Rejection requires a reason.
- Deleting important attendance records should be restricted; prefer soft delete/audit correction.

## 13. Testing
Unit tests:
- AuthService
- AttendanceService
- ApprovalService
- Validation rules

Integration tests:
- Login
- Student attendance submission
- Duplicate attendance prevention
- Admin approval/rejection
- Admin manual marking

Security tests:
- USER cannot access ADMIN endpoints.
- USER cannot submit for another student.
- ADMIN actions are audited.

## 14. AI Implementation Order
Ask the coding AI to implement in small milestones:
1. Create Spring Boot project.
2. Create entities/enums.
3. Configure MySQL/JPA.
4. Create repositories.
5. Create DTOs and validation.
6. Implement authentication/security.
7. Implement admin CRUD.
8. Implement attendance sessions.
9. Implement student attendance.
10. Implement selfie upload.
11. Implement approval workflow.
12. Add audit logging.
13. Add tests.
14. Add Swagger/OpenAPI.
15. Configure production settings.

Do not ask AI to generate the whole application in one prompt. Build and test one module at a time.
