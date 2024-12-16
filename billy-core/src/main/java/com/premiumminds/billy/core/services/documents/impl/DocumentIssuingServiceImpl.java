  package       com . premiumminds . billy . core . services . documents . impl ;   import    java . lang . reflect . Type ;  import   java . util . HashMap ;  import   java . util . Map ;  import   javax . inject . Inject ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import      com . premiumminds . billy . core . exceptions . InvalidTicketException ;  import       com . premiumminds . billy . core . persistence . dao . DAOGenericInvoice ;  import       com . premiumminds . billy . core . persistence . dao . TransactionWrapper ;  import      com . premiumminds . billy . core . services . Builder ;  import      com . premiumminds . billy . core . services . TicketManager ;  import      com . premiumminds . billy . core . services . UID ;  import       com . premiumminds . billy . core . services . documents . DocumentIssuingHandler ;  import       com . premiumminds . billy . core . services . documents . DocumentIssuingService ;  import       com . premiumminds . billy . core . services . documents . IssuingParams ;  import        com . premiumminds . billy . core . services . entities . documents . GenericInvoice ;  import       com . premiumminds . billy . core . services . exceptions . DocumentIssuingException ;   public class DocumentIssuingServiceImpl  implements  DocumentIssuingService  {   private static final Logger  log =  LoggerFactory . getLogger  (  DocumentIssuingServiceImpl . class ) ;   protected  Map  <  Class  <  ? extends GenericInvoice > ,  DocumentIssuingHandler  <  ? extends GenericInvoice ,  ? extends IssuingParams > >  handlers ;   protected DAOGenericInvoice  daoInvoice ;   protected TicketManager  ticketManager ;    @ Inject public DocumentIssuingServiceImpl  (  DAOGenericInvoice daoInvoice ,  TicketManager ticketManager )  {    this . handlers =  new  HashMap  < 
<<<<<<<
 Class  <  ? extends GenericInvoice >
=======
>>>>>>>
  DocumentIssuingHandler  <  ? extends GenericInvoice ,  ? extends IssuingParams > >  ( ) ;    this . daoInvoice = daoInvoice ;    this . ticketManager = ticketManager ; }    @ Override public  <  T  extends GenericInvoice ,  P  extends IssuingParams > void addHandler  (   Class  < T > handledClass ,   DocumentIssuingHandler  < T , P > handler )  {    this . handlers . put  ( handledClass , handler ) ; }    @ Override public synchronized  <  T  extends GenericInvoice > T issue  (   final  Builder  < T > documentBuilder ,   final IssuingParams parameters )  throws DocumentIssuingException  {  try  {  return   new  TransactionWrapper  < T >  (  this . daoInvoice )  {    @ Override public T runTransaction  ( )  throws Exception  {  return   DocumentIssuingServiceImpl . this . issueDocument  ( documentBuilder , parameters ) ; } } . execute  ( ) ; }  catch (   Exception e )  {    DocumentIssuingServiceImpl . log . error  (  e . getMessage  ( ) , e ) ;  throw  new DocumentIssuingException  ( e ) ; } }    @ Override public synchronized  <  T  extends GenericInvoice > T issue  (   final  Builder  < T > documentBuilder ,   final IssuingParams parameters ,   final String ticketUID )  throws DocumentIssuingException  {  try  {  return   new  TransactionWrapper  < T >  (  this . daoInvoice )  {    @ Override public T runTransaction  ( )  throws Exception  {  if  (  !    DocumentIssuingServiceImpl . this . ticketManager . ticketIssued  ( ticketUID ) )  {  throw  new InvalidTicketException  ( ) ; }  T  result =   DocumentIssuingServiceImpl . this . issueDocument  ( documentBuilder , parameters ) ;     DocumentIssuingServiceImpl . this . ticketManager . updateTicket  (  new UID  ( ticketUID ) ,  result . getUID  ( ) ,  result . getDate  ( ) ,  result . getCreateTimestamp  ( ) ) ;  return result ; } } . execute  ( ) ; }  catch (   InvalidTicketException e )  {  throw e ; }  catch (   RuntimeException e )  {  throw  new DocumentIssuingException  ( e ) ; }  catch (   Exception e )  {    DocumentIssuingServiceImpl . log . error  (  e . getMessage  ( ) , e ) ;  throw  new DocumentIssuingException  ( e ) ; } }   private  <  T  extends GenericInvoice > T issueDocument  (   Builder  < T > documentBuilder ,   final IssuingParams parameters )  throws DocumentIssuingException  {   final T  document =  documentBuilder . build  ( ) ;   final  Type  [ ]  types =   document . getClass  ( ) . getGenericInterfaces  ( ) ;  for ( Type type : types )  {  if  (   this . handlers . containsKey  ( type ) )  {    @ SuppressWarnings  ( "unchecked" )  DocumentIssuingHandler  < T , IssuingParams >  handler =  (  DocumentIssuingHandler  < T , IssuingParams > )  handlers . get  ( type ) ;  return  
<<<<<<<
handler
=======
  this . handlers . get  ( type )
>>>>>>>
 . issue  ( document , parameters ) ; } }  throw  new RuntimeException  (  "Cannot handle document : " +   document . getClass  ( ) . getCanonicalName  ( ) ) ; } }