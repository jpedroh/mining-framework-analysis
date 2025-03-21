package it.geosolutions.geoserver.rest.encoder.metadata;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;

public class GSMetadataEncoder<T extends GSDimensionInfoEncoder> extends NestedElementEncoder {
  public final static String METADATA = "metadata";

  public GSMetadataEncoder() {
    super(METADATA);
  }


<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSMetadataEncoder.java/left.java
  public void addMetadata(final String key, final T value) {
    this.add(key, value.getRoot());
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}