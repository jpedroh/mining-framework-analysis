  package      net . sourceforge . pmd . lang . apex . ast ;   import     com . google . summit . ast . Node ;   public final class ASTDmlInsertStatement  extends  
<<<<<<<
 AbstractApexNode . Single
=======
AbstractDmlStatement
>>>>>>>
  < Node >  {  ASTDmlInsertStatement  (  Node dmlInsertStatement )  {  super  ( dmlInsertStatement ) ; }    @ Override protected  <  P ,  R > R acceptApexVisitor  (   ApexVisitor  <  ? super P ,  ? extends R > visitor ,  P data )  {  return  visitor . visit  ( this , data ) ; } }