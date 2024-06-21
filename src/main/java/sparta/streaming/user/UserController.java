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
    public ResponseEntity<ResponseMessage> getUserById(@PathVariable("id") Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            ResponseMessage response = ResponseMessage.builder()
                    .data(user.get())
                    .statusCode(200)
                    .resultMessage("Success")
                    .build();
            return ResponseEntity.ok(response);
        } else { //else부분을 서비스에 넣기
            ResponseMessage response = ResponseMessage.builder()
                    .statusCode(404)
                    .resultMessage("User not found")
                    .build();
            return ResponseEntity.status(404).body(response);
        }
    }

    //회원 수정
    @PutMapping()
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody PutUserRequestDto userDetails) {
        try {
            User updatedUser = userService.updateUser(userDetails); //exception은 서비스에서 내주기
            //로직이랑 에러랑 붙어있는게 좋다
            //에러는 어떤행동이 끝났을때 바로 !
            ResponseMessage response = ResponseMessage.builder()
                    .data(updatedUser)
                    .statusCode(200)
                    .resultMessage("User updated successfully")
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ResponseMessage response = ResponseMessage.builder()
                    .statusCode(404)
                    .resultMessage("User not found")
                    .detailMessage(e.getMessage())
                    .build();
            return ResponseEntity.status(404).body(response);
        }
    }

    // 회원삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            ResponseMessage response = ResponseMessage.builder()
                    .statusCode(204)
                    .resultMessage("User deleted successfully")
                    .build();
            return ResponseEntity.status(204).body(response);
        } catch (RuntimeException e) {
            ResponseMessage response = ResponseMessage.builder()
                    .statusCode(404)
                    .resultMessage("User not found")
                    .detailMessage(e.getMessage())
                    .build();
            return ResponseEntity.status(404).body(response);
            // 404같은 에러중복은 메소드 따로 내주기
        }
    }
}