  package      net . sourceforge . pmd . lang . apex . ast ;   import     com . google . summit . ast . Node ;   public final class ASTDmlUpdateStatement  extends  
<<<<<<<
 AbstractApexNode . Single
=======
AbstractDmlStatement
>>>>>>>
  < Node >  {  ASTDmlUpdateStatement  (  Node dmlUpdateStatement )  {  super  ( dmlUpdateStatement ) ; }    @ Override protected  <  P ,  R > R acceptApexVisitor  (   ApexVisitor  <  ? super P ,  ? extends R > visitor ,  P data )  {  return  visitor . visit  ( this , data ) ; } }