package sparta.streaming.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sparta.streaming.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> { // 엔티티 클래스, PK의 타입
    Optional<User> findByEmail(String email);

    User findByUserId(Long userId);

//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.email = :email")
//    boolean existsByEmail(@Param("email") String email);
//
//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.userName = :userName")
//    boolean existsByUserName(@Param("userName") String userName);


}