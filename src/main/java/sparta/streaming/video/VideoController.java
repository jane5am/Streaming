package sparta.streaming.video;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.streaming.domain.user.CustomUserDetails;
import sparta.streaming.domain.video.Video;
import sparta.streaming.domain.video.VideoWatchHistory;
import sparta.streaming.dto.ResponseMessage;
import sparta.streaming.dto.video.CreateVideoRequestDto;
import sparta.streaming.dto.video.UpdateVideoRequestDto;
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
    public ResponseEntity<ResponseMessage> createVideo(@RequestBody CreateVideoRequestDto createVideoRequestDto,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();
        createVideoRequestDto.setUserId(userId);

        Video createdVideo = videoService.createVideo(createVideoRequestDto);

        System.out.println(createdVideo);
        ResponseMessage response = ResponseMessage.builder()
                .data(createdVideo)
                .statusCode(201)
                .resultMessage("Video created successfully")
                .build();

        return ResponseEntity.status(201).body(response);
    }

//    @PutMapping("/{videoId}/update")
//    public ResponseEntity<ResponseMessage> updateVideo(@PathVariable Long videoId, @RequestBody UpdateVideoRequestDto updateVideoRequestDto) {
//        Video updatedVideo = videoService.updateVideo(videoId, updateVideoRequestDto);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .data(updatedVideo)
//                .statusCode(200)
//                .resultMessage("Video updated successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{videoId}/delete")
//    public ResponseEntity<ResponseMessage> deleteVideo(@PathVariable Long videoId) {
//        videoService.deleteVideo(videoId);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .statusCode(200)
//                .resultMessage("Video deleted successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{videoId}")
//    public ResponseEntity<ResponseMessage> getVideoById(@PathVariable Long videoId) {
//        Video video = videoService.getVideoById(videoId);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .data(video)
//                .statusCode(200)
//                .resultMessage("Video retrieved successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<ResponseMessage> getAllVideos() {
//        List<Video> videos = videoService.getAllVideos();
//
//        ResponseMessage response = ResponseMessage.builder()
//                .data(videos)
//                .statusCode(200)
//                .resultMessage("Videos retrieved successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }





//    //1. 재생 시 기존에 조회했던 영상의 경우 최근 재생 시점부터 재생되고,
//    //   최초 조회의 경우 처음부터 조회 됩니다. 재생 시 해당 영상의 조회수가 증가 합니다.
//    // 동영상 재생
//    @GetMapping("/{videoId}/play")
//    public ResponseEntity<ResponseMessage> playVideo(@RequestBody VideoCommonDto videoCommonDto) {
//        VideoWatchHistory videowatchHistory = videoService.playVideo(videoCommonDto.getVideoId(), videoCommonDto.getUserId());
//        System.out.println(videowatchHistory.getVideoId());
//        System.out.println(videowatchHistory.getUserId());
//        System.out.println(videowatchHistory.getViewDate());
//        System.out.println(videowatchHistory.getSourceIP());
//
//        ResponseMessage response = ResponseMessage.builder()
//                .data(videowatchHistory)
//                .statusCode(200)
//                .resultMessage("Video played successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//
//    }

//    @PostMapping("/{videoId}/pause")
//    public VideoWatchHistory pauseVideo(@PathVariable Long videoId, @RequestParam Long userId, @RequestParam int playbackPosition) {
//        VideoWatchHistory watchHistory = videoService.getWatchHistory(videoId, userId);
//        watchHistory.setPlaybackPosition(playbackPosition);
//        return videoService.saveWatchHistory(watchHistory);
//    }
}
