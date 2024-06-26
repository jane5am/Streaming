package sparta.streaming.video;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.streaming.domain.user.CustomUserDetails;
import sparta.streaming.domain.video.Video;
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
    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateVideo(@RequestBody VideoCommonDto videoCommonDto,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getId();

        Video updatedVideo = videoService.updateVideo(videoCommonDto.getVideoId(), videoCommonDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(updatedVideo)
                .statusCode(200)
                .resultMessage("Video updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }
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





}
