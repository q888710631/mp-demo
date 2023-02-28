package com.mp.config.seurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.config.jwt.AuthenticationResolver;
import com.mp.config.jwt.JwtFilter;
import com.mp.config.jwt.applet.AppletsAuthenticationProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    @Resource
    AppletsAuthenticationProvider appletsAuthenticationProvider;

    @Resource
    SecurityProblemSupport problemSupport;

    @Resource
    ObjectMapper objectMapper;

    List<AuthenticationResolver> resolvers;

    public SecurityConfig(ObjectProvider<AuthenticationResolver> resolvers) {
         this.resolvers = resolvers.stream().collect(Collectors.toList());
    }

    // 配置认证管理器
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(appletsAuthenticationProvider);
    }

    // 配置安全策略
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 设置路径及要求的权限，支持 ant 风格路径写法
        http.csrf().disable()
            .exceptionHandling()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .authenticationEntryPoint(problemSupport)
//            .accessDeniedHandler(problemSupport)
            .and()
            .authorizeRequests()
            // 设置 OPTIONS 尝试请求直接通过
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/api/login").permitAll()
            .antMatchers("/api/test").authenticated()
            .and()
            .apply(securityConfigurerAdapter())
        ;

    }
    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer();
    }

    public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) {
            JwtFilter customFilter = new JwtFilter(objectMapper, resolvers);
            http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
