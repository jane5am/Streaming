package sparta.streaming.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.streaming.domain.video.AdWatchHistory;

import java.util.Optional;

@Repository
public interface AdWatchHistoryRepository extends JpaRepository<AdWatchHistory, Integer> {
    Optional<AdWatchHistory> findByVideoIdAndAdIdAndUserId(int videoId, int adId, Long userId);

}