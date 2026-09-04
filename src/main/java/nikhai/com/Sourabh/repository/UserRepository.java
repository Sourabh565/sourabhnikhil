package nikhai.com.Sourabh.repository;

import nikhai.com.Sourabh.entity.User;
import nikhai.com.Sourabh.enums.Role;
import nikhai.com.Sourabh.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
