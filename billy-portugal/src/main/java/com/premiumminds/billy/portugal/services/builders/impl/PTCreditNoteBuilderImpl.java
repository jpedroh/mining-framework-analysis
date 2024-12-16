  package       com . premiumminds . billy . portugal . services . builders . impl ;   import      com . premiumminds . billy . core . exceptions . BillyValidationException ;  import         com . premiumminds . billy . core . services . entities . documents . GenericInvoice . CreditOrDebit ;  import      com . premiumminds . billy . core . util . Localizer ;  import      com . premiumminds . billy . core . util . NotOnUpdate ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTBusiness ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTCustomer ;  import       com . premiumminds . billy . portugal . persistence . dao . AbstractDAOPTGenericInvoice ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTSupplier ;  import       com . premiumminds . billy . portugal . persistence . entities . PTCreditNoteEntity ;  import       com . premiumminds . billy . portugal . services . builders . PTCreditNoteBuilder ;  import       com . premiumminds . billy . portugal . services . entities . PTCreditNote ;  import       com . premiumminds . billy . portugal . services . entities . PTCreditNoteEntry ;  import        com . premiumminds . billy . portugal . services . entities . PTGenericInvoice . SourceBilling ;   public class PTCreditNoteBuilderImpl  <  TBuilder  extends  PTCreditNoteBuilderImpl  < TBuilder , TEntry , TDocument > ,  TEntry  extends PTCreditNoteEntry ,  TDocument  extends PTCreditNote >  extends  PTGenericInvoiceBuilderImpl  < TBuilder , TEntry , TDocument >  implements   PTCreditNoteBuilder  < TBuilder , TEntry , TDocument >  {   protected static final Localizer  LOCALIZER =  new Localizer  ( "com/premiumminds/billy/core/i18n/FieldNames" ) ;   public  <  TDAO  extends  AbstractDAOPTGenericInvoice  <  ? extends TDocument > > PTCreditNoteBuilderImpl  (  TDAO daoPTCreditNote ,  DAOPTBusiness daoPTBusiness ,  DAOPTCustomer daoPTCustomer ,  DAOPTSupplier daoPTSupplier )  {  super  ( daoPTCreditNote , daoPTBusiness , daoPTCustomer , daoPTSupplier ) ;   this . setSourceBilling  (  SourceBilling . P ) ; }    @ Override protected PTCreditNoteEntity getTypeInstance  ( )  {  return  ( PTCreditNoteEntity )  super . getTypeInstance  ( ) ; }    @ Override protected void validateInstance  ( )  throws BillyValidationException  {  PTCreditNoteEntity  i =  this . getTypeInstance  ( ) ;   i . setSourceBilling  (  SourceBilling . P ) ;   i . setCreditOrDebit  (  CreditOrDebit . DEBIT ) ;   super . validateInstance  ( ) ; }    @ Override  @ NotOnUpdate public TBuilder setSourceBilling  (  SourceBilling sourceBilling )  {  switch  ( sourceBilling )  {   case P :  return  super . setSourceBilling  ( sourceBilling ) ;   case M :   default :  throw  new BillyValidationException  ( ) ; } } }