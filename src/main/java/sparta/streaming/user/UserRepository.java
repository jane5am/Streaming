package sparta.streaming.user;

import sparta.streaming.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> { // 엔티티 클래스, PK의 타입
    Optional<User> findByEmail(String email);

    User findByUserId(Long userId);
}