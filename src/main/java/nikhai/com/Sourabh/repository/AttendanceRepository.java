package nikhai.com.Sourabh.repository;

import nikhai.com.Sourabh.entity.Attendance;
import nikhai.com.Sourabh.entity.AttendanceSession;
import nikhai.com.Sourabh.entity.Student;
import nikhai.com.Sourabh.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findBySessionAndStudent(AttendanceSession session, Student student);
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByStatus(AttendanceStatus status);
    List<Attendance> findBySession(AttendanceSession session);
}
