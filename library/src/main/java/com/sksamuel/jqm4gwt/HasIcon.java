  package   com . sksamuel . jqm4gwt ;   public interface HasIcon  <  T >  extends   HasIconPos  < T >  {  T removeIcon  ( ) ;  void setBuiltInIcon  (  DataIcon icon ) ;  void setIconURL  (  String src ) ;  T withBuiltInIcon  (  DataIcon icon ) ;  T withIconURL  (  String src ) ; }