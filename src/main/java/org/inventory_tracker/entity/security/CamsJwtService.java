package org.inventory_tracker.entity.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;


@Service
public class CamsJwtService {

    // @Value("${cams.jwt.secret}")
    // private String camsJwtSecret;

    private static final String secret = "dMLk4YTxeRi4K45gTV1mz7LSAWoRKSA/cu7zPWkEtyc=";
    // private static final String SECRET = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJlNmI3ODdkYy04NDZiLTRlNmMtOTI1ZC02YzNkYWE0ZTFiZmQiLCJyb2xlIjoiTUVSQ0hBTlQiLCJyb2xlSWQiOiI4YzhkNmM3ZS00NGU5LTRlMjEtYTNjNy05ZjkyYjk2NjYxZmUiLCJ1c2VyVHlwZSI6Ik1FUkNIQU5UIiwicGFyZW50SWQiOiIzOTU0ZGIzYi1hOTExLTQzZGItOTQzZC03MTk5YWUyNGM4OTEiLCJpc3MiOiJjYmkiLCJzdWIiOiJ1c2VyLmF1dGgiLCJleHAiOjE3ODY0NjY2MDR9.YbBALVDLMaWDN_XszvAoxjH3AiBI29-Ewdyckv21Lh0";

    // private SecretKey getVerificationKey() {
    //     return Keys.hmacShaKeyFor(camsJwtSecret.getBytes(StandardCharsets.UTF_8));
    // }
    private SecretKey getVerificationKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // private SecretKey getVerificationKey() {
    //     byte[] keyBytes = Decoders.BASE64.decode(secret);
    //     return Keys.hmacShaKeyFor(keyBytes);
    // }

    public MerchantPrincipal parse(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getVerificationKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return MerchantPrincipal.builder()
                .merchantId(claims.get("userId", String.class))
                .role(claims.get("role", String.class))
                // .institutionId(claims.get("institutionId", String.class))
                // .email(claims.get("email", String.class))
                // .merchantName(claims.get("fullName", String.class))
                .build();
    }

}
