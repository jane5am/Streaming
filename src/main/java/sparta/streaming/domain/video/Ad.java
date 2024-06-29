package sparta.streaming.domain.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int adId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false)
    private String content; // 광고 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "uploadDate", nullable = false)
    private LocalDateTime uploadDate; // 업로드 날짜

    @Column(nullable = false)
    private int playTime;// 광고 길이

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}