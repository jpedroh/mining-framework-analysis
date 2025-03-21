package it.geosolutions.geoserver.rest.encoder;
import org.jdom.Element;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSWorkspaceEncoder extends PropertyXMLEncoder {
  public final static String WORKSPACE = "workspace";

  public final static String NAME = "name";

  public GSWorkspaceEncoder() {
    super(WORKSPACE);
  }

  public GSWorkspaceEncoder(String name) {
    super(
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/left.java
    "workspace"
=======
    WORKSPACE
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/right.java
    );
    addName(name);
  }

  /**
     * Add the name to this workspace
     * @param name
     * @throws IllegalStateException if name is already set
     */
  public void addName(String name) {
    final Element el = 
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/left.java
    contains("name")
=======
    ElementUtils.contains(getRoot(), NAME)
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/right.java
    ;
    if (el == null) {
      add(
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/left.java
      "name"
=======
      NAME
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/right.java
      , name);
    } else {
      throw new IllegalStateException("Workspace name is already set: " + el.getText());
    }
  }

  /**
     * add or change (if already set) the workspace name
     * @param name
     */
  public void setName(String name) {
    final Element el = 
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/left.java
    contains("name")
=======
    ElementUtils.contains(getRoot(), NAME)
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/right.java
    ;
    if (el == null) {
      add(
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/left.java
      "name"
=======
      NAME
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/right.java
      , name);
    } else {
      el.setText(name);
    }
  }

  public String getName() {
    final Element el = 
<<<<<<< /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/left.java
    contains("name")
=======
    ElementUtils.contains(getRoot(), NAME)
>>>>>>> /usr/src/app/output/geosolutions-it/geoserver-manager/352e241e0bf2b9c4a7d640dd5ca7795e1b5d303a/src/main/java/it/geosolutions/geoserver/rest/encoder/GSWorkspaceEncoder.java/right.java
    ;
    if (el != null) {
      return el.getTextTrim();
    } else {
      return null;
    }
  }
}