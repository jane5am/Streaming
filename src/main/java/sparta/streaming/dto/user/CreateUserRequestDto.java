package sparta.streaming.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateUserRequestDto extends UserCommonDto {

    private String name;  // 필드 이름을 name으로 사용
//    private String certificationNumber;
}