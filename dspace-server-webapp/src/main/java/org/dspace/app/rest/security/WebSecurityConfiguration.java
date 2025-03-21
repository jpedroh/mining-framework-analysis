package org.dspace.app.rest.security;
import org.dspace.app.rest.exception.DSpaceAccessDeniedHandler;
import org.dspace.authenticate.service.AuthenticationService;
import org.dspace.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security configuration for DSpace Server Webapp
 *
 * @author Frederic Van Reet (frederic dot vanreet at atmire dot com)
 * @author Tom Desair (tom dot desair at atmire dot com)
 */
@EnableWebSecurity @Configuration @EnableConfigurationProperties(value = SecurityProperties.class) @EnableGlobalMethodSecurity(prePostEnabled = true) public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
  public static final String ADMIN_GRANT = "ADMIN";

  public static final String AUTHENTICATED_GRANT = "AUTHENTICATED";

  public static final String ANONYMOUS_GRANT = "ANONYMOUS";

  @Autowired private EPersonRestAuthenticationProvider ePersonRestAuthenticationProvider;

  @Autowired private RestAuthenticationService restAuthenticationService;

  @Autowired private RequestService requestService;

  @Autowired private CustomLogoutHandler customLogoutHandler;

  @Autowired private AuthenticationService authenticationService;

  @Autowired private DSpaceAccessDeniedHandler accessDeniedHandler;

  @Value(value = "${management.endpoints.web.base-path:/actuator}") private String actuatorBasePath;

  @Override public void configure(WebSecurity webSecurity) throws Exception {
    webSecurity.ignoring().antMatchers(HttpMethod.GET, "/api/authn/login").antMatchers(HttpMethod.PUT, "/api/authn/login").antMatchers(HttpMethod.PATCH, "/api/authn/login").antMatchers(HttpMethod.DELETE, "/api/authn/login");
  }

  @Override protected void configure(HttpSecurity http) throws Exception {
    http.requestMatchers().antMatchers("/api/**", "/iiif/**", actuatorBasePath + "/**").and().authorizeRequests().antMatchers(HttpMethod.POST, "/api/authn/login").permitAll().antMatchers(HttpMethod.GET, "/api/authn/status").permitAll().antMatchers(HttpMethod.GET, actuatorBasePath + "/info").hasAnyAuthority(ADMIN_GRANT).and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().anonymous().authorities(ANONYMOUS_GRANT).and().servletApi().and().cors().and().csrf().csrfTokenRepository(this.csrfTokenRepository()).sessionAuthenticationStrategy(this.sessionAuthenticationStrategy()).and().exceptionHandling().authenticationEntryPoint(new DSpace401AuthenticationEntryPoint(restAuthenticationService)).accessDeniedHandler(accessDeniedHandler).and().logout().addLogoutHandler(customLogoutHandler).logoutRequestMatcher(new AntPathRequestMatcher("/api/authn/logout", HttpMethod.POST.name())).logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)).permitAll().and().addFilterBefore(new AnonymousAdditionalAuthorizationFilter(authenticationManager(), authenticationService), StatelessAuthenticationFilter.class).addFilterBefore(new 
<<<<<<< /usr/src/app/output/dspace/dspace/dc5ef4844b9c2a9f4ea6e1ac0dfa3a6b8d09363b/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    OrcidLoginFilter
=======
    StatelessLoginFilter
>>>>>>> /usr/src/app/output/dspace/dspace/dc5ef4844b9c2a9f4ea6e1ac0dfa3a6b8d09363b/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    (
<<<<<<< /usr/src/app/output/dspace/dspace/dc5ef4844b9c2a9f4ea6e1ac0dfa3a6b8d09363b/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    "/api/authn/orcid"
=======
    "/api/authn/login"
>>>>>>> /usr/src/app/output/dspace/dspace/dc5ef4844b9c2a9f4ea6e1ac0dfa3a6b8d09363b/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    , authenticationManager(), restAuthenticationService), LogoutFilter.class).
<<<<<<< /usr/src/app/output/dspace/dspace/dc5ef4844b9c2a9f4ea6e1ac0dfa3a6b8d09363b/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    addFilterBefore(new OidcLoginFilter("/api/authn/oidc", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new StatelessAuthenticationFilter(authenticationManager(), restAuthenticationService, ePersonRestAuthenticationProvider, requestService), StatelessLoginFilter.class)
=======
    addFilterBefore(new ShibbolethLoginFilter("/api/authn/shibboleth", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new OidcLoginFilter("/api/authn/oidc", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new StatelessAuthenticationFilter(authenticationManager(), restAuthenticationService, ePersonRestAuthenticationProvider, requestService), StatelessLoginFilter.class)
>>>>>>> /usr/src/app/output/dspace/dspace/dc5ef4844b9c2a9f4ea6e1ac0dfa3a6b8d09363b/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    ;
  }

  @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(ePersonRestAuthenticationProvider);
  }

  /**
     * Returns a custom DSpaceCsrfTokenRepository based on Spring Security's CookieCsrfTokenRepository, which is
     * designed for Angular Apps.
     * <P>
     * The DSpaceCsrfTokenRepository stores the token in server-side cookie (for later verification), but sends it to
     * the client as a DSPACE-XSRF-TOKEN header. The client is expected to return the token in either a header named
     * X-XSRF-TOKEN *or* a URL parameter named "_csrf", at which point it is validated against the server-side cookie.
     * <P>
     * This behavior is based on the defaults for Angular apps: https://angular.io/guide/http#security-xsrf-protection.
     * However, instead of sending an XSRF-TOKEN Cookie (as is usual for Angular apps), we send the DSPACE-XSRF-TOKEN
     * header...as this ensures the Angular app can receive the token even if it is on a different domain.
     *
     * @return CsrfTokenRepository as described above
     */
  @Lazy @Bean public CsrfTokenRepository csrfTokenRepository() {
    return new DSpaceCsrfTokenRepository();
  }

  /**
     * Returns a custom DSpaceCsrfAuthenticationStrategy, which ensures that (after authenticating) the CSRF token
     * is only refreshed when it is used (or attempted to be used) by the client.
     */
  private SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new DSpaceCsrfAuthenticationStrategy(csrfTokenRepository());
  }
}