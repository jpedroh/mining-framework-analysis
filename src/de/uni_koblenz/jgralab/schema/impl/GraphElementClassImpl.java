  package     de . uni_koblenz . jgralab . schema . impl ;   import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . GraphElementClass ;  import     de . uni_koblenz . jgralab . schema . Package ;  import    de . uni_koblenz . jgralab . GraphElement ;   public abstract class GraphElementClassImpl  <  SC  extends  GraphElementClass  < SC , IC > ,  IC  extends  GraphElement  < SC , IC > >  extends  AttributedElementClassImpl  < SC , IC >  implements   GraphElementClass  < SC , IC >  {   protected GraphClass  graphClass ;   protected GraphElementClassImpl  (  String simpleName ,  Package pkg ,  GraphClass graphClass )  {  super  ( simpleName , pkg ,  graphClass . getSchema  ( ) ) ;    this . graphClass = graphClass ; }    @ Override public GraphClass getGraphClass  ( )  {  return graphClass ; }   public String getDescriptionString  ( )  {  StringBuilder  output =  new StringBuilder  (      this . getClass  ( ) . getSimpleName  ( ) + " '" +  getQualifiedName  ( ) + "'" ) ;  if  (  isAbstract  ( ) )  {   output . append  ( " (abstract)" ) ; }   output . append  ( ": \n" ) ;   output . append  (   "subClasses of '" +  getQualifiedName  ( ) + "': " ) ;  for ( 
<<<<<<<
AttributedElementClass
=======
SC
>>>>>>>
 aec :  getAllSubClasses  ( ) )  {   output . append  (   "'" +  aec . getQualifiedName  ( ) + "' " ) ; }   output . append  (   "\nsuperClasses of '" +  getQualifiedName  ( ) + "': " ) ;  for ( 
<<<<<<<
AttributedElementClass
=======
SC
>>>>>>>
 aec :  getAllSuperClasses  ( ) )  {   output . append  (   "'" +  aec . getQualifiedName  ( ) + "' " ) ; }   output . append  (   "\ndirectSuperClasses of '" +  getQualifiedName  ( ) + "': " ) ;  for ( 
<<<<<<<
AttributedElementClass
=======
SC
>>>>>>>
 aec :  getDirectSuperClasses  ( ) )  {   output . append  (   "'" +  aec . getQualifiedName  ( ) + "' " ) ; }   output . append  (  attributesToString  ( ) ) ;  if  (  this instanceof VertexClass )  {   output . append  ( "outgoing edge classes: " ) ;   output . append  ( "\n" ) ;   output . append  ( "incomming edge classes: " ) ;   output . append  ( "\n" ) ; }   output . append  ( "\n" ) ;  return  output . toString  ( ) ; } }