package sparta.streaming.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sparta.streaming.filter.JwtAuthenticationFilter;
import sparta.streaming.handler.OAuth2SuccessHandler;

import java.io.IOException;

@Configurable
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(cors -> cors
                .configurationSource(corsConfigurationSource())
        )
        .csrf(CsrfConfigurer::disable)
        .httpBasic(HttpBasicConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션유지하지않겠다
        )
                .authorizeHttpRequests(request -> request
                    .requestMatchers("/","/api/v1/auth/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
                    .requestMatchers("/","/api/v1/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
//                    .requestMatchers("/","/api/v2/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
                    .requestMatchers("/","/api/user/signup/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
                    .requestMatchers("/","/api/user/login/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
                    .requestMatchers("/","/api/v1/video/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
//                    .requestMatchers("/","/api/video/**","/oauth2/**").permitAll() //이 패턴에 대해서는 모두 허용하겠다
                    .requestMatchers("/api/v1/user/**").hasRole("USER")
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                    .anyRequest().authenticated()
                )
                    .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new FailedAuthenticationEntryPoint())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 모든 출처에 대해 모두 허용
        corsConfiguration.addAllowedMethod("*"); // 모든 메소드에 대해 허용
        corsConfiguration.addAllowedHeader("*"); // 모든 헤더에 대해 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", corsConfiguration);

        return source;
    }

}


// 인가 실패 인 경우
class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        {"code":"NP","message" : "No permission:}
        response.getWriter().write("{\"code\":\"NP\",\"message\" : \"No permission:}");
    }
}

