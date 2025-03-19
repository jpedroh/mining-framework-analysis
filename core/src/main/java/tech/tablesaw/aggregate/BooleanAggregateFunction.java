  package   tech . tablesaw . aggregate ;   import    tech . tablesaw . api . ColumnType ;  import    tech . tablesaw . api . BooleanColumn ;   public abstract class BooleanAggregateFunction  <  C  extends  Column  <  ? > >  extends  AggregateFunction  < Boolean , 
<<<<<<<
C
=======
BooleanColumn
>>>>>>>
 >  {   public BooleanAggregateFunction  (  String name )  {  super  ( name ) ; }    @ Override public boolean isCompatableColumn  (  ColumnType type )  {  return  type ==  ColumnType . BOOLEAN ; }    @ Override public ColumnType returnType  ( )  {  return  ColumnType . BOOLEAN ; }   abstract public Boolean summarize  (  BooleanColumn column ) ; }