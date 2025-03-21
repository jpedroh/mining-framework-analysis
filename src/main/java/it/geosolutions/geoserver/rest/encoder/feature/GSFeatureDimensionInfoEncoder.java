package it.geosolutions.geoserver.rest.encoder.feature;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;

public class GSFeatureDimensionInfoEncoder extends GSDimensionInfoEncoder {
  public final static String ATTRIBUTE = "attribute";

  /**
	 * if this dimension is enabled this constructor should be called.
	 * @param attribute the attribute to use as dimension
	 */
  public GSFeatureDimensionInfoEncoder(final String attribute) {
    super(true);
    add(ATTRIBUTE, attribute);
  }

  /**
	 * Change the attribute used as dimension
	 * @param attribute the attribute to use as dimension
	 */
  public void setAttribute(final String attribute) {
    set(ATTRIBUTE, attribute);
  }
}