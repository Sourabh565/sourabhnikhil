package nikhai.com.Sourabh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nikhai.com.Sourabh.dto.ApiResponse;
import nikhai.com.Sourabh.dto.AttendanceDto;
import nikhai.com.Sourabh.dto.AttendanceSessionDto;
import nikhai.com.Sourabh.dto.AttendanceSubmitRequest;
import nikhai.com.Sourabh.security.JwtUtil;
import nikhai.com.Sourabh.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Student", description = "Student attendance management APIs")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {
    
    private final StudentService studentService;
    private final JwtUtil jwtUtil;
    
    public StudentController(StudentService studentService, JwtUtil jwtUtil) {
        this.studentService = studentService;
        this.jwtUtil = jwtUtil;
    }
    
    private Long extractUserId(UserDetails userDetails, String token) {
        // In production, extract from JWT token
        return 1L; // Placeholder
    }
    
    @GetMapping("/attendance/sessions")
    @Operation(summary = "Get active sessions", description = "Retrieve all active attendance sessions for the student")
    public ResponseEntity<ApiResponse<List<AttendanceSessionDto>>> getActiveSessions(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            List<AttendanceSessionDto> sessions = studentService.getActiveSessions(userId);
            return ResponseEntity.ok(ApiResponse.success("Active sessions retrieved successfully", sessions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/attendance/{sessionId}/submit")
    @Operation(summary = "Submit attendance", description = "Submit attendance for a specific session")
    public ResponseEntity<ApiResponse<AttendanceDto>> submitAttendance(
            @PathVariable Long sessionId,
            @RequestBody AttendanceSubmitRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            request.setSessionId(sessionId);
            Long userId = jwtUtil.extractUserId(token.substring(7));
            AttendanceDto result = studentService.submitAttendance(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Attendance submitted successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/attendance/history")
    @Operation(summary = "Get attendance history", description = "Retrieve attendance history for the student")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getAttendanceHistory(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            List<AttendanceDto> history = studentService.getAttendanceHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance history retrieved successfully", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/attendance/summary")
    @Operation(summary = "Get attendance summary", description = "Retrieve attendance summary statistics for the student")
    public ResponseEntity<ApiResponse<AttendanceDto>> getAttendanceSummary(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            AttendanceDto summary = studentService.getAttendanceSummary(userId);
            return ResponseEntity.ok(ApiResponse.success("Attendance summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
