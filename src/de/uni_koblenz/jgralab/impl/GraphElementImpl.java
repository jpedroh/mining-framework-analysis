  package    de . uni_koblenz . jgralab . impl ;   import    de . uni_koblenz . jgralab . Graph ;  import    de . uni_koblenz . jgralab . GraphIOException ;  import     de . uni_koblenz . jgralab . schema . Attribute ;  import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . Schema ;  import    de . uni_koblenz . jgralab . GraphElement ;  import     de . uni_koblenz . jgralab . schema . GraphElementClass ;   public abstract class GraphElementImpl  <  SC  extends  GraphElementClass  < SC , IC > ,  IC  extends  GraphElement  < SC , IC > >  implements  
<<<<<<<
InternalGraphElement
=======
 InternalGraphElement  < SC , IC >
>>>>>>>
  {   protected  int  id ;   protected GraphElementImpl  (  Graph graph )  {  assert  graph != null ;    this . graph =  ( GraphBaseImpl ) graph ; }   protected GraphBaseImpl  graph ;    @ Override public Graph getGraph  ( )  {  return graph ; }    @ Override public GraphClass getGraphClass  ( )  {  return 
<<<<<<<
 ( GraphClass )  graph . getAttributedElementClass  ( )
=======
 graph . getAttributedElementClass  ( )
>>>>>>>
 ; }    @ Override public Schema getSchema  ( )  {  return  graph . getSchema  ( ) ; }   public  @ Override void graphModified  ( )  {   graph . graphModified  ( ) ; }   public void ecaAttributeChanging  (  String name ,  Object oldValue ,  Object newValue )  {  if  (   !  graph . isLoading  ( ) && 
<<<<<<<
  graph . getECARuleManagerIfThere  ( ) != null
=======
 (  graph . hasECARuleManager  ( ) )
>>>>>>>
 )  {    graph . getECARuleManager  ( ) . fireBeforeChangeAttributeEvents  ( this , name , oldValue , newValue ) ; } }   public void ecaAttributeChanged  (  String name ,  Object oldValue ,  Object newValue )  {  if  (   !  graph . isLoading  ( ) && 
<<<<<<<
  graph . getECARuleManagerIfThere  ( ) != null
=======
 (  graph . hasECARuleManager  ( ) )
>>>>>>>
 )  {    graph . getECARuleManager  ( ) . fireAfterChangeAttributeEvents  ( this , name , oldValue , newValue ) ; } }    @ Override public  int getId  ( )  {  return id ; }    @ Override public void initializeAttributesWithDefaultValues  ( )  {  for ( Attribute attr :   getAttributedElementClass  ( ) . getAttributeList  ( ) )  {  if  (   attr . getDefaultValueAsString  ( ) == null )  {  continue ; }  try  {   internalSetDefaultValue  ( attr ) ; }  catch (   GraphIOException e )  {   e . printStackTrace  ( ) ; } } }   public  @ Override void internalSetDefaultValue  (  Attribute attr )  throws GraphIOException  {   attr . setDefaultValue  ( this ) ; }    @ Override public boolean isInstanceOf  (  SC cls )  {  return   cls . getSchemaClass  ( ) . isInstance  ( this ) ; } }