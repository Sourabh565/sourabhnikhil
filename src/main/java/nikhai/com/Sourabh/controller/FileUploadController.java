package nikhai.com.Sourabh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import nikhai.com.Sourabh.dto.ApiResponse;
import nikhai.com.Sourabh.entity.Selfie;
import nikhai.com.Sourabh.service.FileStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/attendance")
@Tag(name = "File Upload", description = "File upload management APIs")
@SecurityRequirement(name = "bearerAuth")
public class FileUploadController {
    
    private final FileStorageService fileStorageService;
    
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    
    @PostMapping("/{sessionId}/selfie")
    @Operation(summary = "Upload selfie", description = "Upload a selfie image for attendance verification")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @io.swagger.v3.oas.annotations.media.Content(
        mediaType = "multipart/form-data",
        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary")
    ))
    public ResponseEntity<ApiResponse<Selfie>> uploadSelfie(
            @PathVariable Long sessionId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File is empty"));
            }
            
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Only image files are allowed"));
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File size exceeds 10MB limit"));
            }
            
            Selfie selfie = fileStorageService.storeSelfie(file, sessionId);
            return ResponseEntity.ok(ApiResponse.success("Selfie uploaded successfully", selfie));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/selfie/{storageKey}")
    @Operation(summary = "Get selfie", description = "Retrieve a selfie image by storage key")
    public ResponseEntity<byte[]> getSelfie(@PathVariable String storageKey) {
        try {
            byte[] fileContent = fileStorageService.getFile(storageKey);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storageKey + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
