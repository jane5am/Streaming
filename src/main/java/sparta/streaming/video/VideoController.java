package sparta.streaming.video;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.streaming.domain.user.CustomUserDetails;
import sparta.streaming.domain.video.Video;
import sparta.streaming.domain.video.VideoWatchHistory;
import sparta.streaming.dto.ResponseMessage;
import sparta.streaming.dto.video.VideoCommonDto;
import sparta.streaming.user.provider.JwtProvider;

import java.util.List;

@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final JwtProvider jwtProvider;

//    @AuthenticationPrincipal
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createVideo(@RequestBody VideoCommonDto videoCommonDto,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();

        Video createdVideo = videoService.createVideo(videoCommonDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdVideo)
                .statusCode(201)
                .resultMessage("Video created successfully")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    //동영상 수정
    @PutMapping("/update/{videoId}")
    public ResponseEntity<ResponseMessage> updateVideo(@PathVariable("videoId") int videoId, @RequestBody VideoCommonDto videoCommonDto,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();

        Video updatedVideo = videoService.updateVideo(videoId, videoCommonDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(updatedVideo)
                .statusCode(200)
                .resultMessage("Video updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    //동영상 삭제
    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<ResponseMessage> deleteVideo(@PathVariable("videoId") int videoId
                                                    , @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long userId = customUserDetails.getId();

        videoService.deleteVideo(videoId,userId);

        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("Video deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    // user id로 동영상 찾기
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseMessage> getVideoByUserId( @PathVariable("userId") Long userId) {

        List<Video> video = videoService.getVideoByUserId(userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(video)
                .statusCode(200)
                .resultMessage("Video retrieved successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    //모든 동영상 조회
    @GetMapping
    public ResponseEntity<ResponseMessage> getAllVideos() {

        List<Video> videos = videoService.getAllVideos();

        ResponseMessage response = ResponseMessage.builder()
                .data(videos)
                .statusCode(200)
                .resultMessage("Videos retrieved successfully")
                .build();

        return ResponseEntity.ok(response);
    }


    // 동영상 재생
    @GetMapping("/play/{videoId}")
    public ResponseEntity<ResponseMessage> playVideo(@PathVariable("videoId") int videoId,
                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     HttpServletRequest request) {
        String sourceIP = request.getRemoteAddr();
        VideoWatchHistory watchHistory = videoService.playVideo(videoId, customUserDetails.getId(), sourceIP);

        ResponseMessage response = ResponseMessage.builder()
                .data(watchHistory)
                .statusCode(200)
                .resultMessage("Video played successfully")
                .build();

        return ResponseEntity.ok(response);
    }

//    // 동영상 정지
//    @PostMapping("/pause/{videoId}")
//    public ResponseEntity<ResponseMessage> updatePlaybackPosition(@PathVariable("videoId") int videoId,
//                                                                  @RequestParam int playbackPosition,
//                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        videoService.updatePlaybackPosition(videoId, customUserDetails.getId(), playbackPosition);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .statusCode(200)
//                .resultMessage("Playback position updated successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }


    @PostMapping("/{videoId}/pause")
    public ResponseEntity<ResponseMessage> updatePlaybackPosition(@PathVariable("videoId") int videoId,
                                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        videoService.updatePlaybackPosition(videoId, customUserDetails.getId());

        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("Playback position updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}
