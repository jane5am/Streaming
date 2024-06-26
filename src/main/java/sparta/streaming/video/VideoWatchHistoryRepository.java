package sparta.streaming.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.streaming.domain.video.VideoWatchHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoWatchHistoryRepository extends JpaRepository<VideoWatchHistory, Integer> {
    List<VideoWatchHistory> findByVideoIdAndUserId(int videoId, Long userId);
}
