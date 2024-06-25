package sparta.streaming.user.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey; // secretKey라는 이름으로 가져오겠다

    private final JwtBlacklist jwtBlacklist;

    public JwtProvider(JwtBlacklist jwtBlacklist) {
        this.jwtBlacklist = jwtBlacklist;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String create(Long userId, String role){
        Date expiredDate = Date.from(Instant.now().plus(1,ChronoUnit.HOURS)); // 유효기한
//        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); //시크릿키 가져오는 것
        Key key = getSigningKey(); //시크릿키 가져오는 것

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        //jwt 생성하여 반환
//        String jwt = Jwts.builder()
//                .signWith(claims, SignatureAlgorithm.HS256)
//                .setSubject(String.valueOf(userId)).setIssuedAt(new Date()).setExpiration(expiredDate)
//                .compact();
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }
    

    // jwt(json웹 토큰) 검증
    public Claims validate(String jwt) {
        if (jwtBlacklist.contains(jwt)) {
            return null; // 블랙리스트에 있는 토큰은 무효화
        }

        Key key = getSigningKey();

        try {
//            String userId = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(jwt)
//                    .getBody()
//                    .getSubject();
//            return userId;
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}