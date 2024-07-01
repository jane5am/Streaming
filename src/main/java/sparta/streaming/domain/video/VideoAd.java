package sparta.streaming.domain.video;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoAd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int videoAdId;

    @Column(nullable = false)
    private int videoId;

    @Column(nullable = false)
    private int adId;

}