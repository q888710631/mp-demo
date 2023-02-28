package com.mp.config.jwt;

import com.esotericsoftware.yamlbeans.YamlReader;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.FileReader;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final TokenProvider INSTANT = new TokenProvider();

    private Key key;

    private JwtParser jwtParser = null;

    private TokenProvider() {
        try {
            ClassPathResource application = new ClassPathResource("application.yml");
            YamlReader reader = new YamlReader(new FileReader(application.getFile()));
            Map map = (Map) reader.read();
            String baseSecret = map.getOrDefault("token-secret", "undefined token-secret").toString();
            byte[] keyBytes = Decoders.BASE64.decode(baseSecret);
            key = Keys.hmacShaKeyFor(keyBytes);
            jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        } catch (Exception e) {
            log.error("{0}", e);
        }
    }

    public static String createToken(
        Authentication authentication,
        boolean rememberMe,
        String loginType,
        Object loginKey,
        JwtHandler jwtHandler
    ) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
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
            .claim(JwtConstants.AUTHORITIES_KEY, authorities)
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
        try {
            return INSTANT.jwtParser.parseClaimsJws(authToken).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            throw e;
        }
    }

}
