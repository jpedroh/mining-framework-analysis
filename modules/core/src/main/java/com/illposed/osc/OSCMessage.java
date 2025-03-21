  package   com . illposed . osc ;   import   java . util . ArrayList ;  import   java . util . Collection ;  import   java . util . Collections ;  import   java . util . LinkedList ;  import   java . util . List ;  import     com . illposed . osc . utility . OSCJavaToByteArrayConverter ;   public class OSCMessage  extends OSCPacket  {   private String  address ;   private  List  < Object >  arguments ;   public OSCMessage  ( )  {  this  ( null ) ; }   public OSCMessage  (  String address )  {  this  ( address , null ) ; }   public OSCMessage  (  String address ,   Collection  < Object > arguments )  {    this . address = address ;  if  (  arguments == null )  {    this . arguments =  new  LinkedList  < Object >  ( ) ; } else  {    this . arguments = 
<<<<<<<
 new ArrayList  ( arguments )
=======
 new  ArrayList  < Object >  ( arguments )
>>>>>>>
 ; }   init  ( ) ; }   public String getAddress  ( )  {  return address ; }   public void setAddress  (  String address )  {    this . address = address ; }   public void addArgument  (  Object argument )  {   arguments . add  ( argument ) ; }   public  List  < Object > getArguments  ( )  {  return  Collections . unmodifiableList  ( arguments ) ; }   protected void computeAddressByteArray  (  OSCJavaToByteArrayConverter stream )  {   stream . write  ( address ) ; }   protected void computeArgumentsByteArray  (  OSCJavaToByteArrayConverter stream )  {   stream . write  ( ',' ) ;  if  (  null == arguments )  {  return ; }   stream . writeTypes  ( arguments ) ;  for ( Object argument : arguments )  {   stream . write  ( argument ) ; } }   protected   byte  [ ] computeByteArray  (  OSCJavaToByteArrayConverter stream )  {   computeAddressByteArray  ( stream ) ;   computeArgumentsByteArray  ( stream ) ;  return  stream . toByteArray  ( ) ; } 
<<<<<<<
=======
  public OSCMessage  (  String address ,   Object  [ ] arguments )  {    this . address = address ;  if  (  arguments == null )  {    this . arguments =  new LinkedList  ( ) ; } else  {    this . arguments =  new ArrayList  (  arguments . length ) ;    this . arguments . addAll  (  Arrays . asList  ( arguments ) ) ; }   init  ( ) ; }
>>>>>>>
 }