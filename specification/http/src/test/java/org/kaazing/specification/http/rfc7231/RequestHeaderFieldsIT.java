  package     org . kaazing . specification . http . rfc7231 ;   import static     java . util . concurrent . TimeUnit . SECONDS ;  import static     org . junit . rules . RuleChain . outerRule ;  import   org . junit . Ignore ;  import   org . junit . Rule ;  import   org . junit . Test ;  import    org . junit . rules . DisableOnDebug ;  import    org . junit . rules . TestRule ;  import    org . junit . rules . Timeout ;  import      org . kaazing . k3po . junit . annotation . Specification ;  import      org . kaazing . k3po . junit . rules . K3poRule ;   public class RequestHeaderFieldsIT  {   private final K3poRule  k3po =   new K3poRule  ( ) . setScriptRoot  ( 
<<<<<<<
"org/kaazing/specification/http/rfc7231/request.header"
=======
"org/kaazing/specification/http/rfc7231/request.header.fields"
>>>>>>>
 ) ;   private final TestRule  timeout =  new DisableOnDebug  (  new Timeout  ( 5 , SECONDS ) ) ;    @ Rule public final TestRule  chain =   outerRule  ( k3po ) . around  ( timeout ) ; 
<<<<<<<
   @ Test  @ Specification  (  { "expectation.responds.with.417/request" , "expectation.responds.with.417/response" } ) public void serverShouldRespondToMeetableExpectWith417  ( )  throws Exception  {   k3po . finish  ( ) ; }
=======
>>>>>>>
    @ Test  @ Specification  (  { "intermediary.decrement.max.forward.header/request" , "intermediary.decrement.max.forward.header/response" } ) public void intermediaryMustDecrementMaxForwardHeaderOnOptionsOrTraceRequest  ( )  throws Exception  {   k3po . finish  ( ) ; }    @ Test  @ Specification  (  { "intermediary.responds.zero.max.forward/request" , "intermediary.responds.zero.max.forward/response" } ) public void intermediaryThatReceivesMaxForwardOfZeroOnOptionsOrTraceMustRespondToRequest  ( )  throws Exception  {   k3po . finish  ( ) ; }    @ Test  @ Ignore  ( "not complete" )  @ Specification  (  { "server.responds.to.unmeetable.expect.with.417/request" , "server.responds.to.unmeetable.expect.with.417/response" } ) public void serverRespondsToUnmeetableExpectWith417  ( )  throws Exception  {   k3po . finish  ( ) ; } }