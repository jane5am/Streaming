package sparta.streaming.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateUserRequestDto extends UserCommonDto {
    private String userName;
}