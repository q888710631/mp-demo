package com.honyee.app.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.jwt.JwtConstants;
import com.honyee.app.config.jwt.LoginTypeEnum;
import com.honyee.app.config.jwt.TokenProvider;
import com.honyee.app.config.jwt.my.MyAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class JwtFilter extends GenericFilter {

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;

    public JwtFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        // 存储authentication
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);
        if (!"/api/authenticate".equals(httpServletRequest.getRequestURI()) && StringUtils.hasText(jwt)) {
            try {
                // getClaims 不报错表示jwt格式正确
                Authentication authentication = null;
                Claims claims = TokenProvider.getClaims(jwt);
                Object loginType = claims.get(JwtConstants.LOGIN_TYPE);
                Object loginKey = claims.get(JwtConstants.LOGIN_KEY);
                if (Objects.equals(loginType, LoginTypeEnum.COMMON.name())) {
                    authentication = new MyAuthenticationToken(Long.valueOf(loginKey.toString()));
                    authentication = authenticationManager.authenticate(authentication);
                    if (null == authentication) {
                        log.info("token invalid [2]: authenticate target not find");
                    } else if (Boolean.FALSE.equals(authentication.isAuthenticated())) {
                        log.info("token invalid [2]: authenticate is failure");
                    } else {
                        securityContext.setAuthentication(authentication);
                    }
                } else {
                    log.info("token invalid [3]:unsupported login type");
                }
            } catch (JwtException | IllegalArgumentException | AuthenticationException e) {
                log.info("token invalid [4]: {}", e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        String startWith = JwtConstants.BEARER + " ";
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(startWith)) {
            return bearerToken.substring(startWith.length());
        }
        String jwt = request.getParameter(JwtConstants.AUTHORIZATION_PARAM);
        if (StringUtils.hasText(jwt)) {
            return jwt;
        }
        return null;
    }

    private void resolveAuthenticationFailure(String message, ServletResponse servletResponse) {
        log.info(message);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            response.getWriter().print(objectMapper.writeValueAsString(new MyResponse<>(HttpStatus.UNAUTHORIZED.value(), message, null)));
        } catch (IOException e) {
            log.error("### resolvePermissionFailException 处理权限异常返回失败", e);
        }
    }
}
