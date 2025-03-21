package it.geosolutions.geoserver.rest.encoder.coverage;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;

/**
 * Creates an XML 
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class GSCoverageEncoder extends GSResourceEncoder<GSDimensionInfoEncoder> {
  public GSCoverageEncoder() {
    super("coverage");
  }
}