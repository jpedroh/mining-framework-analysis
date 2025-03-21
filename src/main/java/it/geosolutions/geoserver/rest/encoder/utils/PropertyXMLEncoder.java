package it.geosolutions.geoserver.rest.encoder.utils;
import org.jdom.Element;

/**
 * Creates an XML document by mapping properties to XML nodes.<br/>
 * You can set the root element name in the constructor. Any key/value pair will
 * be encoded as {@code <key>value</key>} node. <br/>
 * <br/>
 * 
 * <h4>Nested nodes</h4> Any key containing one or more slash ("/") will be
 * encoded as nested nodes; <br/>
 * e.g.:
 * 
 * <PRE>
 * {@code 
 *          key = "k1/k2/k3", value = "value" }
 * </pre>
 * 
 * will be encoded as
 * 
 * <PRE>
 * {@code        <k1><k2><k3>value</k3></k2></k1> }
 * </pre>
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class PropertyXMLEncoder extends XmlElement {
  public PropertyXMLEncoder(final String rootName) {
    super(rootName);
  }

  public void set(final String key, final String value) {
    if (key != null && value != null) {
      set(getRoot(), key, value);
    }
  }

  private void set(final Element e, final String key, final String value) {

<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/utils/PropertyXMLEncoder.java/left.java
    if (!key.contains("/")) {
      Element pp = null;
      if ((pp = contains(key)) == null) {
        add(e, key, value);
      } else {
        remove(pp);
        add(e, key, value);
      }
    } else {
      final int i = key.indexOf("/");
      final String childName = key.substring(0, i);
      final String newkey = key.substring(i + 1);
      Element child = e.getChild(childName);
      if (child == null) {
        child = new Element(childName);
        e.addContent(child);
        add(child, newkey, value);
      }
      set(child, newkey, value);
    }
=======
    if (key.contains("/")) {
      final int i = key.indexOf("/");
      final String childName = key.substring(0, i);
      final String newkey = key.substring(i + 1);
      Element child = e.getChild(childName);
      if (child == null) {
        child = new Element(childName);
        e.addContent(child);
        add(child, newkey, value);
      }
      set(child, newkey, value);
    } else {
      Element pp = null;
      if ((pp = ElementUtils.contains(e, key)) == null) {
        add(e, key, value);
      } else {
        ElementUtils.remove(e, pp);
        add(e, key, value);
      }
    }
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/utils/PropertyXMLEncoder.java/right.java
  }

  public void add(final String key, final String value) {
    if (key != null && value != null) {
      add(this.getRoot(), key, value);
    }
  }

  private void add(Element e, String key, String value) {
    if (key.contains("/")) {
      final int i = key.indexOf("/");
      final String childName = key.substring(0, i);
      final String newkey = key.substring(i + 1);
      Element child = e.getChild(childName);
      if (child == null) {
        child = new Element(childName);
        e.addContent(child);
      }
      add(child, newkey, value);
    } else {

<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/utils/PropertyXMLEncoder.java/left.java
      final int i = key.indexOf("/");
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/utils/PropertyXMLEncoder.java/left.java
      final String childName = key.substring(0, i);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/utils/PropertyXMLEncoder.java/left.java
      final String newkey = key.substring(i + 1);
=======
>>>>>>> Unknown file: This is a bug in JDime.

      e.addContent(new Element(key).setText(value));
    }
  }
}