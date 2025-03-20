package org.geojson;
import java.util.Arrays;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(property = "type", use = Id.NAME) @JsonSubTypes(value = { @Type(value = Feature.class), @Type(value = Polygon.class), @Type(value = MultiPolygon.class), @Type(value = FeatureCollection.class), @Type(value = Point.class), @Type(value = MultiPoint.class), @Type(value = MultiLineString.class), @Type(value = LineString.class) }) @JsonInclude(value = Include.NON_NULL) public abstract class GeoJsonObject {
  private Crs crs;

  private double[] bbox;

  @JsonInclude(value = Include.NON_EMPTY) private Map<String, Object> properties = new HashMap<String, Object>();

  public Crs getCrs() {
    return crs;
  }

  public void setCrs(Crs crs) {
    this.crs = crs;
  }

  public double[] getBbox() {
    return bbox;
  }

  public void setBbox(double[] bbox) {
    this.bbox = bbox;
  }

  public void setProperty(String key, Object value) {
    properties.put(key, value);
  }

  @SuppressWarnings(value = { "unchecked" }) public <T extends java.lang.Object> T getProperty(String key) {
    return (T) properties.get(key);
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public abstract <T extends java.lang.Object> T accept(GeoJsonObjectVisitor<T> geoJsonObjectVisitor);

  @Override public String toString() {
    return "GeoJsonObject{" + "properties=" + properties + "}";
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GeoJsonObject)) {
      return false;
    }
    GeoJsonObject that = (GeoJsonObject) o;
    if (!Arrays.equals(bbox, that.bbox)) {
      return false;
    }
    if (crs != null ? !crs.equals(that.crs) : that.crs != null) {
      return false;
    }
    return !(properties != null ? !properties.equals(that.properties) : that.properties != null);
  }

  @Override public int hashCode() {
    int result = crs != null ? crs.hashCode() : 0;
    result = 31 * result + (bbox != null ? Arrays.hashCode(bbox) : 0);
    result = 31 * result + (properties != null ? properties.hashCode() : 0);
    return result;
  }
}