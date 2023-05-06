package com.honyee.app.config.jwt;

import com.honyee.app.utils.LogUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class TokenProvider {

    private static final TokenProvider INSTANT = new TokenProvider();

    private Key key;

    private JwtParser jwtParser = null;

    private TokenProvider() {
        try {
            ClassPathResource application = new ClassPathResource("application.yml");
            InputStream inputStream = new FileInputStream(application.getFile());
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(inputStream);
            String baseSecret = map.getOrDefault("token-secret", "undefined token-secret").toString();
            byte[] keyBytes = Decoders.BASE64.decode(baseSecret);
            key = Keys.hmacShaKeyFor(keyBytes);
            jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        } catch (Exception e) {
            LogUtil.get().error("{0}", e);
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
        Date validity;
        if (rememberMe) {
            validity = new Date(now + JwtConstants.TOKEN_VALID_MS);
        } else {
            validity = new Date(now + JwtConstants.TOKEN_VALID_MS_FOR_REMEMBER_ME);
        }

        JwtBuilder builder = Jwts.builder();
        Object details = authentication.getDetails();
        if (details != null) {
            // ...
        }
        JwtBuilder claim = builder
            .setSubject(authentication.getName())
            .claim(JwtConstants.LOGIN_TYPE, loginType)
            .claim(JwtConstants.LOGIN_KEY, loginKey)
            .signWith(INSTANT.key, SignatureAlgorithm.HS512)
            .setExpiration(validity);

        jwtHandler.handleJwt(claim);
        return claim.compact();

    }

    public interface JwtHandler {
        JwtBuilder handleJwt(JwtBuilder jwtBuilder);
    }

    public static Claims getClaims(String authToken) {
        return INSTANT.jwtParser.parseClaimsJws(authToken).getBody();
    }

}
