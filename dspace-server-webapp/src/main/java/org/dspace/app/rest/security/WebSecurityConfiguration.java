  package     org . dspace . app . rest . security ;   import      org . dspace . app . rest . exception . DSpaceAccessDeniedHandler ;  import     org . dspace . authenticate . service . AuthenticationService ;  import    org . dspace . services . RequestService ;  import      org . springframework . beans . factory . annotation . Autowired ;  import      org . springframework . boot . autoconfigure . security . SecurityProperties ;  import      org . springframework . boot . context . properties . EnableConfigurationProperties ;  import     org . springframework . context . annotation . Bean ;  import     org . springframework . context . annotation . Configuration ;  import     org . springframework . context . annotation . Lazy ;  import    org . springframework . http . HttpMethod ;  import    org . springframework . http . HttpStatus ;  import        org . springframework . security . config . annotation . authentication . builders . AuthenticationManagerBuilder ;  import        org . springframework . security . config . annotation . method . configuration . EnableGlobalMethodSecurity ;  import        org . springframework . security . config . annotation . web . builders . HttpSecurity ;  import        org . springframework . security . config . annotation . web . builders . WebSecurity ;  import        org . springframework . security . config . annotation . web . configuration . EnableWebSecurity ;  import        org . springframework . security . config . annotation . web . configuration . WebSecurityConfigurerAdapter ;  import      org . springframework . security . config . http . SessionCreationPolicy ;  import       org . springframework . security . web . authentication . logout . HttpStatusReturningLogoutSuccessHandler ;  import       org . springframework . security . web . authentication . logout . LogoutFilter ;  import       org . springframework . security . web . authentication . session . SessionAuthenticationStrategy ;  import      org . springframework . security . web . csrf . CsrfTokenRepository ;  import       org . springframework . security . web . util . matcher . AntPathRequestMatcher ;  import      org . springframework . beans . factory . annotation . Value ;    @ EnableWebSecurity  @ Configuration  @ EnableConfigurationProperties  (  SecurityProperties . class )  @ EnableGlobalMethodSecurity  (  prePostEnabled = true ) public class WebSecurityConfiguration  extends WebSecurityConfigurerAdapter  {   public static final String  ADMIN_GRANT = "ADMIN" ;   public static final String  AUTHENTICATED_GRANT = "AUTHENTICATED" ;   public static final String  ANONYMOUS_GRANT = "ANONYMOUS" ;    @ Autowired private EPersonRestAuthenticationProvider  ePersonRestAuthenticationProvider ;    @ Autowired private RestAuthenticationService  restAuthenticationService ;    @ Autowired private RequestService  requestService ;    @ Autowired private CustomLogoutHandler  customLogoutHandler ;    @ Autowired private AuthenticationService  authenticationService ;    @ Autowired private DSpaceAccessDeniedHandler  accessDeniedHandler ;    @ Override public void configure  (  WebSecurity webSecurity )  throws Exception  {       webSecurity . ignoring  ( ) . antMatchers  (  HttpMethod . GET , "/api/authn/login" ) . antMatchers  (  HttpMethod . PUT , "/api/authn/login" ) . antMatchers  (  HttpMethod . PATCH , "/api/authn/login" ) . antMatchers  (  HttpMethod . DELETE , "/api/authn/login" ) ; }    @ Override protected void configure  (  HttpSecurity http )  throws Exception  {                                         
<<<<<<<
http
=======
 http . requestMatchers  ( )
>>>>>>>
 . 
<<<<<<<
requestMatchers
=======
antMatchers
>>>>>>>
  ( "/api/**" , "/iiif/**" ,  actuatorBasePath + "/**" ) . 
<<<<<<<
antMatchers
=======
and
>>>>>>>
  ( "/api/**" , "/iiif/**" ) . 
<<<<<<<
and
=======
authorizeRequests
>>>>>>>
  ( ) . 
<<<<<<<
authorizeRequests
=======
antMatchers
>>>>>>>
  (  HttpMethod . POST , "/api/authn/login" ) . 
<<<<<<<
antMatchers
=======
permitAll
>>>>>>>
  (  HttpMethod . POST , "/api/authn/login" ) . permitAll  ( 
<<<<<<<
=======
 HttpMethod . GET
>>>>>>>
 "/api/authn/status" ) . antMatchers  (  HttpMethod . GET , "/api/authn/status" ) . permitAll  (  actuatorBasePath + "/info" ) . 
<<<<<<<
and
=======
hasAnyAuthority
>>>>>>>
  ( ADMIN_GRANT ) . sessionManagement  ( ) . sessionCreationPolicy  (  SessionCreationPolicy . STATELESS ) . and  ( ) . anonymous  ( ) . authorities  ( ANONYMOUS_GRANT ) . and  ( ) . servletApi  ( ) . and  ( ) . cors  ( ) . and  ( ) . csrf  ( ) . csrfTokenRepository  (  this . csrfTokenRepository  ( ) ) . sessionAuthenticationStrategy  (  this . sessionAuthenticationStrategy  ( ) ) . and  ( ) . exceptionHandling  ( ) . authenticationEntryPoint  (  new DSpace401AuthenticationEntryPoint  ( restAuthenticationService ) ) . accessDeniedHandler  ( accessDeniedHandler ) . and  ( ) . logout  ( ) . addLogoutHandler  ( customLogoutHandler ) . logoutRequestMatcher  (  new AntPathRequestMatcher  ( "/api/authn/logout" ,   HttpMethod . POST . name  ( ) ) ) . logoutSuccessHandler  (  new HttpStatusReturningLogoutSuccessHandler  (  HttpStatus . NO_CONTENT ) ) . permitAll  ( ) . and  ( ) . addFilterBefore  (  new AnonymousAdditionalAuthorizationFilter  (  authenticationManager  ( ) , authenticationService ) ,  StatelessAuthenticationFilter . class ) . addFilterBefore  (  new StatelessLoginFilter  ( "/api/authn/login" ,  authenticationManager  ( ) , restAuthenticationService ) ,  LogoutFilter . class ) . addFilterBefore  (  new ShibbolethLoginFilter  ( "/api/authn/shibboleth" ,  authenticationManager  ( ) , restAuthenticationService ) ,  LogoutFilter . class ) . addFilterBefore  (  new OrcidLoginFilter  ( "/api/authn/orcid" ,  authenticationManager  ( ) , restAuthenticationService ) ,  LogoutFilter . class ) . addFilterBefore  (  new OidcLoginFilter  ( "/api/authn/oidc" ,  authenticationManager  ( ) , restAuthenticationService ) ,  LogoutFilter . class ) . addFilterBefore  (  new StatelessAuthenticationFilter  (  authenticationManager  ( ) , restAuthenticationService , ePersonRestAuthenticationProvider , requestService ) ,  StatelessLoginFilter . class ) ; }    @ Override protected void configure  (  AuthenticationManagerBuilder auth )  throws Exception  {   auth . authenticationProvider  ( ePersonRestAuthenticationProvider ) ; }    @ Lazy  @ Bean public CsrfTokenRepository csrfTokenRepository  ( )  {  return  new DSpaceCsrfTokenRepository  ( ) ; }   private SessionAuthenticationStrategy sessionAuthenticationStrategy  ( )  {  return  new DSpaceCsrfAuthenticationStrategy  (  csrfTokenRepository  ( ) ) ; }    @ Value  ( "${management.endpoints.web.base-path:/actuator}" ) private String  actuatorBasePath ; }