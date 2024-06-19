package sparta.streaming.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateUserRequestDto extends UserCommonDto {
//    private String userName;

//    private String email;
//    private String password;
    private String userName;  // 필드 이름을 userName으로 사용


}