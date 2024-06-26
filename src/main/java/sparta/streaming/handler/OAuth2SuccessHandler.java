package sparta.streaming.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sparta.streaming.domain.user.CustomOAuth2User;
import sparta.streaming.user.provider.JwtProvider;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException, IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

//        Long id = oAuth2User.getName();
        Long userId = Long.parseLong(oAuth2User.getName());
        String role = oAuth2User.getAuthorities().iterator().next().getAuthority(); // 역할을 가져옵니다.

        String token = jwtProvider.create(userId, role); //이게 바로 토큰
        response.sendRedirect("http://localhost:3000/auth/oauth-response/" + token + "/3600");

    }
}
