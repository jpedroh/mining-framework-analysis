  package      com . premiumminds . billy . portugal . persistence . dao ;   import   java . util . Date ;  import   java . util . List ;  import      com . premiumminds . billy . core . services . UID ;  import       com . premiumminds . billy . portugal . persistence . entities . PTReceiptInvoiceEntity ;   public interface DAOPTReceiptInvoice  extends   AbstractDAOPTGenericInvoice  < PTReceiptInvoiceEntity >  {   public  List  < PTReceiptInvoiceEntity > getBusinessReceiptInvoicesForSAFTPT  (  UID uid ,  Date from ,  Date to ) ; }