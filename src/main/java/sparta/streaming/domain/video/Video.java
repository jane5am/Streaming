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
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int videoId;

    @Column(name = "creator", nullable = false)
    private Long creator;

    @Column(nullable = false)
    private String title;

    @Column(name = "playTime", nullable = false)
    private int playTime; // 동영상 길이

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadDate;

    @Column(nullable = false)
    private int views;

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.views = 0; // 기본값을 0으로 설정
    }

}
