package sparta.streaming.video;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import sparta.streaming.domain.video.AdWatchHistory;
import sparta.streaming.domain.video.Video;
import sparta.streaming.domain.video.VideoAd;
import sparta.streaming.domain.video.VideoWatchHistory;
import sparta.streaming.dto.video.VideoCommonDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VideoService {

    @Autowired
    private final VideoRepository videoRepository;
    private final VideoWatchHistoryRepository videoWatchHistoryRepository;
    private final AdWatchHistoryRepository adWatchHistoryRepository;
    private final VideoAdRepository videoAdRepository;

    // 동영상 등록
    public Video createVideo(VideoCommonDto videoCommonDto, Long creator) {
        Video video = new Video();
        video.setCreator(creator);
        video.setTitle(videoCommonDto.getTitle());
        video.setPlayTime(videoCommonDto.getPlayTime());
        System.out.println("video.getUserId()" + video.getCreator());
        return videoRepository.save(video);
    }

    // 동영상 수정
    public Video updateVideo(int videoId, VideoCommonDto videoCommonDto, Long creator) {

        Optional<Video> videoOptional = videoRepository.findByVideoId(videoId);

        if (videoOptional.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + videoId);
        }

        if (!creator.equals(videoOptional.get().getCreator())) { // 요청한 사람, 비디오 올린사람 id
            throw new AccessDeniedException("You do not have permission to access this video.");
        }

        Video video = videoOptional.get();
        video.setPlayTime(videoCommonDto.getPlayTime());
        video.setTitle(videoCommonDto.getTitle());
        return videoRepository.save(video);

    }

    // 동영상 삭제
    public void deleteVideo(int videoId, Long creator) {
        Optional<Video> videoOptional = videoRepository.findByVideoId(videoId);

        if (videoOptional.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + videoId);
        }

        if (!creator.equals(videoOptional.get().getCreator())) { // 요청한 사람, 비디오 올린사람 id
            throw new AccessDeniedException("You do not have permission to access this video.");
        }

        videoRepository.deleteById(videoId);
    }

    //userId로 올린 동영상 찾기
    public List<Video> getVideoByUserId(Long creator) {
        List<Video> video = videoRepository.findAllByCreator(creator);

        if (video.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + creator);
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

            // 이전에 모두 봤을 경우 새로운 row 생성
            if (latestWatchHistory.getPlaybackPosition() == videoOptional.get().getPlayTime()) {
                VideoWatchHistory watchHistory = new VideoWatchHistory(userId, videoId, 0, LocalDateTime.now(), sourceIP);
                return videoWatchHistoryRepository.save(watchHistory);
            } else {
                latestWatchHistory.setViewDate(LocalDateTime.now());
                latestWatchHistory.setSourceIP(sourceIP);
                return videoWatchHistoryRepository.save(latestWatchHistory);
            }
        } else {
            // 최초 시청인 경우
            VideoWatchHistory watchHistory = new VideoWatchHistory(userId, videoId, 0, LocalDateTime.now(), sourceIP);
            return videoWatchHistoryRepository.save(watchHistory);
        }
    }


    //동영상 정지
    public void updatePlaybackPosition(int videoId, Long userId, String sourceIP) {
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

        if (latestWatchHistory.getPlaybackPosition() + elapsedTime >= video.getPlayTime()) {
            // playTime을 초과하면 정지하고 playbackPosition을 0으로 설정
            latestWatchHistory.setPlaybackPosition(videoOptional.get().getPlayTime());
        } else {
            // playTime을 초과하지 않으면 경과 시간을 playbackPosition에 설정
            latestWatchHistory.setPlaybackPosition(latestWatchHistory.getPlaybackPosition() + elapsedTime);
        }

        videoWatchHistoryRepository.save(latestWatchHistory);

        // 광고 시청 기록 추가
        int totalPlaybackTime = latestWatchHistory.getPlaybackPosition();
        System.out.println("totalPlaybackTime : " + totalPlaybackTime);
//        int adInterval = 5 * 60; // 5분 단위로 광고가 붙음
        int adInterval = 30; // 5분 단위로 광고가 붙음

        List<VideoAd> videoAds = videoAdRepository.findByVideoId(videoId);
        System.out.println("videoAds : " + videoAds);
        for (int i = 0; i < videoAds.size(); i++) {
            int adPosition = (i + 1) * adInterval;
            if (totalPlaybackTime >= adPosition) {
                saveAdWatchHistory(videoId, userId, sourceIP, videoAds.get(i).getAdId());
            }
        }

    }

    // 광고 재생
    // 비디오가 재생되고 비디오 길이가 5분 이상이고 재생시간이 5분이상이다 그러면 해당 메소드 출력
    // 광고 시청기록에 save
    public void saveAdWatchHistory(int videoId, Long userId, String sourceIP, int adId) {
        AdWatchHistory adWatchHistory = new AdWatchHistory();
        adWatchHistory.setVideoId(videoId);
        adWatchHistory.setAdId(adId);
        adWatchHistory.setUserId(userId);
        adWatchHistory.setViewDate(LocalDateTime.now());
        adWatchHistory.setSourceIP(sourceIP);
        adWatchHistoryRepository.save(adWatchHistory);

    }


}
