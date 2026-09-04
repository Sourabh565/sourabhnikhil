package nikhai.com.Sourabh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nikhai.com.Sourabh.dto.*;
import nikhai.com.Sourabh.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    // Department endpoints
    @PostMapping("/departments")
    @Operation(summary = "Create department", description = "Create a new department")
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@RequestBody DepartmentDto dto) {
        try {
            DepartmentDto result = adminService.createDepartment(dto);
            return ResponseEntity.ok(ApiResponse.success("Department created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/departments")
    @Operation(summary = "Get all departments", description = "Retrieve all departments")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> departments = adminService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", departments));
    }
    
    // Student endpoints
    @PostMapping("/students")
    @Operation(summary = "Create student", description = "Create a new student")
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(@RequestBody StudentDto dto) {
        try {
            // Password should come from request or be generated
            StudentDto result = adminService.createStudent(dto, "defaultPassword123");
            return ResponseEntity.ok(ApiResponse.success("Student created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/students")
    @Operation(summary = "Get all students", description = "Retrieve all students")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents() {
        List<StudentDto> students = adminService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }
    
    // Subject endpoints
    @PostMapping("/subjects")
    @Operation(summary = "Create subject", description = "Create a new subject")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@RequestBody SubjectDto dto) {
        try {
            SubjectDto result = adminService.createSubject(dto);
            return ResponseEntity.ok(ApiResponse.success("Subject created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/subjects")
    @Operation(summary = "Get all subjects", description = "Retrieve all subjects")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getAllSubjects() {
        List<SubjectDto> subjects = adminService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully", subjects));
    }
    
    // Class Section endpoints
    @PostMapping("/sections")
    @Operation(summary = "Create class section", description = "Create a new class section")
    public ResponseEntity<ApiResponse<ClassSectionDto>> createClassSection(@RequestBody ClassSectionDto dto) {
        try {
            ClassSectionDto result = adminService.createClassSection(dto);
            return ResponseEntity.ok(ApiResponse.success("Class section created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/sections")
    @Operation(summary = "Get all class sections", description = "Retrieve all class sections")
    public ResponseEntity<ApiResponse<List<ClassSectionDto>>> getAllClassSections() {
        List<ClassSectionDto> sections = adminService.getAllClassSections();
        return ResponseEntity.ok(ApiResponse.success("Class sections retrieved successfully", sections));
    }
    
    // Attendance Session endpoints
    @PostMapping("/attendance-sessions")
    @Operation(summary = "Create attendance session", description = "Create a new attendance session")
    public ResponseEntity<ApiResponse<AttendanceSessionDto>> createAttendanceSession(
            @RequestBody(required = false) AttendanceSessionDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // For now, we'll need to extract userId from token or use a different approach
            Long userId = 1L; // Placeholder - should be extracted from JWT
            AttendanceSessionDto result = adminService.createAttendanceSession(dto, userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance session created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/attendance-sessions")
    @Operation(summary = "Get all attendance sessions", description = "Retrieve all attendance sessions")
    public ResponseEntity<ApiResponse<List<AttendanceSessionDto>>> getAllAttendanceSessions() {
        List<AttendanceSessionDto> sessions = adminService.getAllAttendanceSessions();
        return ResponseEntity.ok(ApiResponse.success("Attendance sessions retrieved successfully", sessions));
    }
    
    // Approval endpoints
    @GetMapping("/attendance/pending")
    @Operation(summary = "Get pending attendances", description = "Retrieve all pending attendance records for approval")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getPendingAttendances() {
        List<AttendanceDto> attendances = adminService.getPendingAttendances();
        return ResponseEntity.ok(ApiResponse.success("Pending attendances retrieved successfully", attendances));
    }
    
    @PatchMapping("/attendance/{id}/approve")
    @Operation(summary = "Approve attendance", description = "Approve a pending attendance record")
    public ResponseEntity<ApiResponse<Void>> approveAttendance(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = 1L; // Placeholder - should be extracted from JWT
            adminService.approveAttendance(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance approved successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/attendance/{id}/reject")
    @Operation(summary = "Reject attendance", description = "Reject a pending attendance record with reason")
    public ResponseEntity<ApiResponse<Void>> rejectAttendance(
            @PathVariable Long id,
            @RequestBody ApprovalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = 1L; // Placeholder - should be extracted from JWT
            adminService.rejectAttendance(id, request.getRejectionReason(), userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance rejected successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
