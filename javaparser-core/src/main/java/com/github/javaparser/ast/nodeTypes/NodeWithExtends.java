  package     com . github . javaparser . ast . nodeTypes ;   import     com . github . javaparser . ast . Node ;  import     com . github . javaparser . ast . NodeList ;  import      com . github . javaparser . ast . type . ClassOrInterfaceType ;   public interface NodeWithExtends  <  N  extends Node >  {   NodeList  < ClassOrInterfaceType > getExtendedTypes  ( ) ;   default ClassOrInterfaceType getExtendedTypes  (   int i )  {  return   getExtendedTypes  ( ) . get  ( i ) ; }  N setExtendedTypes  (   NodeList  < ClassOrInterfaceType > extendsList ) ;   default N addExtends  (   Class  <  ? > clazz )  {    (  ( Node ) this ) . tryAddImportToParentCompilationUnit  ( clazz ) ;  return  addExtends  (  clazz . getSimpleName  ( ) ) ; }    @ SuppressWarnings  ( "unchecked" ) default N addExtends  (  String name )  {  ClassOrInterfaceType  classOrInterfaceType =  new ClassOrInterfaceType  ( name ) ;    getExtendedTypes  ( ) . add  ( classOrInterfaceType ) ;   classOrInterfaceType . setParentNode  (  ( Node ) this ) ;  return  ( N ) this ; } }