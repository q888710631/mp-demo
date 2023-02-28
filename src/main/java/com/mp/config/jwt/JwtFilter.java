package com.mp.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.config.MyHttpRequestWrapper;
import com.mp.config.MyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
import java.util.List;

public class JwtFilter extends GenericFilter {
    private final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    final ObjectMapper objectMapper;

    final List<AuthenticationResolver> resolvers;

    public JwtFilter(ObjectMapper objectMapper, List<AuthenticationResolver> resolvers) {
        this.objectMapper = objectMapper;
        this.resolvers = resolvers;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        log.info("method = {}, url = {} ", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        String jwt = resolveToken(httpServletRequest);
        if (StringUtils.hasText(jwt)) {
            try {
                // getClaims 不报错表示jwt格式正确
                Claims claims = TokenProvider.getClaims(jwt);
                Authentication authentication = null;
                for (AuthenticationResolver resolver : resolvers) {
                    if (resolver.supports(jwt)) {
                        authentication = resolver.getAuthentication(jwt);
                        break;
                    }
                }
                if (null == authentication) {
                    log.error("token认证失败,未找到合适的认证类型");
                    throw new AuthenticationCredentialsNotFoundException("token认证失败,未找到合适的认证类型");
                }
                if (Boolean.FALSE.equals(authentication.isAuthenticated())) {
                    log.error("授权认证失败");
                    throw new BadCredentialsException("token认证失败");
                }
                // 存储token
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

            } catch (JwtException | IllegalArgumentException e) {
                log.info("Invalid JWT token.");
            } catch (AuthenticationException e) {
                log.error("token 认证授权失败:{}", e.getMessage());
                resolveAuthenticationException(e, response);
                return;
            }
        }
        chain.doFilter(request, response);
        // 装饰Request，用来反复获取body
        // chain.doFilter(new MyHttpRequestWrapper(httpServletRequest), response);
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
            log.error("### resolvePermissionFailException 处理权限异常返回失败", e);
        }
    }
}
