package sparta.streaming.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sparta.streaming.domain.CustomOAuth2User;
import sparta.streaming.domain.User;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oauthClientName = userRequest.getClientRegistration().getClientName();
        try {
            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        User user = null;
        String type = null;
        String name = "";
        String email = "email@email.com";
        String id = "";
        if( oauthClientName.equals("kakao")){
//            type = "kakao_" +oAuth2User.getAttributes().get("id");
            id = "kakao_" +oAuth2User.getAttributes().get("id");
            user = new User(email, name, "kakao");


        }
        if(oauthClientName.equals("naver")){
            Map<String,String> responseMap = (Map<String,String>) oAuth2User.getAttributes().get("response");
//            type = "naver_" + responseMap.get("id").substring(0,14);
            id = "naver_" + responseMap.get("id").substring(0,14);

            email = responseMap.get("email");
            name = responseMap.get("name");
            user = new User( email, name, "naver");
        }

        userRepository.save(user);
        System.out.println("1111111111");
        System.out.println("id : " + id);
        return new CustomOAuth2User(id); //사용자 정보 제공하여 토큰발행
    }
}
