  package      net . sourceforge . pmd . lang . apex . ast ;   import     com . google . summit . ast . Node ;   public final class ASTDmlUpsertStatement  extends  
<<<<<<<
 AbstractApexNode . Single
=======
AbstractDmlStatement
>>>>>>>
  < Node >  {  ASTDmlUpsertStatement  (  Node dmlUpsertStatement )  {  super  ( dmlUpsertStatement ) ; }    @ Override protected  <  P ,  R > R acceptApexVisitor  (   ApexVisitor  <  ? super P ,  ? extends R > visitor ,  P data )  {  return  visitor . visit  ( this , data ) ; } }