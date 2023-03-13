package com.mp.config.seurity;

import com.mp.config.ResponseAdviceHandler;
import com.mp.config.jwt.JwtFilter;
import com.mp.config.jwt.my.MyAuthenticationProvider;
import com.mp.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private MyAuthenticationProvider myAuthenticationProvider;

    @Autowired
    private MySecurityProblemSupport problemSupport;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 配置认证管理器
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService).passwordEncoder(passwordEncoder());
        auth.authenticationProvider(myAuthenticationProvider);
    }

    // 配置安全策略
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 设置路径及要求的权限，支持 ant 风格路径写法
        http.csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint(problemSupport)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 设置 OPTIONS 尝试请求直接通过
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                    FilterInvocationSecurityMetadataSource securityMetadataSource = object.getSecurityMetadataSource();
                    object.setSecurityMetadataSource(
                        new MyFilterInvocationSecurityMetadataSource(securityMetadataSource));
                    object.setAccessDecisionManager(new MyFilterAccessDecisionManager());
                    return object;
                }
            })
//            .antMatchers("/api/authenticate").permitAll()
//            .antMatchers("/api/test/**").permitAll()
            .and()
            .authorizeRequests()
            .anyRequest()
            .authenticated()
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
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
