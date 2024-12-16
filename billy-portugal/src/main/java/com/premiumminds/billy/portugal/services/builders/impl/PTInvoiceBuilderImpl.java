  package       com . premiumminds . billy . portugal . services . builders . impl ;   import      com . premiumminds . billy . core . exceptions . BillyValidationException ;  import         com . premiumminds . billy . core . services . entities . documents . GenericInvoice . CreditOrDebit ;  import      com . premiumminds . billy . core . util . Localizer ;  import      com . premiumminds . billy . core . util . NotOnUpdate ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTBusiness ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTCustomer ;  import       com . premiumminds . billy . portugal . persistence . dao . AbstractDAOPTGenericInvoice ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTSupplier ;  import       com . premiumminds . billy . portugal . persistence . entities . PTInvoiceEntity ;  import       com . premiumminds . billy . portugal . services . builders . PTInvoiceBuilder ;  import        com . premiumminds . billy . portugal . services . entities . PTGenericInvoice . SourceBilling ;  import       com . premiumminds . billy . portugal . services . entities . PTInvoice ;  import       com . premiumminds . billy . portugal . services . entities . PTInvoiceEntry ;   public class PTInvoiceBuilderImpl  <  TBuilder  extends  PTInvoiceBuilderImpl  < TBuilder , TEntry , TDocument > ,  TEntry  extends PTInvoiceEntry ,  TDocument  extends PTInvoice >  extends  PTGenericInvoiceBuilderImpl  < TBuilder , TEntry , TDocument >  implements   PTInvoiceBuilder  < TBuilder , TEntry , TDocument >  {   protected static final Localizer  LOCALIZER =  new Localizer  ( "com/premiumminds/billy/core/i18n/FieldNames" ) ;   public  <  TDAO  extends  AbstractDAOPTGenericInvoice  <  ? extends TDocument > > PTInvoiceBuilderImpl  (  TDAO daoPTInvoice ,  DAOPTBusiness daoPTBusiness ,  DAOPTCustomer daoPTCustomer ,  DAOPTSupplier daoPTSupplier )  {  super  ( daoPTInvoice , daoPTBusiness , daoPTCustomer , daoPTSupplier ) ;   this . setSourceBilling  (  SourceBilling . P ) ; }    @ Override protected PTInvoiceEntity getTypeInstance  ( )  {  return  ( PTInvoiceEntity )  super . getTypeInstance  ( ) ; }    @ Override protected void validateInstance  ( )  throws BillyValidationException  {  PTInvoiceEntity  i =  this . getTypeInstance  ( ) ;   i . setSourceBilling  (  SourceBilling . P ) ;   i . setCreditOrDebit  (  CreditOrDebit . CREDIT ) ;   super . validateInstance  ( ) ; }    @ Override  @ NotOnUpdate public TBuilder setSourceBilling  (  SourceBilling sourceBilling )  {  switch  ( sourceBilling )  {   case P :  return  super . setSourceBilling  ( sourceBilling ) ;   case M :   default :  throw  new BillyValidationException  ( ) ; } } }