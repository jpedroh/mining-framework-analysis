package ${package}.config;

<<<<<<< /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/left.java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
||||||| /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/base.java
import org.springframework.context.annotation.*;
=======
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.*;
>>>>>>> /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/right.java
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
<<<<<<< /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/left.java
import pl.codeleak.arch.account.UserService;
||||||| /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/base.java

import ${package}.account.UserService;
=======
import org.springframework.security.web.util.matcher.RequestMatcher;

import ${package}.account.UserService;
>>>>>>> /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/right.java

@Configuration
@EnableWebMvcSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

<<<<<<< /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/left.java
    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices("remember-me-key", userService());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .eraseCredentials(true)
                .userDetailsService(userService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/favicon.ico", "/resources/**", "/signup").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/signin")
                .permitAll()
                .failureUrl("/signin?error=1")
                .loginProcessingUrl("/authenticate")
                .and()
            .logout()
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessUrl("/signin?logout")
                .and()
            .rememberMe()
                .rememberMeServices(rememberMeServices())
                .key("remember-me-key");
    }
||||||| /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/base.java
	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("remember-me-key", userService());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}
=======
	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("remember-me-key", userService());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}

  @Profile("test")
  @Bean(name = "csrfMatcher")
  public RequestMatcher testCsrfMatcher() {
    return new RequestMatcher() {

      @Override
      public boolean matches(HttpServletRequest request) {
        return false;
      }
    };
  }

  @Profile("!test")
  @Bean(name = "csrfMatcher")
  public RequestMatcher csrfMatcher() {
    /**
     * Copy of default request matcher from
     * CsrfFilter$DefaultRequiresCsrfMatcher
     */
    return new RequestMatcher() {
      private Pattern allowedMethods = Pattern
        .compile("^(GET|HEAD|TRACE|OPTIONS)$");

      /*
       * (non-Javadoc)
       *
       * @see
       * org.springframework.security.web.util.matcher.RequestMatcher#
       * matches(javax.servlet.http.HttpServletRequest)
       */
      public boolean matches(HttpServletRequest request) {
        return !allowedMethods.matcher(request.getMethod()).matches();
      }
    };
  }
>>>>>>> /usr/src/app/output/kolorobot/spring-mvc-quickstart-archetype/78c6fba5414281cddde8139a83318aeb0a6dc9ec/src/main/resources/archetype-resources/src/main/java/config/SecurityConfig.java/right.java
}
