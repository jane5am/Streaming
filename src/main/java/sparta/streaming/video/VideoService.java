package sparta.streaming.video;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import sparta.streaming.domain.video.*;
import sparta.streaming.dto.video.VideoCommonDto;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class VideoService {

    private static final int AD_INTERVAL = 5 * 60; // 300초 (5분) 당 한 개의 광고
    private static final int AD_PLAYTIME = 30; // 광고 길이를 30초로 설정 (필요에 따라 조정 가능)

    @Autowired
    private final VideoRepository videoRepository;
    private final VideoWatchHistoryRepository videoWatchHistoryRepository;
    private final AdWatchHistoryRepository adWatchHistoryRepository;
    private final VideoAdRepository videoAdRepository;
    private final AdRepository adRepository;
    private final RedisService redisService;

    // 동영상 등록
    public Video createVideo(VideoCommonDto videoCommonDto, Long creator) {
        Video video = new Video();
        video.setCreator(creator);
        video.setTitle(videoCommonDto.getTitle());
        video.setPlayTime(videoCommonDto.getPlayTime());

        Video savedVideo = videoRepository.save(video);

        int adCount = videoCommonDto.getPlayTime() / AD_INTERVAL;
        List<Ad> adList = adRepository.findAll();
        Random random = new Random();

        for (int i = 1; i < adCount; i++) {
            if ((Math.random() > 0.7)) {
                Ad ad = createAd("Ad content for video " + savedVideo.getVideoId() + ", ad " + (i + 1));
                createVideoAd(savedVideo.getVideoId(), ad.getAdId());
            } else { // 있는 거 가져옴
                int randomAdId = random.nextInt(adList.size()) + 1;
                createVideoAd(savedVideo.getVideoId(), randomAdId);
            }
        }

        return videoRepository.save(video);
    }

    private Ad createAd(String content) {
        Ad ad = new Ad();
        ad.setContent(content);
        ad.setPlayTime(AD_PLAYTIME);
        return adRepository.save(ad);
    }

    private void createVideoAd(int videoId, int adId) {
        VideoAd videoAd = new VideoAd();
        videoAd.setVideoId(videoId);
        videoAd.setAdId(adId);
        videoAdRepository.save(videoAd);
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

        // 유효한 사용자인지 확인
        // 1. videoId에서 creator과 userId이 같은지 확인한다. 같으면 throw
        // false
        boolean checkUser = !Objects.equals(userId, videoRepository.findByVideoId(videoId).get().getCreator());
        if (!checkUser) {
            throw new RuntimeException("User is the creator of the video");
        }

        // 2. sourceIP를 redis에 저장한다. redis에 이미 존재하는 sourceIP일경우 throw
        // false
        if (redisService.getData(sourceIP) != null) {
            throw new RuntimeException("Source IP already exists in Redis");
        } else {
            redisService.saveDataWithTTL(sourceIP, 1, 30, TimeUnit.SECONDS);
        }
        System.out.println("redisService.getData(sourceIP) : " + redisService.getData(sourceIP));


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
        List<AdWatchHistory> adWatchHistories = adWatchHistoryRepository.findByVideoIdAndUserId(videoId, userId);
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
            // playTime을 초과하면 정지하고 playbackPosition을 영상의 최대 시간으로 설정
            latestWatchHistory.setPlaybackPosition(video.getPlayTime());
        } else {
            // playTime을 초과하지 않으면 경과 시간을 playbackPosition에 설정
            latestWatchHistory.setPlaybackPosition(latestWatchHistory.getPlaybackPosition() + elapsedTime);
        }

        videoWatchHistoryRepository.save(latestWatchHistory);

        List<VideoWatchHistory> updatedWatchHistories = videoWatchHistoryRepository.findByVideoIdAndUserId(videoId, userId);

        // 광고 시청 기록 추가
        int totalPlaybackTime = latestWatchHistory.getPlaybackPosition();
//        int adInterval = 5 * 60; // 5분 단위로 광고가 붙음
        int adInterval = 10;

        List<VideoAd> videoAds = videoAdRepository.findByVideoId(videoId);

        // 유저가 시청할 수 있는 최대 광고 숫자
        int maxAdsToWatch = updatedWatchHistories.size() * videoAds.size();

        for (int i = adWatchHistories.size(); i < maxAdsToWatch; i++) {
            int adPosition = ((i % videoAds.size()) + 1) * adInterval;
            if (totalPlaybackTime >= adPosition) {
                saveAdWatchHistory(videoId, userId, sourceIP, videoAds.get(i % videoAds.size()).getAdId());
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
