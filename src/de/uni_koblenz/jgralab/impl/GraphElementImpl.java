  package    de . uni_koblenz . jgralab . impl ;   import    de . uni_koblenz . jgralab . Graph ;  import    de . uni_koblenz . jgralab . GraphIOException ;  import     de . uni_koblenz . jgralab . schema . Attribute ;  import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . Schema ;   public abstract class GraphElementImpl  implements  GraphElementBase  {   protected  int  id ;   protected GraphElementImpl  (  Graph graph )  {  assert  graph != null ;    this . graph =  ( GraphBaseImpl ) graph ; }   protected GraphBaseImpl  graph ;    @ Override public Graph getGraph  ( )  {  return graph ; }    @ Override public GraphClass getGraphClass  ( )  {  return  ( GraphClass )  graph . getAttributedElementClass  ( ) ; }    @ Override public Schema getSchema  ( )  {  return  graph . getSchema  ( ) ; }   public void graphModified  ( )  {   graph . graphModified  ( ) ; }   public void ecaAttributeChanging  (  String name ,  Object oldValue ,  Object newValue )  {  if  ( 
<<<<<<<
  !   this . graph . isLoading  ( ) &&    this . graph . getECARuleManagerIfThere  ( ) != null
=======
 !  graph . isLoading  ( )
>>>>>>>
 )  {    graph . getECARuleManagerIfThere  ( ) . fireBeforeChangeAttributeEvents  ( this , name , oldValue , newValue ) ; } }   public void ecaAttributeChanged  (  String name ,  Object oldValue ,  Object newValue )  {  if  ( 
<<<<<<<
  !   this . graph . isLoading  ( ) &&    this . graph . getECARuleManagerIfThere  ( ) != null
=======
 !  graph . isLoading  ( )
>>>>>>>
 )  {    graph . getECARuleManagerIfThere  ( ) . fireAfterChangeAttributeEvents  ( this , name , oldValue , newValue ) ; } }    @ Override public  int getId  ( )  {  return id ; }    @ Override public void initializeAttributesWithDefaultValues  ( )  {  for ( Attribute attr :   getAttributedElementClass  ( ) . getAttributeList  ( ) )  {  if  (   attr . getDefaultValueAsString  ( ) == null )  {  continue ; }  try  {   internalSetDefaultValue  ( attr ) ; }  catch (   GraphIOException e )  {   e . printStackTrace  ( ) ; } } }   public void internalSetDefaultValue  (  Attribute attr )  throws GraphIOException  {   attr . setDefaultValue  ( this ) ; } }