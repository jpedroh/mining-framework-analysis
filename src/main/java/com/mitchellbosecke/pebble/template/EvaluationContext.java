  package    com . mitchellbosecke . pebble . template ;   import   java . util . Locale ;  import      com . github . benmanes . caffeine . cache . Cache ;   public interface EvaluationContext  {  boolean isStrictVariables  ( ) ;  Locale getLocale  ( ) ; }