  package      it . geosolutions . geoserver . rest . encoder . metadata ;   import       it . geosolutions . geoserver . rest . encoder . utils . NestedElementEncoder ;   public class GSMetadataEncoder  <  T  extends GSDimensionInfoEncoder >  extends NestedElementEncoder  {   public GSMetadataEncoder  ( )  {  super  ( METADATA ) ; } 
<<<<<<<
  public void addMetadata  (   final String key ,   final T value )  {   this . add  ( key ,  value . getRoot  ( ) ) ; }
=======
>>>>>>>
   public final static String  METADATA = "metadata" ; }