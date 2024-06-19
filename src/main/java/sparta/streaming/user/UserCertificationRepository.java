package sparta.streaming.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.streaming.domain.UserCertification;

@Repository
public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {

}
