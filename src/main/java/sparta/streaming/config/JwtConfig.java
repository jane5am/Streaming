//package sparta.streaming.config;
//
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.crypto.SecretKey;
//import java.util.Base64;
//
//@Configuration
//public class JwtConfig {
//
//    // 시크릿 키를 담는 변수
//    private SecretKey cachedSecretKey;
//
//    @Value("${JWT_SECRET_KEY}")
//    private String secretKeyPlain;
//
//    @Bean
//    public SecretKey jwtSecretKey() {
//        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
//        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
//    }
//
//    // 시크릿 키를 반환하는 method
//    public SecretKey getSecretKey() {
//        if (cachedSecretKey == null) cachedSecretKey = getSecretKey();
//
//        return cachedSecretKey;
//    }
//}
