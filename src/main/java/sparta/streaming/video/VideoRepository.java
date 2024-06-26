package sparta.streaming.video;

import org.springframework.stereotype.Repository;
import sparta.streaming.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByVideoId(int videoId);
}
