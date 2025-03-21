package ch.hsr.geohash.util;
import java.util.Iterator;
import ch.hsr.geohash.GeoHash;
import java.util.NoSuchElementException;

/**
 * Iterate over all of the values within a bounding box at a particular
 * resolution
 */
public class BoundingBoxGeoHashIterator implements Iterator<GeoHash> {
  private TwoGeoHashBoundingBox boundingBox;

  private GeoHash current;

  public BoundingBoxGeoHashIterator(TwoGeoHashBoundingBox bbox) {
    boundingBox = bbox;
    current = bbox.getSouthWestCorner();
  }

  public TwoGeoHashBoundingBox getBoundingBox() {
    return boundingBox;
  }

  @Override public boolean hasNext() {
    return 
<<<<<<< /usr/src/app/output/kungfoo/geohash-java/1e0aa77a02c168f39dc3c26f64257af4baffac6f/src/main/java/ch/hsr/geohash/util/BoundingBoxGeoHashIterator.java/left.java
    current.compareTo(boundingBox.getNorthEastCorner()) <= 0
=======
    current != null
>>>>>>> /usr/src/app/output/kungfoo/geohash-java/1e0aa77a02c168f39dc3c26f64257af4baffac6f/src/main/java/ch/hsr/geohash/util/BoundingBoxGeoHashIterator.java/right.java
    ;
  }

  @Override public GeoHash next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    GeoHash rv = current;
    if (rv.equals(boundingBox.getTopRight())) {
      current = null;
    } else {
      current = rv.next();
      while (hasNext() && !boundingBox.getBoundingBox().contains(current.getPoint())) {
        current = current.next();
      }
    }

<<<<<<< /usr/src/app/output/kungfoo/geohash-java/1e0aa77a02c168f39dc3c26f64257af4baffac6f/src/main/java/ch/hsr/geohash/util/BoundingBoxGeoHashIterator.java/left.java
    while (hasNext() && !boundingBox.getBoundingBox().contains(current.getOriginatingPoint())) {
      current = current.next();
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return rv;
  }

  @Override public void remove() {
    throw new UnsupportedOperationException();
  }
}