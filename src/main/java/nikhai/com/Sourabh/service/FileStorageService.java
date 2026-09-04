package nikhai.com.Sourabh.service;

import nikhai.com.Sourabh.entity.Selfie;
import nikhai.com.Sourabh.enums.VerificationStatus;
import nikhai.com.Sourabh.repository.SelfieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.storage.path}")
    private String storagePath;
    
    private final SelfieRepository selfieRepository;
    
    public FileStorageService(SelfieRepository selfieRepository) {
        this.selfieRepository = selfieRepository;
    }
    
    public Selfie storeSelfie(MultipartFile file, Long attendanceId) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Store file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // Create selfie metadata
        Selfie selfie = new Selfie();
        selfie.setStorageKey(uniqueFilename);
        selfie.setFileUrl(filePath.toString());
        selfie.setCapturedAt(LocalDateTime.now());
        selfie.setMimeType(file.getContentType());
        selfie.setFileSize(file.getSize());
        selfie.setVerificationStatus(VerificationStatus.NOT_APPLICABLE);
        
        return selfieRepository.save(selfie);
    }
    
    public byte[] getFile(String storageKey) throws IOException {
        Path filePath = Paths.get(storagePath).resolve(storageKey);
        return Files.readAllBytes(filePath);
    }
}
