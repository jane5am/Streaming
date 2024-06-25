package sparta.streaming.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.streaming.domain.user.User;
import sparta.streaming.domain.video.Video;
import sparta.streaming.domain.video.VideoWatchHistory;
import sparta.streaming.dto.video.CreateVideoRequestDto;
import sparta.streaming.dto.video.UpdateVideoRequestDto;
import sparta.streaming.dto.video.VideoCommonDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private final VideoRepository videoRepository;
//    private VideoWatchHistoryRepository videoWatchHistoryRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // 동영상 등록
    public Video createVideo(CreateVideoRequestDto createVideoRequestDto) {
        Video video = new Video();
        video.setUserId(createVideoRequestDto.getUserId());
        video.setPlayTime(createVideoRequestDto.getPlayTime());
        System.out.println(video);
        return videoRepository.save(video);
    }
//
//    public Video updateVideo(Long videoId, UpdateVideoRequestDto updateVideoRequestDto) {
//        Optional<Video> videoOptional = videoRepository.findById(videoId);
//        if (videoOptional.isPresent()) {
//            Video video = videoOptional.get();
//            video.setPlayTime(updateVideoRequestDto.getPlayTime());
//            video.setUploadDate(updateVideoRequestDto.getUploadDate());
//            return videoRepository.save(video);
//        } else {
//            throw new RuntimeException("Video not found with id " + videoId);
//        }
//    }
//
//    public void deleteVideo(Long videoId) {
//        videoRepository.deleteById(videoId);
//    }
//
//    public Video getVideoById(Long videoId) {
//        return videoRepository.findById(videoId)
//                .orElseThrow(() -> new RuntimeException("Video not found with id " + videoId));
//    }
//
//    public List<Video> getAllVideos() {
//        return videoRepository.findAll();
//    }





//    /**
//     * 특정 사용자와 동영상에 대한 시청 기록을 반환합니다.
//     * 기록이 없으면 새로 생성합니다.
//     */
//    public VideoWatchHistory playVideo(Long videoId, Long userId) {
//
//        System.out.println(videoWatchHistoryRepository.findByVideoIdAndUserId(videoId, userId));
//        Optional<VideoWatchHistory> watchHistory = videoWatchHistoryRepository.findByVideoIdAndUserId(videoId, userId);
//        System.out.println(watchHistory);
//        return watchHistory.orElseGet(() -> new VideoWatchHistory(videoId, userId, 0,LocalDateTime.now(),"127.0.0.1"));
//    }
//    /**
//     * 동영상 재생을 중단할 때 호출됩니다.
//     * 현재 재생 위치를 시청 기록에 저장합니다.
//     */
////    public VideoWatchHistory saveWatchHistory(VideoWatchHistory watchHistory) {
////        return videoRepository.save(watchHistory);
////    }
////



}
