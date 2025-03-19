<<<<<<< /usr/src/app/output/andreaskl/springboot-angular-atmosphere-quickstart/cadddbad17de1526cd54f53de4a0c633acb328e9/src/main/java/net/andreaskluth/toastonatmosphere/configuration/SecurityConfiguration.java/left.java
package net.andreaskluth.toastonatmosphere.configuration;

import net.andreaskluth.toastonatmosphere.security.FakeAuthenticationProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration for spring security.
 * 
 * @author Andreas Kluth
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Bean
  public AuthenticationProvider provider() {
    return new FakeAuthenticationProvider();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth, AuthenticationProvider provider) throws Exception {
    auth.authenticationProvider(provider);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
      .antMatchers("/images/**")
      .antMatchers("/css/**")
      .antMatchers("/js/**")
      .antMatchers("/webjars/**")
      .antMatchers("/templates/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
      .anyRequest()
      .authenticated()
      .and()
      .formLogin()
      .loginPage("/login")
      .defaultSuccessUrl("/")
      .permitAll()
      .and()
      .logout()
      .logoutUrl("/logout")
      .logoutSuccessUrl("/logout-success")
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
      .permitAll();
  }

}
||||||| /usr/src/app/output/andreaskl/springboot-angular-atmosphere-quickstart/cadddbad17de1526cd54f53de4a0c633acb328e9/src/main/java/net/andreaskluth/toastonatmosphere/configuration/SecurityConfiguration.java/base.java
package net.andreaskluth.toastonatmosphere.configuration;

import net.andreaskluth.toastonatmosphere.security.FakeAuthenticationProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration for spring security.
 * 
 * @author Andreas Kluth
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new FakeAuthenticationProvider(passwordEncoder());
  }

  @Autowired
  public void configureGlobal(PasswordEncoder encoder, AuthenticationManagerBuilder auth) throws Exception {
    auth
      .authenticationProvider(authenticationProvider());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
      .antMatchers("/images/**")
      .antMatchers("/css/**")
      .antMatchers("/js/**")
      .antMatchers("/webjars/**")
      .antMatchers("/templates/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
      .anyRequest()
      .authenticated()
      .and()
      .formLogin()
      .loginPage("/login")
      .defaultSuccessUrl("/")
      .permitAll()
      .and()
      .logout()
      .logoutUrl("/logout")
      .logoutSuccessUrl("/logout-success")
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
      .permitAll();
  }

}
=======
fatal: path 'src/main/java/net/andreaskluth/toastonatmosphere/configuration/SecurityConfiguration.java' exists on disk, but not in 'ca84722195aac40f62ec2db5eed3eea4273a6ff8'
>>>>>>> /usr/src/app/output/andreaskl/springboot-angular-atmosphere-quickstart/cadddbad17de1526cd54f53de4a0c633acb328e9/src/main/java/net/andreaskluth/toastonatmosphere/configuration/SecurityConfiguration.java/right.java
