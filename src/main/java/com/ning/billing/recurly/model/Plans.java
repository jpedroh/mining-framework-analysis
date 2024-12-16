  package     com . ning . billing . recurly . model ;   import     javax . xml . bind . annotation . XmlAccessorType ;  import     javax . xml . bind . annotation . XmlAccessType ;  import     javax . xml . bind . annotation . XmlRootElement ;  import     javax . xml . bind . annotation . XmlTransient ;    @ XmlRootElement  (  name = "plans" )  @ XmlAccessorType  (  XmlAccessType . FIELD ) public class Plans  extends  RecurlyObjects  < Plan >  {    @ XmlTransient public static final String  PLANS_RESOURCE = "/plans" ; 
<<<<<<<
   @ XmlElement  (  name = "plan" ) private  List  < Plan >  plans ;
=======
>>>>>>>
 }