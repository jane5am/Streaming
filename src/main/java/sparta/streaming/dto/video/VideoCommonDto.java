package sparta.streaming.dto.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCommonDto {

    private String title;
    private int length;
    private int videoId;

}
