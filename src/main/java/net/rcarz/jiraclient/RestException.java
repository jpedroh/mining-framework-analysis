  package   net . rcarz . jiraclient ; 
<<<<<<<
=======
  import    org . apache . http . Header ;
>>>>>>>
   public class RestException  extends Exception  {   private  int  status ;   private String  result ;   public  int getHttpStatusCode  ( )  {  return status ; }   public String getHttpResult  ( )  {  return result ; }   public String getMessage  ( )  {  return  String . format  ( "%s %s: %s" ,  Integer . toString  ( status ) ,  super . getMessage  ( ) , result ) ; }   private  Header  [ ]  headers ;   public RestException  (  String msg ,   int status ,  String result ,   Header  [ ] headers )  {  super  ( msg ) ;    this . status = status ;    this . result = result ;    this . headers = headers ; }   public  Header  [ ] getHeaders  ( )  {  return headers ; } }