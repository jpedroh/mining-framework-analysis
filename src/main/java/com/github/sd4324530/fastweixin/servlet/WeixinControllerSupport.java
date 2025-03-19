  package     com . github . sd4324530 . fastweixin . servlet ;   import    org . springframework . stereotype . Controller ;  import      org . springframework . web . bind . annotation . RequestMapping ;  import      org . springframework . web . bind . annotation . RequestMethod ;  import      org . springframework . web . bind . annotation . ResponseBody ;  import   javax . servlet . ServletException ;  import    javax . servlet . http . HttpServletRequest ;  import    javax . servlet . http . HttpServletResponse ;  import   java . io . IOException ;  import   java . io . PrintWriter ;    @ Controller public abstract class WeixinControllerSupport  extends WeixinSupport  {    @ RequestMapping  (  method =  RequestMethod . GET )  @ ResponseBody protected final String bind  (  HttpServletRequest request )  {  if  (  isLegal  ( request ) )  {  return  request . getParameter  ( "echostr" ) ; } else  {  return "" ; } }    @ RequestMapping  (  method =  RequestMethod . POST )  @ ResponseBody protected final 
<<<<<<<
String
=======
void
>>>>>>>
 process  (  HttpServletRequest request ,  HttpServletResponse response )  throws ServletException , IOException  { 
<<<<<<<
 String  result =  processRequest  ( request ) ;
=======
 if  (  isLegal  ( request ) )  {  String  result =  processRequest  ( request ) ;   response . setContentType  ( "text/xml;charset=UTF-8" ) ;  PrintWriter  writer =  response . getWriter  ( ) ;   writer . write  ( result ) ;   writer . close  ( ) ; }
>>>>>>>
   response . setContentType  ( "text/xml;charset=UTF-8" ) ;    response . getWriter  ( ) . write  ( result ) ;  return null ; } }