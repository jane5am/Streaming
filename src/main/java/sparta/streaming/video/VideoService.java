package sparta.streaming.video;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import sparta.streaming.domain.user.User;
import sparta.streaming.domain.video.Video;
import sparta.streaming.domain.video.VideoWatchHistory;
import sparta.streaming.dto.video.CreateVideoRequestDto;
import sparta.streaming.dto.video.UpdateVideoRequestDto;
import sparta.streaming.dto.video.VideoCommonDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VideoService {

    @Autowired
    private final VideoRepository videoRepository;
    private final VideoWatchHistoryRepository videoWatchHistoryRepository;

    // 동영상 등록
    public Video createVideo(VideoCommonDto videoCommonDto, Long userId) {
        Video video = new Video();
        video.setUserId(userId);
        video.setTitle(videoCommonDto.getTitle());
        video.setLength(videoCommonDto.getLength());
        System.out.println("video.getUserId()" + video.getUserId());
        return videoRepository.save(video);
    }

    // 동영상 수정
    public Video updateVideo(int videoId, VideoCommonDto videoCommonDto, Long userId) {

        Optional<Video> videoOptional = videoRepository.findByVideoId(videoId);

        if (videoOptional.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + videoId);
        }

        if (!userId.equals(videoOptional.get().getUserId())) { // 요청한 사람, 비디오 올린사람 id
            throw new AccessDeniedException("You do not have permission to access this video.");
        }

        Video video = videoOptional.get();
        video.setLength(videoCommonDto.getLength());
        video.setTitle(videoCommonDto.getTitle());
        return videoRepository.save(video);

    }

    // 동영상 삭제
    public void deleteVideo(int videoId, Long userId) {
        Optional<Video> videoOptional = videoRepository.findByVideoId(videoId);

        if (videoOptional.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + videoId);
        }

        if (!userId.equals(videoOptional.get().getUserId())) { // 요청한 사람, 비디오 올린사람 id
            throw new AccessDeniedException("You do not have permission to access this video.");
        }

        videoRepository.deleteById(videoId);
    }

    //userId로 올린 동영상 찾기
    public List<Video> getVideoByUserId(Long userId) {
        List<Video> video = videoRepository.findAllByUserId(userId);

        if (video.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + userId);
        }

        return video;
    }

    // 모든 동영상 조회
    public List<Video> getAllVideos() {

        return videoRepository.findAll();
    }

    // 동영상 재생
    public VideoWatchHistory playVideo(int videoId, Long userId, String sourceIP) {
        List<VideoWatchHistory> watchHistories = videoWatchHistoryRepository.findByVideoIdAndUserId(videoId, userId);

        // 비디오가 존재하는지 확인
        Optional<Video> videoOptional = videoRepository.findById(videoId);
        if (videoOptional.isEmpty()) {
            throw new RuntimeException("Video not found with id " + videoId);
        }

        if (!watchHistories.isEmpty()) {
            // 시청 기록이 있는 경우, 가장 최근의 시청 기록을 찾음
            VideoWatchHistory latestWatchHistory = watchHistories.get(watchHistories.size() - 1);

            VideoWatchHistory watchHistory ;

            if( latestWatchHistory.getPlaybackPosition() == videoOptional.get().getLength() ){// 이전에 모두봤을 경우
                watchHistory = new VideoWatchHistory( userId,videoId,0, LocalDateTime.now(), sourceIP);

            }else{
                watchHistory = new VideoWatchHistory( userId,videoId, latestWatchHistory.getPlaybackPosition(), LocalDateTime.now(), sourceIP);

            }
            return videoWatchHistoryRepository.save(watchHistory);

        } else {
            // 최초 시청인 경우
            VideoWatchHistory watchHistory = new VideoWatchHistory( userId,videoId, 0, LocalDateTime.now(), sourceIP);
            return videoWatchHistoryRepository.save(watchHistory);
        }
    }

    public void updatePlaybackPosition(int videoId, Long userId) {
        List<VideoWatchHistory> watchHistories = videoWatchHistoryRepository.findByVideoIdAndUserId(videoId, userId);
        Optional<Video> videoOptional = videoRepository.findById(videoId);

        // 비디오가 존재하는지 확인
        if (videoOptional.isEmpty()) {
            throw new RuntimeException("Video not found with id " + videoId);
        }

        // 시청 기록이 있는 경우, 가장 최근의 시청 기록을 찾음
        VideoWatchHistory latestWatchHistory = watchHistories.get(watchHistories.size() - 1);

        // viewDate로부터 현재까지의 경과 시간을 계산
        Duration duration = Duration.between(latestWatchHistory.getViewDate(), LocalDateTime.now());
        int elapsedTime = (int) duration.getSeconds();

        Video video = videoOptional.get();

        if (latestWatchHistory.getPlaybackPosition() + elapsedTime >= video.getLength()) {
            // playTime을 초과하면 정지하고 playbackPosition을 0으로 설정
            latestWatchHistory.setPlaybackPosition(videoOptional.get().getLength());
        } else {
            // playTime을 초과하지 않으면 경과 시간을 playbackPosition에 설정
            latestWatchHistory.setPlaybackPosition(latestWatchHistory.getPlaybackPosition() + elapsedTime);
        }

        videoWatchHistoryRepository.save(latestWatchHistory);


    }


}
