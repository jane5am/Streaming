package sparta.streaming.user;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sparta.streaming.domain.User;
import sparta.streaming.dto.ResponseMessage;
import sparta.streaming.dto.user.CreateUserRequestDto;
import sparta.streaming.dto.user.PutUserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.streaming.dto.user.UserCommonDto;
import sparta.streaming.user.provider.JwtProvider;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;
    private final JwtProvider jwtProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody CreateUserRequestDto createUserRequestDto) throws BadRequestException {

        User createdUser = userService.createUser(createUserRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdUser)
                .statusCode(201)
                .resultMessage("User created successfully")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody UserCommonDto userCommonDto) throws BadRequestException {

        String token = userService.login(userCommonDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(token)// 토큰 다시 준거
                .statusCode(200)
                .resultMessage("Login successful")
                .build();

        return ResponseEntity.ok(response);
    }

    // 유저 전체 조회
    @GetMapping
    public ResponseEntity<ResponseMessage> getAllUsers() {

        List<User> users = userService.getAllUsers();

        ResponseMessage response = ResponseMessage.builder()
                .data(users)
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }

    // id로 유저 찾기
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getUserById(@PathVariable("id") Long id) throws BadRequestException {

        Optional<User> user = userService.getUserById(id);

        ResponseMessage response = ResponseMessage.builder()
                .data(user.get())
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }


    //회원 수정
    @PutMapping()
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody PutUserRequestDto putUserRequestDto) {

        User updatedUser = userService.updateUser(putUserRequestDto); //exception은 서비스에서 내주기

        ResponseMessage response = ResponseMessage.builder()
                .data(updatedUser)
                .statusCode(200)
                .resultMessage("User updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }


    // 회원삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable("id") Long id) {

        userService.deleteUser(id);

        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("User deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

}