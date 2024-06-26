package sparta.streaming.video;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import sparta.streaming.domain.user.User;
import sparta.streaming.domain.video.Video;
import sparta.streaming.dto.video.CreateVideoRequestDto;
import sparta.streaming.dto.video.UpdateVideoRequestDto;
import sparta.streaming.dto.video.VideoCommonDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VideoService {

    @Autowired
    private final VideoRepository videoRepository;

    // 동영상 등록
    public Video createVideo(VideoCommonDto videoCommonDto, Long userId) {
        Video video = new Video();
        video.setUserId(userId);
        video.setTitle(videoCommonDto.getTitle());
        video.setPlayTime(videoCommonDto.getPlayTime());
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
        video.setPlayTime(videoCommonDto.getPlayTime());
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
        List<Video> videoOptional = videoRepository.findAllByUserId(userId);
        System.out.println("userId : " + userId);
        System.out.println("videoOptional : " + videoOptional);
        if (videoOptional.isEmpty()) {// 비디오id가 우리 db에 있는지 확인
            throw new RuntimeException("Video not found with id " + userId);
        }

        return videoRepository.findAllByUserId(userId);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }


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
