package it.geosolutions.geoserver.rest.encoder.metadata;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;
import java.math.BigDecimal;
import org.jdom.Element;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public class GSDimensionInfoEncoder extends XmlElement {
  public final static String DIMENSIONINFO = "dimensionInfo";

  public final static String RESOLUTION = "resolution";

  public final static String PRESENTATION = "presentation";

  private boolean enabled;

  public enum Presentation {
    LIST,
    CONTINUOUS_INTERVAL
  }

  public enum PresentationDiscrete {
    DISCRETE_INTERVAL
  }

  /**
	 * Build a dimension
	 * @param enabled enable dimension if true
	 * @note a enabled dimension also need a presentation mode set.
	 */
  public GSDimensionInfoEncoder(final boolean enabled) {
    super(DIMENSIONINFO);
    add("enabled", (enabled) ? "true" : "false");
    this.enabled = enabled;
  }

  /**
	 * build an not enabled dimension
	 */
  public GSDimensionInfoEncoder() {
    super(DIMENSIONINFO);
    add("enabled", "false");
    this.enabled = Boolean.FALSE;
  }

  public void setEnabled(final boolean enabled) {
    set("enabled", "true");
    this.enabled = Boolean.TRUE;
  }

  public void addPresentation(final Presentation pres) {
    if (enabled) {
      add(
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/left.java
      "presentation"
=======
      PRESENTATION
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java
      , pres.toString());
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setPresentation(final Presentation pres) {
    if (enabled) {
      set(PRESENTATION, pres.toString());
      remove(RESOLUTION);
    }
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java


  public void addPresentation(final PresentationDiscrete pres, final BigDecimal interval) {
    if (enabled) {
      add(
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/left.java
      "presentation"
=======
      PRESENTATION
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java
      , pres.toString());
      add(
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/left.java
      "resolution"
=======
      RESOLUTION
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java
      , String.valueOf(interval));
    }
  }

  public void add(String nodename, String nodetext) {
    final Element el = new Element(nodename);
    el.setText(nodetext);
    this.addContent(el);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void setPresentation(final PresentationDiscrete pres, final BigDecimal interval) {
    if (enabled) {
      set(PRESENTATION, pres.toString());
      set(RESOLUTION, String.valueOf(interval));
    }
  }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/metadata/GSDimensionInfoEncoder.java/right.java
}