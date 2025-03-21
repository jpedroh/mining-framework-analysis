  package     org . dspace . app . rest . security ;   import      org . dspace . app . rest . exception . DSpaceAccessDeniedHandler ;  import     org . dspace . authenticate . service . AuthenticationService ;  import    org . dspace . services . RequestService ;  import      org . springframework . beans . factory . annotation . Autowired ;  import      org . springframework . boot . autoconfigure . security . SecurityProperties ;  import      org . springframework . boot . context . properties . EnableConfigurationProperties ;  import     org . springframework . context . annotation . Configuration ;  import    org . springframework . http . HttpMethod ;  import    org . springframework . http . HttpStatus ;  import        org . springframework . security . config . annotation . authentication . builders . AuthenticationManagerBuilder ;  import        org . springframework . security . config . annotation . method . configuration . EnableGlobalMethodSecurity ;  import        org . springframework . security . config . annotation . web . builders . HttpSecurity ;  import        org . springframework . security . config . annotation . web . builders . WebSecurity ;  import        org . springframework . security . config . annotation . web . configuration . EnableWebSecurity ;  import        org . springframework . security . config . annotation . web . configuration . WebSecurityConfigurerAdapter ;  import      org . springframework . security . config . http . SessionCreationPolicy ;  import       org . springframework . security . web . authentication . logout . HttpStatusReturningLogoutSuccessHandler ;  import       org . springframework . security . web . authentication . logout . LogoutFilter ;  import       org . springframework . security . web . authentication . session . SessionAuthenticationStrategy ;  import      org . springframework . security . web . csrf . CsrfTokenRepository ;  import       org . springframework . security . web . util . matcher . AntPathRequestMatcher ;  import      org . springframework . beans . factory . annotation . Value ;  import     org . springframework . context . annotation . Bean ;  import     org . springframework . context . annotation . Lazy ;    @ EnableWebSecurity  @ Configuration  @ EnableConfigurationProperties  (  SecurityProperties . class )  @ EnableGlobalMethodSecurity  (  prePostEnabled = true ) public class WebSecurityConfiguration  extends WebSecurityConfigurerAdapter  {   public static final String  ADMIN_GRANT = "ADMIN" ;   public static final String  AUTHENTICATED_GRANT = "AUTHENTICATED" ;   public static final String  ANONYMOUS_GRANT = "ANONYMOUS" ;    @ Autowired private EPersonRestAuthenticationProvider  ePersonRestAuthenticationProvider ;    @ Autowired private RestAuthenticationService  restAuthenticationService ;    @ Autowired private RequestService  requestService ;    @ Autowired private CustomLogoutHandler  customLogoutHandler ;    @ Autowired private AuthenticationService  authenticationService ;    @ Autowired private DSpaceAccessDeniedHandler  accessDeniedHandler ;    @ Override public void configure  (  WebSecurity webSecurity )  throws Exception  {       webSecurity . ignoring  ( ) . antMatchers  (  HttpMethod . GET , "/api/authn/login" ) . antMatchers  (  HttpMethod . PUT , "/api/authn/login" ) . antMatchers  (  HttpMethod . PATCH , "/api/authn/login" ) . antMatchers  (  HttpMethod . DELETE , "/api/authn/login" ) ; }    @ Override protected void configure  (  HttpSecurity http )  throws Exception  {                                        
<<<<<<<
http
=======
   http . requestMatchers  ( ) . antMatchers  ( "/api/**" , "/iiif/**" ,  actuatorBasePath + "/**" ) . and  ( )
>>>>>>>
 . 
<<<<<<<
requestMatchers
=======
authorizeRequests
>>>>>>>
  ( ) . antMatchers  ( 
<<<<<<<
"/api/**"
=======
 HttpMethod . POST
>>>>>>>
 , 
<<<<<<<
"/iiif/**"
=======
"/api/authn/login"
>>>>>>>
 ) . 
<<<<<<<
and
=======
permitAll
>>>>>>>
  ( ) . 
<<<<<<<
authorizeRequests
=======
antMatchers
>>>>>>>
  (  HttpMethod . GET , "/api/authn/status" ) . 
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
  actuatorBasePath + "/info" ) . 
<<<<<<<
antMatchers
=======
hasAnyAuthority
>>>>>>>
  ( 
<<<<<<<
 HttpMethod . GET
=======
ADMIN_GRANT
>>>>>>>
 , "/api/authn/status" ) . 
<<<<<<<
permitAll
=======
and
>>>>>>>
  ( ) . 
<<<<<<<
and
=======
sessionManagement
>>>>>>>
  ( ) . 
<<<<<<<
sessionManagement
=======
sessionCreationPolicy
>>>>>>>
  (  SessionCreationPolicy . STATELESS ) . 
<<<<<<<
sessionCreationPolicy
=======
and
>>>>>>>
  (  SessionCreationPolicy . STATELESS ) . 
<<<<<<<
and
=======
anonymous
>>>>>>>
  ( ) . 
<<<<<<<
anonymous
=======
authorities
>>>>>>>
  ( ANONYMOUS_GRANT ) . 
<<<<<<<
authorities
=======
and
>>>>>>>
  ( ANONYMOUS_GRANT ) . 
<<<<<<<
and
=======
servletApi
>>>>>>>
  ( ) . servletApi  ( ) . 
<<<<<<<
and
=======
cors
>>>>>>>
  ( ) . cors  ( ) . 
<<<<<<<
and
=======
csrf
>>>>>>>
  ( ) . 
<<<<<<<
csrf
=======
csrfTokenRepository
>>>>>>>
  (  this . csrfTokenRepository  ( ) ) . 
<<<<<<<
csrfTokenRepository
=======
sessionAuthenticationStrategy
>>>>>>>
  (  this . 
<<<<<<<
getCsrfTokenRepository
=======
sessionAuthenticationStrategy
>>>>>>>
  ( ) ) . 
<<<<<<<
sessionAuthenticationStrategy
=======
and
>>>>>>>
  ( 
<<<<<<<
 this . sessionAuthenticationStrategy  ( )
=======
>>>>>>>
 ) . 
<<<<<<<
and
=======
exceptionHandling
>>>>>>>
  ( ) . 
<<<<<<<
exceptionHandling
=======
authenticationEntryPoint
>>>>>>>
  (  new DSpace401AuthenticationEntryPoint  ( restAuthenticationService ) ) . 
<<<<<<<
authenticationEntryPoint
=======
accessDeniedHandler
>>>>>>>
  ( 
<<<<<<<
 new DSpace401AuthenticationEntryPoint  ( restAuthenticationService )
=======
accessDeniedHandler
>>>>>>>
 ) . 
<<<<<<<
accessDeniedHandler
=======
and
>>>>>>>
  ( accessDeniedHandler ) . 
<<<<<<<
and
=======
logout
>>>>>>>
  ( ) . 
<<<<<<<
logout
=======
addLogoutHandler
>>>>>>>
  ( customLogoutHandler ) . 
<<<<<<<
addLogoutHandler
=======
logoutRequestMatcher
>>>>>>>
  ( 
<<<<<<<
customLogoutHandler
=======
 new AntPathRequestMatcher  ( "/api/authn/logout" ,   HttpMethod . POST . name  ( ) )
>>>>>>>
 ) . 
<<<<<<<
logoutRequestMatcher
=======
logoutSuccessHandler
>>>>>>>
  ( 
<<<<<<<
 new AntPathRequestMatcher  ( "/api/authn/logout" ,   HttpMethod . POST . name  ( ) )
=======
 new HttpStatusReturningLogoutSuccessHandler  (  HttpStatus . NO_CONTENT )
>>>>>>>
 ) . 
<<<<<<<
logoutSuccessHandler
=======
permitAll
>>>>>>>
  (  new HttpStatusReturningLogoutSuccessHandler  (  HttpStatus . NO_CONTENT ) ) . 
<<<<<<<
permitAll
=======
and
>>>>>>>
  ( ) . 
<<<<<<<
and
=======
addFilterBefore
>>>>>>>
  (  new AnonymousAdditionalAuthorizationFilter  (  authenticationManager  ( ) , authenticationService ) ,  StatelessAuthenticationFilter . class ) . addFilterBefore  ( 
<<<<<<<
 new AnonymousAdditionalAuthorizationFilter  (  authenticationManager  ( ) , authenticationService )
=======
 new StatelessLoginFilter  ( "/api/authn/login" ,  authenticationManager  ( ) , restAuthenticationService )
>>>>>>>
 ,  
<<<<<<<
StatelessAuthenticationFilter
=======
LogoutFilter
>>>>>>>
 . class ) . addFilterBefore  ( 
<<<<<<<
 new StatelessLoginFilter  ( "/api/authn/login" ,  authenticationManager  ( ) , restAuthenticationService )
=======
 new ShibbolethLoginFilter  ( "/api/authn/shibboleth" ,  authenticationManager  ( ) , restAuthenticationService )
>>>>>>>
 ,  LogoutFilter . class ) . addFilterBefore  ( 
<<<<<<<
 new ShibbolethLoginFilter  ( "/api/authn/shibboleth" ,  authenticationManager  ( ) , restAuthenticationService )
=======
 new OrcidLoginFilter  ( "/api/authn/orcid" ,  authenticationManager  ( ) , restAuthenticationService )
>>>>>>>
 ,  LogoutFilter . class ) . addFilterBefore  (  new OidcLoginFilter  ( "/api/authn/oidc" ,  authenticationManager  ( ) , restAuthenticationService ) ,  LogoutFilter . class ) . addFilterBefore  (  new StatelessAuthenticationFilter  (  authenticationManager  ( ) , restAuthenticationService , ePersonRestAuthenticationProvider , requestService ) ,  StatelessLoginFilter . class ) ; }    @ Override protected void configure  (  AuthenticationManagerBuilder auth )  throws Exception  {   auth . authenticationProvider  ( ePersonRestAuthenticationProvider ) ; }   private SessionAuthenticationStrategy sessionAuthenticationStrategy  ( )  {  return  new DSpaceCsrfAuthenticationStrategy  (  csrfTokenRepository  ( ) ) ; }    @ Value  ( "${management.endpoints.web.base-path:/actuator}" ) private String  actuatorBasePath ;    @ Lazy  @ Bean public CsrfTokenRepository csrfTokenRepository  ( )  {  return  new DSpaceCsrfTokenRepository  ( ) ; } }