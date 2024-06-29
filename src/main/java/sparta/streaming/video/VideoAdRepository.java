package sparta.streaming.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.streaming.domain.video.VideoAd;

import java.util.List;

@Repository
public interface VideoAdRepository extends JpaRepository<VideoAd, Integer>  {
    List<VideoAd> findByVideoId(int videoId);
}
