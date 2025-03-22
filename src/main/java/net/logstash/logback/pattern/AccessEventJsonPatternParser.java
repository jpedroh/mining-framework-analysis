  package    net . logstash . logback . pattern ;   import     ch . qos . logback . access . PatternLayout ;  import      ch . qos . logback . access . spi . IAccessEvent ;  import     ch . qos . logback . core . Context ;  import      ch . qos . logback . core . pattern . PatternLayoutBase ;  import     com . fasterxml . jackson . core . JsonFactory ;   public class AccessEventJsonPatternParser  extends  AbstractJsonPatternParser  < IAccessEvent >  {   public AccessEventJsonPatternParser  (   final Context context ,   final JsonFactory jsonFactory )  {  super  ( context , jsonFactory ) ;   addOperation  ( "nullNA" ,  new NullNaValueOperation  ( ) ) ; }   protected class NullNaValueOperation 
<<<<<<<
 implements   Operation  < IAccessEvent , String >
=======
 extends    AbstractJsonPatternParser  < IAccessEvent > . Operation  < String >
>>>>>>>
  {    @ Override public  ValueGetter  < String , IAccessEvent > createValueGetter  (  String data )  {  return   makeLayoutValueGetter  ( data ) . andThen  (  this :: convert ) ; }   private String convert  (   final String value )  {  return   "-" . equals  ( value ) ? null : value ; } 
<<<<<<<
=======
  public NullNaValueOperation  ( )  {  super  ( true ) ; }
>>>>>>>
 }    @ Override protected  PatternLayoutBase  < IAccessEvent > createLayout  ( )  {  return  new PatternLayout  ( ) ; } 
<<<<<<<
=======
  public AccessEventJsonPatternParser  (   final ContextAware contextAware ,   final JsonFactory jsonFactory )  {  super  ( contextAware , jsonFactory ) ;   addOperation  ( "nullNA" ,  new NullNaValueOperation  ( ) ) ; }
>>>>>>>
 }