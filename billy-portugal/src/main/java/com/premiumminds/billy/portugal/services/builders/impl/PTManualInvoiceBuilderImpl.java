  package       com . premiumminds . billy . portugal . services . builders . impl ;   import   javax . inject . Inject ;  import      com . premiumminds . billy . core . exceptions . BillyValidationException ;  import         com . premiumminds . billy . core . services . entities . documents . GenericInvoice . CreditOrDebit ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTBusiness ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTCustomer ;  import       com . premiumminds . billy . portugal . persistence . dao . AbstractDAOPTGenericInvoice ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTSupplier ;  import       com . premiumminds . billy . portugal . persistence . entities . PTGenericInvoiceEntity ;  import       com . premiumminds . billy . portugal . persistence . entities . PTInvoiceEntity ;  import       com . premiumminds . billy . portugal . services . builders . PTManualInvoiceBuilder ;  import       com . premiumminds . billy . portugal . services . entities . PTGenericInvoice ;  import       com . premiumminds . billy . portugal . services . entities . PTGenericInvoiceEntry ;   public class PTManualInvoiceBuilderImpl  <  TBuilder  extends  PTManualInvoiceBuilderImpl  < TBuilder , TEntry , TDocument > ,  TEntry  extends PTGenericInvoiceEntry ,  TDocument  extends PTGenericInvoice >  extends  PTManualBuilderImpl  < TBuilder , TEntry , TDocument >  implements   PTManualInvoiceBuilder  < TBuilder , TEntry , TDocument >  {    @ Inject public  <  TDAO  extends  AbstractDAOPTGenericInvoice  <  ? extends TDocument > > PTManualInvoiceBuilderImpl  (  TDAO daoPTGenericInvoice ,  DAOPTBusiness daoPTBusiness ,  DAOPTCustomer daoPTCustomer ,  DAOPTSupplier daoPTSupplier )  {  super  ( daoPTGenericInvoice , daoPTBusiness , daoPTCustomer , daoPTSupplier ) ; }    @ Override protected PTInvoiceEntity getTypeInstance  ( )  {  return  ( PTInvoiceEntity )  super . getTypeInstance  ( ) ; }    @ Override protected void validateInstance  ( )  throws BillyValidationException  {  PTGenericInvoiceEntity  i =  this . getTypeInstance  ( ) ;   i . setCreditOrDebit  (  CreditOrDebit . CREDIT ) ;   super . validateInstance  ( ) ; } }