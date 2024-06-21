package sparta.streaming.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import sparta.streaming.domain.User;
import sparta.streaming.dto.user.PutUserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.streaming.user.provider.JwtProvider;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //회원가입
    public User createUser(User user) {

        return userRepository.save(user);
    }


    // 로그인
    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    // 이메일 중복확인
    public Optional<User> idCheck(String email) {
        return userRepository.findByEmail(email);
    }


    // 전체 유저 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // id로 유저 찾기
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }


    // 회원 수정
    public User updateUser(PutUserRequestDto putUserRequestDto) {
        Optional<User> optionalUser = userRepository.findById(putUserRequestDto.getUserId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(putUserRequestDto.getEmail());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + putUserRequestDto.getUserId());
        }
    }

    // 회원삭제
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }



}