package com.honyee.app.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    private static final String BASE_SECRET = "Y2M3MTZmNTllMTMyMGQzNmQ5MjNhZTNjYWY2MmFmMDFiMWFkYjMwMWE0MjhiMDI3MWZhY2JiZGFkZDlhNDcwZTI3M2M0Mjg5MDdjY2E2YWMzMDEwMzkwYTQ0MzczN2NjODVhZmJmNDUzODdmNDBmODFiZDU4M2I1YjQwNjBlMTA=";

    private static Key KEY;

    private static JwtParser JWT_PARSER = null;

    public TokenProvider(@Value("${application.token-secret:}") String baseSecret) {
        try {
            if (StringUtils.isBlank(baseSecret)) {
                baseSecret = BASE_SECRET;
                log.info("未配置application.token-secret，使用默认值");
            }
            byte[] keyBytes = Decoders.BASE64.decode(baseSecret);
            KEY = Keys.hmacShaKeyFor(keyBytes);
            JWT_PARSER = Jwts.parserBuilder().setSigningKey(KEY).build();
        } catch (Exception e) {
            log.warn("TokenProvider初始化失败，{}", e.getMessage());
        }
    }

    public static String createToken(
        Authentication authentication,
        boolean rememberMe,
        String loginType,
        Object loginKey,
        JwtHandler jwtHandler
    ) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + JwtConstants.TOKEN_VALID_MS_FOR_REMEMBER_ME);
//        if (rememberMe) {
//            validity = new Date(now + JwtConstants.TOKEN_VALID_MS_FOR_REMEMBER_ME);
//        } else {
//            validity = new Date(now + JwtConstants.TOKEN_VALID_MS);
//        }

        JwtBuilder builder = Jwts.builder();
        Object details = authentication.getDetails();
        if (details != null) {
            // ...
        }
        JwtBuilder claim = builder
            .setSubject(authentication.getName())
            .claim(JwtConstants.LOGIN_TYPE, loginType)
            .claim(JwtConstants.LOGIN_KEY, loginKey)
            .signWith(KEY, SignatureAlgorithm.HS512)
            .setExpiration(validity);

        jwtHandler.handleJwt(claim);
        return claim.compact();

    }

    public interface JwtHandler {
        JwtBuilder handleJwt(JwtBuilder jwtBuilder);
    }

    public static Claims getClaims(String authToken) {
        return JWT_PARSER.parseClaimsJws(authToken).getBody();
    }

}
