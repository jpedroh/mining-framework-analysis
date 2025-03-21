package it.geosolutions.geoserver.rest.encoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class GSLayerEncoder extends PropertyXMLEncoder {
  public GSLayerEncoder() {
    super("layer");
    addEnabled();
  }

  protected void addEnabled() {
    add("enabled", "true");
  }

  /**
     * @param enable true if layer should be set to enabled 
     */
  public void setEnabled(boolean enable) {
    if (enable) {
      set("enabled", "true");
    } else {
      set("enabled", "false");
    }
  }

  public void addDefaultStyle(String defaultStyle) {
    add("defaultStyle", defaultStyle);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setDefaultStyle(String defaultStyle) {
    set("defaultStyle", defaultStyle);
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSLayerEncoder.java/right.java
}