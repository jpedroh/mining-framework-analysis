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
    http.requestMatchers().antMatchers("/api/**", "/iiif/**", actuatorBasePath + "/**").and().authorizeRequests().antMatchers(HttpMethod.POST, "/api/authn/login").permitAll().antMatchers(HttpMethod.GET, "/api/authn/status").permitAll().
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    and()
=======
    antMatchers(HttpMethod.GET, actuatorBasePath + "/info")
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    sessionManagement()
=======
    hasAnyAuthority(ADMIN_GRANT)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    sessionCreationPolicy(SessionCreationPolicy.STATELESS)
=======
    and()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    and()
=======
    sessionManagement()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    anonymous()
=======
    sessionCreationPolicy(SessionCreationPolicy.STATELESS)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    authorities(ANONYMOUS_GRANT)
=======
    and()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .anonymous().
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    servletApi()
=======
    authorities(ANONYMOUS_GRANT)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .and().
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    cors()
=======
    servletApi()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .and().
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    csrf()
=======
    cors()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .csrfTokenRepository(this.getCsrfTokenRepository()).
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    sessionAuthenticationStrategy(this.sessionAuthenticationStrategy())
=======
    csrf()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    and()
=======
    csrfTokenRepository(this.csrfTokenRepository())
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    exceptionHandling()
=======
    sessionAuthenticationStrategy(this.sessionAuthenticationStrategy())
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .authenticationEntryPoint(new DSpace401AuthenticationEntryPoint(restAuthenticationService)).
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    accessDeniedHandler(accessDeniedHandler)
=======
    exceptionHandling()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    and()
=======
    authenticationEntryPoint(new DSpace401AuthenticationEntryPoint(restAuthenticationService))
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    logout()
=======
    accessDeniedHandler(accessDeniedHandler)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    addLogoutHandler(customLogoutHandler)
=======
    and()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    logoutRequestMatcher(new AntPathRequestMatcher("/api/authn/logout", HttpMethod.POST.name()))
=======
    logout()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
=======
    addLogoutHandler(customLogoutHandler)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    permitAll()
=======
    logoutRequestMatcher(new AntPathRequestMatcher("/api/authn/logout", HttpMethod.POST.name()))
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    and()
=======
    logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    addFilterBefore(new AnonymousAdditionalAuthorizationFilter(authenticationManager(), authenticationService), StatelessAuthenticationFilter.class)
=======
    permitAll()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    addFilterBefore(new StatelessLoginFilter("/api/authn/login", authenticationManager(), restAuthenticationService), LogoutFilter.class)
=======
    and()
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .addFilterBefore(new 
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    ShibbolethLoginFilter
=======
    AnonymousAdditionalAuthorizationFilter
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    ("/api/authn/shibboleth", authenticationManager(), 
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    restAuthenticationService
=======
    authenticationService
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    ), 
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    LogoutFilter
=======
    StatelessAuthenticationFilter
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
    .class).
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/left.java
    addFilterBefore(new OidcLoginFilter("/api/authn/oidc", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new StatelessAuthenticationFilter(authenticationManager(), restAuthenticationService, ePersonRestAuthenticationProvider, requestService), StatelessLoginFilter.class)
=======
    addFilterBefore(new StatelessLoginFilter("/api/authn/login", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new ShibbolethLoginFilter("/api/authn/shibboleth", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new OrcidLoginFilter("/api/authn/orcid", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new OidcLoginFilter("/api/authn/oidc", authenticationManager(), restAuthenticationService), LogoutFilter.class).addFilterBefore(new StatelessAuthenticationFilter(authenticationManager(), restAuthenticationService, ePersonRestAuthenticationProvider, requestService), StatelessLoginFilter.class)
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/security/WebSecurityConfiguration.java/right.java
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