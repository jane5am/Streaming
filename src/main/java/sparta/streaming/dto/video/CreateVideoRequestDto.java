package sparta.streaming.dto.video;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateVideoRequestDto extends VideoCommonDto {

    private Long userId;
    private int length;

}
