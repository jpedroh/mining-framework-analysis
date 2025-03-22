  package      net . sourceforge . pmd . lang . apex . ast ;   import     com . google . summit . ast . Node ;   public final class ASTDmlDeleteStatement  extends  
<<<<<<<
 AbstractApexNode . Single
=======
AbstractDmlStatement
>>>>>>>
  < Node >  {  ASTDmlDeleteStatement  (  Node dmlDeleteStatement )  {  super  ( dmlDeleteStatement ) ; }    @ Override protected  <  P ,  R > R acceptApexVisitor  (   ApexVisitor  <  ? super P ,  ? extends R > visitor ,  P data )  {  return  visitor . visit  ( this , data ) ; } }