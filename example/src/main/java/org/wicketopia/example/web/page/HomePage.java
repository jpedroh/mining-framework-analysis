  package     org . wicketopia . example . web . page ;   import       org . apache . wicket . spring . injection . annot . SpringBean ;  import     org . springframework . security . authentication . AuthenticationManager ;  import    org . wicketopia . persistence . PersistenceProvider ;   public class HomePage  extends BasePage  {   private static final  long  serialVersionUID = 1L ;    @ SpringBean private PersistenceProvider  persistenceProvider ;    @ SpringBean private AuthenticationManager  authenticationManager ;   public HomePage  ( )  { 
<<<<<<<
  add  (  new Link  ( "loginAdmin" )  {    @ Override public void onClick  ( )  {   final UsernamePasswordAuthenticationToken  tok =  new UsernamePasswordAuthenticationToken  ( "admin" , "admin" ) ;    SecurityContextHolder . getContext  ( ) . setAuthentication  (  authenticationManager . authenticate  ( tok ) ) ;   setResponsePage  (  HomePage . class ) ; }    @ Override public boolean isVisible  ( )  {  return    SecurityContextHolder . getContext  ( ) . getAuthentication  ( ) == null ; } } ) ;
=======
>>>>>>>
 
<<<<<<<
  add  (  new Link  ( "logout" )  {    @ Override public void onClick  ( )  {   SecurityContextHolder . clearContext  ( ) ;   setResponsePage  (  HomePage . class ) ; }    @ Override public boolean isVisible  ( )  {  return    SecurityContextHolder . getContext  ( ) . getAuthentication  ( ) != null ; } } ) ;
=======
>>>>>>>
 } }