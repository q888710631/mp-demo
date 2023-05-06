package com.honyee.app.config.seurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.jwt.JwtFilter;
import com.honyee.app.config.jwt.my.MyAuthenticationProvider;
import com.honyee.app.service.MyUserDetailService;
import com.honyee.app.utils.YamlUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
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
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.yaml.snakeyaml.Yaml;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements InitializingBean {

    public static AuthenticateProperties authenticateProperties;

    private JwtFilter jwtFilter;

    private final MyUserDetailService myUserDetailService;

    private final MyAuthenticationProvider myAuthenticationProvider;

    private final MySecurityProblemSupport problemSupport;

    private final ObjectMapper objectMapper;

    private AuthenticationManager authenticationManager;

    @Value("${spring.profiles.active}")
    private String env;

    public SecurityConfiguration(ObjectMapper objectMapper,
                                 MyUserDetailService myUserDetailService,
                                 MyAuthenticationProvider myAuthenticationProvider,
                                 MySecurityProblemSupport problemSupport) throws Exception {
        this.myUserDetailService = myUserDetailService;
        this.myAuthenticationProvider = myAuthenticationProvider;
        this.problemSupport = problemSupport;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        authenticationManager = super.authenticationManagerBean();
        this.jwtFilter = new JwtFilter(objectMapper, super.authenticationManagerBean());

        // 读取权限配置文件
//        ClassPathResource application = new ClassPathResource("authentication.yml");
//        Map<String, Object> data = yaml.load(new FileInputStream(application.getFile()));
//        String json = objectMapper.writeValueAsString(data);
//        authenticateProperties = objectMapper.readValue(json, AuthenticateProperties.class);
        // 获得文件流时，因为读取的文件是在打好jar文件里面，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
        // ResourcePatternResolver的实现方法，可以匹配到各种部署时的各种文件类型例如war，jar，zip等等findPathMatchingResources
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("authentication.yml");
        Resource resource = resources[0];
        authenticateProperties = YamlUtil.loadAs(resource.getInputStream(), AuthenticateProperties.class);
        System.out.println();

    }

    // 配置认证管理器
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
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
                        new MyFilterInvocationSecurityMetadataSource(env, securityMetadataSource, authenticateProperties));
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

    private SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> securityConfigurerAdapter() {
        return new SecurityConfigurerAdapter<>() {
            @Override
            public void configure(HttpSecurity http) {
                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            }
        };
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }
}
