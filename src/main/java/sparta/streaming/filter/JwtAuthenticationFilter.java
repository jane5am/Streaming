package sparta.streaming.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sparta.streaming.domain.CustomUserDetails;
import sparta.streaming.domain.User;
import sparta.streaming.user.UserRepository;
import sparta.streaming.user.provider.JwtProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request로부터 bearertoken 꺼내오는 것
        try {
            String token = parseBearerToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtProvider.validate(token);
            if (userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = userRepository.findByEmail(userId).orElse(null);
            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            String role = user.getRole().toString(); // role은 반드시 이런 형태를 갖추고 있어야함 ROLE_USER, ROLE_ADMIN
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities); // 세번째 파라미터에 권한 있으면 넣기
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        // request로부터 header에 있는 Authorization값을 가져오는거
        String authorization = request.getHeader("Authorization");

        boolean hasAutorization = StringUtils.hasText(authorization);
        if(!hasAutorization) {
            return null;
        }

        // bearer인증 방식인지 확인
        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) {
            return null;
        }

        String token = authorization.substring(7);
        return token;
    }
}
