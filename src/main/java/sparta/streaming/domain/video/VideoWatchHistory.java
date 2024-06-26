package sparta.streaming.domain.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoWatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int videoWatchHistoryId ;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "videoId", nullable = false)
    private int videoId;

    @Column(name = "playbackPosition", nullable = false)
    private int playbackPosition; // 마지막 재생 시점

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "viewDate", nullable = false)
    private LocalDateTime viewDate; // 시청시간 (기본값 현재시간)

    @Column(name = "sourceIP", nullable = false)
    private String sourceIP; // 시청한 사용자 IP주소


    public VideoWatchHistory( Long userId, int videoId, int playbackPosition, LocalDateTime viewDate, String sourceIP) {
        this.userId = userId;
        this.videoId = videoId;
        this.playbackPosition = playbackPosition;
        this.viewDate = viewDate;
        this.sourceIP = sourceIP;
    }

    @PrePersist
    protected void onCreate() {
        this.viewDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

}