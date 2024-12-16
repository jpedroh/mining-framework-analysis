  package    com . taobao . top . mix ;   import   java . util . Map ;  import      com . taobao . top . link . endpoint . EndpointContext ;  import      com . taobao . top . link . endpoint . Identity ;  import      com . taobao . top . link . endpoint . MessageHandler ;   public class ServerMessageHandler  implements  MessageHandler  {    @ Override public void onMessage  (   Map  < String , String > message ,  Identity messageFrom )  {    System . out . println  (  "onMessage:" + message ) ; }    @ Override public void onMessage  (  EndpointContext context )  throws Exception  {   context . reply  (  context . getMessage  ( ) ) ; } 
<<<<<<<
=======
   @ Override public void onMessage  (   Map  < String , String > message )  { }
>>>>>>>
 }