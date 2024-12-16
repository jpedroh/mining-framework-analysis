  package       com . premiumminds . billy . portugal . persistence . dao . jpa ;   import   java . util . List ;  import   javax . inject . Inject ;  import   javax . inject . Provider ;  import   javax . persistence . EntityManager ;  import      com . mysema . query . jpa . impl . JPAQuery ;  import       com . premiumminds . billy . portugal . persistence . dao . DAOPTCreditNoteEntry ;  import       com . premiumminds . billy . portugal . persistence . entities . PTCreditNoteEntity ;  import       com . premiumminds . billy . portugal . persistence . entities . PTCreditNoteEntryEntity ;  import        com . premiumminds . billy . portugal . persistence . entities . jpa . JPAPTCreditNoteEntity ;  import        com . premiumminds . billy . portugal . persistence . entities . jpa . JPAPTCreditNoteEntryEntity ;  import        com . premiumminds . billy . portugal . persistence . entities . jpa . QJPAPTCreditNoteEntity ;  import       com . premiumminds . billy . portugal . services . entities . PTCreditNoteEntry ;  import       com . premiumminds . billy . portugal . services . entities . PTInvoice ;   public class DAOPTCreditNoteEntryImpl  extends  AbstractDAOPTGenericInvoiceEntryImpl  < PTCreditNoteEntryEntity , JPAPTCreditNoteEntryEntity >  implements  DAOPTCreditNoteEntry  {    @ Inject public DAOPTCreditNoteEntryImpl  (   Provider  < EntityManager > emProvider )  {  super  ( emProvider ) ; }    @ Override public PTCreditNoteEntryEntity getEntityInstance  ( )  {  return  new JPAPTCreditNoteEntryEntity  ( ) ; }    @ Override protected  Class  < JPAPTCreditNoteEntryEntity > getEntityClass  ( )  {  return  JPAPTCreditNoteEntryEntity . class ; }    @ Override public PTCreditNoteEntity checkCreditNote  (  PTInvoice invoice )  {  QJPAPTCreditNoteEntity  creditNoteEntity =  QJPAPTCreditNoteEntity . jPAPTCreditNoteEntity ;  JPAQuery  query =  new JPAQuery  (  this . getEntityManager  ( ) ) ;   query . from  ( creditNoteEntity ) ;   List  < JPAPTCreditNoteEntity >  allCns =  query . list  ( creditNoteEntity ) ;  for ( JPAPTCreditNoteEntity cne : allCns )  {  for ( PTCreditNoteEntry cnee :  cne . getEntries  ( ) )  {  if  (     cnee . getReference  ( ) . getNumber  ( ) . compareTo  (  invoice . getNumber  ( ) ) == 0 )  {  return cne ; } } }  return null ; } }