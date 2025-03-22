package me.zhengjie.modules.security.config;

import me.zhengjie.modules.security.annotation.AnonymousAccess;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.config.ElPermissionConfig;
import me.zhengjie.modules.security.security.JwtAuthenticationEntryPoint;
import me.zhengjie.modules.security.security.JwtAuthorizationTokenFilter;
import me.zhengjie.modules.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    private final JwtUserDetailsService jwtUserDetailsService;
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
    @Autowired
    private ApplicationContext applicationContext;
||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
=======
    private final ApplicationContext applicationContext;
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java

    private final JwtAuthorizationTokenFilter authenticationTokenFilter;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoderBean());
    }

//    @Bean

//    public AnonymousAuthenticationFilter anonymousAuthenticationFilter() {

//        AnonymousAuthenticationFilter authenticationFilter = new AnonymousAuthenticationFilter("anonymous");

//        return authenticationFilter;

//    }

//

//    @Bean

//    public AnonymousAuthenticationProvider anonymousAuthenticationProvider() {

//        return new AnonymousAuthenticationProvider("anonymous");

//    }

    // 自定义基于JWT的安全过滤器

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, JwtUserDetailsService jwtUserDetailsService, JwtAuthorizationTokenFilter authenticationTokenFilter, ApplicationContext applicationContext) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.authenticationTokenFilter = authenticationTokenFilter;
        this.applicationContext = applicationContext;
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove the ROLE_ prefix
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
    
        // 搜寻 匿名标记 url： PreAuthorize("hasAnyRole('ROLE_ANONYMOUS')") 和 AnonymousAccess
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        Set<String> anonymousUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (null != preAuthorize && preAuthorize.value().contains("ROLE_ANONYMOUS")) {
                anonymousUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
            } else if (null != anonymousAccess && null == preAuthorize) {
                anonymousUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
            }
        }
||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
=======
        // 搜寻 匿名标记 url： PreAuthorize("hasAnyRole('anonymous')") 和 PreAuthorize("@el.check('anonymous')") 和 AnonymousAccess
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        Set<String> anonymousUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if (null != preAuthorize && preAuthorize.value().contains("anonymous")) {
                anonymousUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
            } else if (null != anonymousAccess && null == preAuthorize) {
                anonymousUrls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
            }
        }
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java
        httpSecurity
                // 禁用 CSRF
                .csrf().disable()
                // 授权异常
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
                // 禁用表单登陆
                .formLogin().disable()
||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
=======
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java
                // 不创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 过滤请求
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).anonymous()
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
    
                .antMatchers(HttpMethod.POST, "/auth/" + loginPath).anonymous()
                .antMatchers("/auth/vCode").anonymous()
                // 支付宝回调
                .antMatchers("/api/aliPay/return").anonymous()
                .antMatchers("/api/aliPay/notify").anonymous()

||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
    
                .antMatchers( HttpMethod.POST,"/auth/"+loginPath).anonymous()
                .antMatchers("/auth/vCode").anonymous()
                // 支付宝回调
                .antMatchers("/api/aliPay/return").anonymous()
                .antMatchers("/api/aliPay/notify").anonymous()

=======
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java
                // swagger start
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/*/api-docs").permitAll()
                // swagger end
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
    
                // 接口限流测试
                .antMatchers("/test/**").anonymous()
=======
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java
                // 文件
                .antMatchers("/avatar/**").permitAll()
                .antMatchers("/file/**").permitAll()
                // 放行OPTIONS请求
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()

                .antMatchers("/druid/**").anonymous()
                // 自定义匿名访问所有url放行 ： 允许 匿名和带权限以及登录用户访问
                .antMatchers(anonymousUrls.toArray(new String[0])).permitAll()
||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()

                .antMatchers("/druid/**").anonymous()
=======
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/druid/**").permitAll()
                // 自定义匿名访问所有url放行 ： 允许 匿名和带权限以及登录用户访问
                .antMatchers(anonymousUrls.toArray(new String[0])).permitAll()
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java
                // 所有请求都需要认证
                .anyRequest().authenticated()

                // 防止iframe 造成跨域
                .and().headers().frameOptions().disable();
<<<<<<< /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/left.java
    
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
||||||| /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/base.java
    
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
=======
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
>>>>>>> /usr/src/app/output/elunez/eladmin/5d3ae17a177f9307231bbae1aca11b486e6d1b44/eladmin-system/src/main/java/me/zhengjie/modules/security/config/SecurityConfig.java/right.java
    }
}
