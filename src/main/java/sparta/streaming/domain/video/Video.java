package sparta.streaming.domain.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int videoId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(name = "playTime", nullable = false)
    private int playTime; // 동영상 총 길이

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadDate; // 업로드 날짜

    @PrePersist
    protected void onCreate() {
        this.uploadDate = new Date();
    }

}
