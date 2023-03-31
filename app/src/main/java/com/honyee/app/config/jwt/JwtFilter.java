package com.honyee.app.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.http.MyHttpRequestWrapper;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.jwt.my.MyAuthenticationToken;
import com.honyee.app.config.mybatis.MybatisPlusTenantHandler;
import com.honyee.app.utils.LogUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

public class JwtFilter extends GenericFilter {

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;

    public JwtFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Logger logger = LogUtil.get();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        // 存储authentication
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);
        if (StringUtils.hasText(jwt)) {
            try {
                // getClaims 不报错表示jwt格式正确
                Authentication authentication = null;
                Claims claims = TokenProvider.getClaims(jwt);
                Object loginType = claims.get(JwtConstants.LOGIN_TYPE);
                Object loginKey = claims.get(JwtConstants.LOGIN_KEY);
                if (Objects.equals(loginType, LoginTypeEnum.COMMON.name())) {
                    authentication = new MyAuthenticationToken(Long.valueOf(loginKey.toString()));
                }
                authentication = authenticationManager.authenticate(authentication);

                if (null == authentication) {
                    logger.error("token认证失败,未找到合适的认证类型");
                    throw new AuthenticationCredentialsNotFoundException("token认证失败,未找到合适的认证类型");
                }
                if (Boolean.FALSE.equals(authentication.isAuthenticated())) {
                    logger.error("授权认证失败");
                    throw new BadCredentialsException("token认证失败");
                }
                securityContext.setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                logger.info("Invalid JWT token.");
            } catch (AuthenticationException e) {
                logger.error("token 认证授权失败:{}", e.getMessage());
                resolveAuthenticationException(e, response);
                return;
            }
        }
        try {
            // 装饰Request，用来反复获取body
            chain.doFilter(new MyHttpRequestWrapper(httpServletRequest), response);
        } finally {
            MybatisPlusTenantHandler.removeTenantValue();
        }
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


    private void resolveAuthenticationException(AuthenticationException ex, ServletResponse servletResponse) {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            response.getWriter().print(objectMapper.writeValueAsString(new MyResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null)));
        } catch (IOException e) {
            LogUtil.get().error("### resolvePermissionFailException 处理权限异常返回失败", e);
        }
    }
}
