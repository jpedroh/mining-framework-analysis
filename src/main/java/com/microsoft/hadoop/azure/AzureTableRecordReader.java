  package    com . microsoft . hadoop . azure ;   import static     com . microsoft . hadoop . azure . AzureTableConfiguration .  * ;  import  java . io .  * ;  import   java . net . URISyntaxException ;  import  java . util .  * ;  import     org . apache . hadoop . conf . Configuration ;  import    org . apache . hadoop . io .  * ;  import    org . apache . hadoop . mapreduce .  * ;  import     com . microsoft . windowsazure . storage . StorageException ;  import     com . microsoft . windowsazure . storage . table .  * ;   public class AzureTableRecordReader  extends  RecordReader  < Text , WritableEntity >  {   private  Iterator  < WritableEntity >  queryResults ;   private WritableEntity  currentEntity ;   private Text  currentKey =  new Text  ( ) ;   public void initialize  (  InputSplit split ,  TaskAttemptContext context )  throws IOException , InterruptedException  {  Configuration  job =  context . getConfiguration  ( ) ;  CloudTableClient  tableClient =  createTableClient  ( job ) ;  String  tableName =  getTableName  ( job ) ;   TableQuery  < WritableEntity >  query =   (  ( AzureTableInputSplit ) split ) . getQuery  ( ) ;  try  {   queryResults =    tableClient . getTableReference  ( tableName ) . execute  ( query ) . iterator  ( ) ; }  catch (   StorageException e )  { 
<<<<<<<
  e . printStackTrace  ( ) ;
=======
 throw  new IOException  ( e ) ;
>>>>>>>
 }  catch (   URISyntaxException e )  { 
<<<<<<<
  e . printStackTrace  ( ) ;
=======
 throw  new IllegalArgumentException  ( e ) ;
>>>>>>>
 } }   public boolean nextKeyValue  ( )  throws IOException , InterruptedException  {  if  (  queryResults . hasNext  ( ) )  {   currentEntity =  queryResults . next  ( ) ;   currentKey . set  (  currentEntity . getRowKey  ( ) ) ;  return true ; } else  {   currentEntity = null ;  return false ; } }   public Text getCurrentKey  ( )  throws IOException , InterruptedException  {  if  (  currentEntity == null )  {  return null ; }  return currentKey ; }   public WritableEntity getCurrentValue  ( )  throws IOException , InterruptedException  {  return currentEntity ; }   public  float getProgress  ( )  throws IOException , InterruptedException  {  return 0.5f ; }   public void close  ( )  throws IOException  { } }