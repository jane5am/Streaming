package sparta.streaming.user.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;


@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey; //secretKey라는 이름으로 가져오겠다

    public String create(String userId){
        Date expiredDate = Date.from(Instant.now().plus(1,ChronoUnit.HOURS)); // 유효기한
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); //시크릿키 가져오는 것

        //jwt 생성하여 반환
        String jwt = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(userId).setIssuedAt(new Date()).setExpiration(expiredDate)
                .compact();
        return jwt;
    }

    //jwt(json웹 토큰) 검증
    public String validate (String jwt){

        String subject = null;
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody().getSubject();


        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
        return "";
    }
}
