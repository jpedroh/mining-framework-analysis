package org.dspace.app.orcid.model;

<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
import java.util.Arrays;

=======
>>>>>>> Unknown file: This is a bug in JDime.

/**
 * The entity types of the ORCID objects that can be synchronized.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */public enum OrcidEntityType {
  PUBLICATION(
<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
  "Publication"
=======
>>>>>>> Unknown file: This is a bug in JDime.
  , 
<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
  "/work"
=======
>>>>>>> Unknown file: This is a bug in JDime.
  ),
  FUNDING(
<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
  "Project"
=======
>>>>>>> Unknown file: This is a bug in JDime.
  , 
<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
  "/funding"
=======
>>>>>>> Unknown file: This is a bug in JDime.
  )
  ;

  private final String entityType;


<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
  private OrcidEntityType(String entityType, String path) {
    this.entityType = entityType;
    this.path = path;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public String getEntityType() {
    return entityType;
  }

  public static boolean isValidEntityType(String entityType) {
    return Arrays.stream(OrcidEntityType.values()).anyMatch((orcidEntityType) -> orcidEntityType.getEntityType().equalsIgnoreCase(entityType));
  }

  public static OrcidEntityType fromEntityType(String entityType) {
    return Arrays.stream(OrcidEntityType.values()).filter((orcidEntityType) -> orcidEntityType.getEntityType().equalsIgnoreCase(entityType)).findFirst().orElse(null);
  }
}