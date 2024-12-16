  package      com . premiumminds . billy . portugal . services . documents ;   import   java . util . Date ;  import   javax . inject . Inject ;  import   javax . persistence . LockModeType ;  import       com . premiumminds . billy . core . persistence . dao . DAOInvoiceSeries ;  import       com . premiumminds . billy . core . persistence . entities . InvoiceSeriesEntity ;  import        com . premiumminds . billy . core . persistence . entities . jpa . JPAInvoiceSeriesEntity ;  import       com . premiumminds . billy . core . services . documents . DocumentIssuingHandler ;  import       com . premiumminds . billy . core . services . exceptions . DocumentIssuingException ;  import       com . premiumminds . billy . portugal . persistence . dao . AbstractDAOPTGenericInvoice ;  import       com . premiumminds . billy . portugal . persistence . entities . PTGenericInvoiceEntity ;  import        com . premiumminds . billy . portugal . services . documents . exceptions . InvalidInvoiceDateException ;  import        com . premiumminds . billy . portugal . services . documents . exceptions . InvalidInvoiceTypeException ;  import        com . premiumminds . billy . portugal . services . documents . exceptions . InvalidSourceBillingException ;  import        com . premiumminds . billy . portugal . services . documents . util . PTIssuingParams ;  import       com . premiumminds . billy . portugal . services . entities . PTGenericInvoice ;  import        com . premiumminds . billy . portugal . services . entities . PTGenericInvoice . SourceBilling ;  import        com . premiumminds . billy . portugal . services . entities . PTGenericInvoice . TYPE ;  import      com . premiumminds . billy . portugal . util . GenerateHash ;   public abstract class PTGenericInvoiceIssuingHandler  <  T  extends PTGenericInvoiceEntity ,  P  extends PTIssuingParams >  implements   DocumentIssuingHandler  < T , P >  {   protected DAOInvoiceSeries  daoInvoiceSeries ;    @ Inject public PTGenericInvoiceIssuingHandler  (  DAOInvoiceSeries daoInvoiceSeries )  {    this . daoInvoiceSeries = daoInvoiceSeries ; }   protected void validateDocumentType  (  TYPE documentType ,  TYPE expectedType ,  String series )  throws InvalidInvoiceTypeException  {  if  (  documentType != expectedType )  {  throw  new InvalidInvoiceTypeException  ( series ,  documentType . toString  ( ) ,  expectedType . toString  ( ) ) ; } }   protected  <  D  extends  AbstractDAOPTGenericInvoice  < T > > T issue  (   final T document ,   final PTIssuingParams parametersPT ,   final D daoInvoice ,   final TYPE invoiceType )  throws DocumentIssuingException  {  String  series =  parametersPT . getInvoiceSeries  ( ) ;  InvoiceSeriesEntity  invoiceSeriesEntity =  this . getInvoiceSeries  ( document , series ,  LockModeType . PESSIMISTIC_WRITE ) ;  SourceBilling  sourceBilling =   (  ( PTGenericInvoice ) document ) . getSourceBilling  ( ) ;   document . initializeEntityDates  ( ) ;  Date  invoiceDate =    document . getDate  ( ) == null ?  new Date  ( ) :  document . getDate  ( ) ;  Date  systemDate =  document . getCreateTimestamp  ( ) ;  Integer  seriesNumber = 1 ;  String  previousHash = null ;  T  latestInvoice =  daoInvoice . getLatestInvoiceFromSeries  (  invoiceSeriesEntity . getSeries  ( ) ,    document . getBusiness  ( ) . getUID  ( ) . toString  ( ) ) ;  if  (  null != latestInvoice )  {   seriesNumber =   latestInvoice . getSeriesNumber  ( ) + 1 ;   previousHash =  latestInvoice . getHash  ( ) ;  Date  latestInvoiceDate =  latestInvoice . getDate  ( ) ;   validateDocumentType  ( invoiceType ,  latestInvoice . getType  ( ) ,  invoiceSeriesEntity . getSeries  ( ) ) ;  if  (  !   latestInvoice . getSourceBilling  ( ) . equals  ( sourceBilling ) )  {  throw  new InvalidSourceBillingException  (  invoiceSeriesEntity . getSeries  ( ) ,  sourceBilling . toString  ( ) ,   latestInvoice . getSourceBilling  ( ) . toString  ( ) ) ; }  if  (   latestInvoiceDate . compareTo  ( invoiceDate ) > 0 )  {  throw  new InvalidInvoiceDateException  ( ) ; } }  String  formatedNumber =      invoiceType . toString  ( ) + " " +  parametersPT . getInvoiceSeries  ( ) + "/" + seriesNumber ;  String  newHash =  GenerateHash . generateHash  (  parametersPT . getPrivateKey  ( ) ,  parametersPT . getPublicKey  ( ) , invoiceDate , systemDate , formatedNumber ,  document . getAmountWithTax  ( ) , previousHash ) ;  String  sourceHash =  GenerateHash . generateSourceHash  ( invoiceDate , systemDate , formatedNumber ,  document . getAmountWithTax  ( ) , previousHash ) ;   document . setDate  ( invoiceDate ) ;   document . setNumber  ( formatedNumber ) ;   document . setSeries  (  invoiceSeriesEntity . getSeries  ( ) ) ;   document . setSeriesNumber  ( seriesNumber ) ;   document . setHash  ( newHash ) ;   document . setBilled  ( false ) ;   document . setCancelled  ( false ) ;   document . setType  ( invoiceType ) ;   document . setSourceHash  ( sourceHash ) ;   document . setHashControl  (  parametersPT . getPrivateKeyVersion  ( ) ) ;   document . setEACCode  (  parametersPT . getEACCode  ( ) ) ;   document . setCurrency  (  document . getCurrency  ( ) ) ;   daoInvoice . create  ( document ) ;  return document ; }   private InvoiceSeriesEntity getInvoiceSeries  (   final T document ,  String series ,  LockModeType lockMode )  {  InvoiceSeriesEntity  invoiceSeriesEntity =   this . daoInvoiceSeries . getSeries  ( series ,    document . getBusiness  ( ) . getUID  ( ) . toString  ( ) , lockMode ) ;  if  (  null == invoiceSeriesEntity )  {  InvoiceSeriesEntity  entity =  new JPAInvoiceSeriesEntity  ( ) ;   entity . setBusiness  (  document . getBusiness  ( ) ) ;   entity . setSeries  ( series ) ;   invoiceSeriesEntity =   this . daoInvoiceSeries . create  ( entity ) ; }  return invoiceSeriesEntity ; } }