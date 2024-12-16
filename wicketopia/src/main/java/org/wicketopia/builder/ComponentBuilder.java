  package   org . wicketopia . builder ;   import    org . apache . wicket . Component ;  import     org . apache . wicket . behavior . Behavior ;   public interface ComponentBuilder  {   public void addBehavior  (  Behavior behavior ) ;  void visible  (  boolean viewable ) ;  Component build  ( ) ; 
<<<<<<<
=======
 void addBehavior  (  IBehavior behavior ) ;
>>>>>>>
 }