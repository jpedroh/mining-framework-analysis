  package       com . premiumminds . billy . spain . persistence . dao . jpa ;   import   java . util . List ;  import   javax . inject . Inject ;  import   javax . inject . Provider ;  import   javax . persistence . EntityManager ;  import      com . mysema . query . jpa . impl . JPAQuery ;  import       com . premiumminds . billy . spain . persistence . dao . DAOESCreditReceiptEntry ;  import       com . premiumminds . billy . spain . persistence . entities . ESCreditReceiptEntity ;  import       com . premiumminds . billy . spain . persistence . entities . ESCreditReceiptEntryEntity ;  import        com . premiumminds . billy . spain . persistence . entities . jpa . JPAESCreditReceiptEntity ;  import        com . premiumminds . billy . spain . persistence . entities . jpa . JPAESCreditReceiptEntryEntity ;  import        com . premiumminds . billy . spain . persistence . entities . jpa . QJPAESCreditReceiptEntity ;  import       com . premiumminds . billy . spain . services . entities . ESCreditReceiptEntry ;  import       com . premiumminds . billy . spain . services . entities . ESReceipt ;   public class DAOESCreditReceiptEntryImpl  extends  AbstractDAOESGenericInvoiceEntryImpl  < ESCreditReceiptEntryEntity , JPAESCreditReceiptEntryEntity >  implements  DAOESCreditReceiptEntry  {    @ Inject public DAOESCreditReceiptEntryImpl  (   Provider  < EntityManager > emProvider )  {  super  ( emProvider ) ; }    @ Override public ESCreditReceiptEntryEntity getEntityInstance  ( )  {  return  new JPAESCreditReceiptEntryEntity  ( ) ; }    @ Override protected  Class  < JPAESCreditReceiptEntryEntity > getEntityClass  ( )  {  return  JPAESCreditReceiptEntryEntity . class ; }    @ Override public ESCreditReceiptEntity checkCreditReceipt  (  ESReceipt receipt )  {  QJPAESCreditReceiptEntity  creditReceiptEntity =  QJPAESCreditReceiptEntity . jPAESCreditReceiptEntity ;  JPAQuery  query =  new JPAQuery  (  this . getEntityManager  ( ) ) ;   query . from  ( creditReceiptEntity ) ;   List  < JPAESCreditReceiptEntity >  allCns =  query . list  ( creditReceiptEntity ) ;  for ( JPAESCreditReceiptEntity cne : allCns )  {  for ( ESCreditReceiptEntry cnee :  cne . getEntries  ( ) )  {  if  (     cnee . getReference  ( ) . getNumber  ( ) . compareTo  (  receipt . getNumber  ( ) ) == 0 )  {  return cne ; } } }  return null ; } }