  package   me . zhengjie . config ;   import    me . zhengjie . utils . SecurityUtils ;  import     org . springframework . security . core . GrantedAuthority ;  import    org . springframework . stereotype . Service ;  import   java . util . Arrays ;  import   java . util . List ;  import    java . util . stream . Collectors ;    @ Service  (  value = "el" ) public class ElPermissionConfig  {   public Boolean check  (  String ...  permissions )  {  String  anonymous = "anonymous" ;  if  (   Arrays . asList  ( permissions ) . contains  ( anonymous ) )  {  return true ; }   List  < String >  elPermissions =      SecurityUtils . getUserDetails  ( ) . getAuthorities  ( ) . stream  ( ) . map  (  GrantedAuthority :: getAuthority ) . collect  (  Collectors . toList  ( ) ) ;  if  (  elPermissions . contains  ( "admin" ) )  {  return true ; }  return    
<<<<<<<
  Arrays . stream  ( permissions ) . filter  (  elPermissions :: contains )
=======
Arrays
>>>>>>>
 . 
<<<<<<<
collect
=======
stream
>>>>>>>
  ( 
<<<<<<<
 Collectors . toList  ( )
=======
permissions
>>>>>>>
 ) . 
<<<<<<<
size
=======
anyMatch
>>>>>>>
  (  elPermissions :: contains ) > 0 ; } }