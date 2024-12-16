  package     org . apache . metamodel . query . builder ;   import   java . util . Collection ;  import   java . util . Date ;  import     org . apache . metamodel . schema . Column ;   public interface FilterBuilder  <  B >  {   public B isNull  ( ) ;   public B isNotNull  ( ) ;   public B in  (   Collection  <  ? > values ) ;   public B in  (  Number ...  numbers ) ;   public B in  (  String ...  strings ) ;   public B like  (  String string ) ;   public B eq  (  Column column ) ;   public B eq  (  Date date ) ;   public B eq  (  Number number ) ;   public B eq  (  String string ) ;   public B eq  (  Boolean bool ) ;   public B eq  (  Object obj ) ;   public B isEquals  (  Column column ) ;   public B isEquals  (  Date date ) ;   public B isEquals  (  Number number ) ;   public B isEquals  (  String string ) ;   public B isEquals  (  Boolean bool ) ;   public B isEquals  (  Object obj ) ;   public B differentFrom  (  Column column ) ;   public B differentFrom  (  Date date ) ;   public B differentFrom  (  Number number ) ;   public B differentFrom  (  String string ) ;   public B differentFrom  (  Boolean bool ) ;   public B differentFrom  (  Object obj ) ;   public B ne  (  Column column ) ;   public B ne  (  Date date ) ;   public B ne  (  Number number ) ;   public B ne  (  String string ) ;   public B ne  (  Boolean bool ) ;   public B ne  (  Object obj ) ;   public B greaterThan  (  Column column ) ;   public B gt  (  Column column ) ;   public B greaterThan  (  Object obj ) ;   public B gt  (  Object obj ) ;   public B greaterThan  (  Date date ) ;   public B gt  (  Date date ) ;   public B greaterThan  (  Number number ) ;   public B gt  (  Number number ) ;   public B greaterThan  (  String string ) ;   public B gt  (  String string ) ;   public B lessThan  (  Column column ) ;   public B lt  (  Column column ) ;   public B lessThan  (  Date date ) ;   public B lessThan  (  Number number ) ;   public B lessThan  (  String string ) ;   public B lessThan  (  Object obj ) ;   public B lt  (  Object obj ) ;   public B lt  (  Date date ) ;   public B lt  (  Number number ) ;   public B lt  (  String string ) ;   public B greaterThanOrEquals  (  Column column ) ;   public B gte  (  Column column ) ;   public B greaterThanOrEquals  (  Date date ) ;   public B gte  (  Date date ) ;   public B greaterThanOrEquals  (  Number number ) ;   public B gte  (  Number number ) ;   public B greaterThanOrEquals  (  String string ) ;   public B gte  (  String string ) ;   public B greaterThanOrEquals  (  Object obj ) ;   public B gte  (  Object obj ) ;   public B lessThanOrEquals  (  Column column ) ;   public B lte  (  Column column ) ;   public B lessThanOrEquals  (  Date date ) ;   public B lte  (  Date date ) ;   public B lessThanOrEquals  (  Number number ) ;   public B lte  (  Number number ) ;   public B lessThanOrEquals  (  String string ) ;   public B lte  (  String string ) ;   public B lessThanOrEquals  (  Object obj ) ;   public B lte  (  Object obj ) ;   public B notIn  (   Collection  <  ? > values ) ;   public B notIn  (  Number ...  numbers ) ;   public B notIn  (  String ...  strings ) ;   public B notLike  (  String string ) ; }