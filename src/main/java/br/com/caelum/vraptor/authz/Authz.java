  package     br . com . caelum . vraptor . authz ;   import     br . com . caelum . vraptor . InterceptionException ;  import     br . com . caelum . vraptor . Intercepts ;  import     br . com . caelum . vraptor . Result ;  import       br . com . caelum . vraptor . authz . annotation . AuthzBypass ;  import      br . com . caelum . vraptor . core . InterceptorStack ;  import      br . com . caelum . vraptor . interceptor . Interceptor ;  import      br . com . caelum . vraptor . ioc . RequestScoped ;  import      br . com . caelum . vraptor . resource . ResourceMethod ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;    @ Intercepts  @ RequestScoped public class Authz  implements  Interceptor  {   private final AuthzInfo  authInfo ;   private final Authorizator  authorizator ;   private final Result  result ;   public Authz  (  Authorizator authorizator ,  AuthzInfo authInfo ,  Result result )  {    this . authorizator = authorizator ;    this . authInfo = authInfo ;    this . result = result ; }    @ Override public void intercept  (  InterceptorStack stack ,  ResourceMethod method ,  Object resourceInstance )  throws InterceptionException  {  if  (  
<<<<<<<
 authorizable != null
=======
authInfo
>>>>>>>
 &&  isAllowed  ( method , authorizable ) ) 
<<<<<<<
=======
 {  Authorizable  authorizable =  authInfo . getAuthorizable  ( ) ;  if  (  authorizable != null )  {   Set  < Role >  roles =  authorizable . roles  ( ) ;  for ( Role role : roles )  {  if  (  authorizator . isAllowed  ( role , method ) )  {   stack . next  ( method , resourceInstance ) ;  return ; } } }   authInfo . handleAuthError  ( result ) ; }
>>>>>>>
 else  {   
<<<<<<<
stack
=======
log
>>>>>>>
 . 
<<<<<<<
next
=======
error
>>>>>>>
  ( 
<<<<<<<
method
=======
"no AuthInfo found!"
>>>>>>>
 , resourceInstance ) ; 
<<<<<<<
 return ;
=======
 throw  new IllegalStateException  ( "No AuthInfo found" ) ;
>>>>>>>
 } }   private boolean isAllowed  (  ResourceMethod method ,  Authorizable authorizable )  {  for ( Role role :  authorizable . roles  ( ) )  {  if  (  authorizator . isAllowed  ( role , method ) )  {  return true ; } }  return false ; }    @ Override public boolean accepts  (  ResourceMethod method )  {  if  (   method . getMethod  ( ) . isAnnotationPresent  (  AuthzBypass . class ) )  {  return false ; }  return true ; }   private static final Logger  log =  LoggerFactory . getLogger  (  Authz . class ) ; }