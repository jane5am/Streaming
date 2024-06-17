package sparta.streaming.user;

import sparta.streaming.domain.User;
import sparta.streaming.dto.user.PutUserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }



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

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
