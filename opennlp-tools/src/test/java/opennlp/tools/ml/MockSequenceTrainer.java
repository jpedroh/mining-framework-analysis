  package   opennlp . tools . ml ;   import   java . util . Map ;  import    opennlp . tools . commons . Sample ;  import     opennlp . tools . ml . model . AbstractModel ;  import     opennlp . tools . ml . model . SequenceStream ;  import    opennlp . tools . util . TrainingParameters ;   public class MockSequenceTrainer  implements  EventModelSequenceTrainer  {   public AbstractModel train  (   SequenceStream  <  ? extends Sample > events )  throws IOException  {  return null ; }    @ Override public void init  (   Map  < String , Object > trainParams ,   Map  < String , String > reportMap )  { }    @ Override public void init  (  TrainingParameters trainParams ,   Map  < String , String > reportMap )  { } 
<<<<<<<
=======
  public AbstractModel train  (  SequenceStream events )  {  return null ; }
>>>>>>>
 }