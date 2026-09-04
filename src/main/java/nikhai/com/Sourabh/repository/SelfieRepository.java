package nikhai.com.Sourabh.repository;

import nikhai.com.Sourabh.entity.Selfie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelfieRepository extends JpaRepository<Selfie, Long> {
}
