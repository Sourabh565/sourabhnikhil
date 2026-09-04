package nikhai.com.Sourabh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nikhai.com.Sourabh.enums.VerificationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "selfies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Selfie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String storageKey;

    @Column(length = 500)
    private String fileUrl;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 30)
    private VerificationStatus verificationStatus;

    @Column(name = "verification_message", length = 255)
    private String verificationMessage;
}
