package nikhai.com.Sourabh.repository;

import nikhai.com.Sourabh.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEnrollmentNo(String enrollmentNo);
    Optional<Student> findByUserId(Long userId);
    boolean existsByEnrollmentNo(String enrollmentNo);
}
