  package       com . premiumminds . billy . portugal . persistence . dao . jpa ;   import   javax . inject . Inject ;  import   javax . inject . Provider ;  import   javax . persistence . EntityManager ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTInvoiceEntry ;  import       com . premiumminds . billy . portugal . persistence . entities . PTInvoiceEntryEntity ;  import        com . premiumminds . billy . portugal . persistence . entities . jpa . JPAPTInvoiceEntryEntity ;   public class DAOPTInvoiceEntryImpl  extends  AbstractDAOPTGenericInvoiceEntryImpl  < PTInvoiceEntryEntity , JPAPTInvoiceEntryEntity >  implements  DAOPTInvoiceEntry  {    @ Inject public DAOPTInvoiceEntryImpl  (   Provider  < EntityManager > emProvider )  {  super  ( emProvider ) ; }    @ Override public PTInvoiceEntryEntity getEntityInstance  ( )  {  return  new JPAPTInvoiceEntryEntity  ( ) ; }    @ Override protected  Class  < JPAPTInvoiceEntryEntity > getEntityClass  ( )  {  return  JPAPTInvoiceEntryEntity . class ; } }