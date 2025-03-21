/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.orcid.model;

import java.util.Arrays;

/**
 * The types of activities defined on ORCID that can be synchronized.
 *
 * @author Luca Giamminonni (luca.giamminonni at 4science.it)
 *
 */
public enum OrcidEntityType {

<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
    PUBLICATION("Publication", "/work"),
    FUNDING("Project", "/funding");

    private final String entityType;
||||||| /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/base.java
    PUBLICATION("/work"),
    FUNDING("/funding");
=======
    /**
     * The publication/work activity.
     */
    PUBLICATION,
>>>>>>> /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/right.java

    /**
     * The funding activity.
     */
    FUNDING;

<<<<<<< /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/left.java
    private OrcidEntityType(String entityType, String path) {
        this.entityType = entityType;
        this.path = path;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getPath() {
        return path;
    }

    public static boolean isValidEntityType(String entityType) {
        return Arrays.stream(OrcidEntityType.values())
            .anyMatch(orcidEntityType -> orcidEntityType.getEntityType().equalsIgnoreCase(entityType));
    }

    public static OrcidEntityType fromEntityType(String entityType) {
        return Arrays.stream(OrcidEntityType.values())
            .filter(orcidEntityType -> orcidEntityType.getEntityType().equalsIgnoreCase(entityType))
            .findFirst()
            .orElse(null);
    }
||||||| /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/base.java
    private OrcidEntityType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static boolean isValid(String entityType) {
        return entityType != null ? EnumUtils.isValidEnum(OrcidEntityType.class, entityType.toUpperCase()) : false;
    }

    public static OrcidEntityType fromString(String entityType) {
        return isValid(entityType) ? OrcidEntityType.valueOf(entityType.toUpperCase()) : null;
    }
=======
>>>>>>> /usr/src/app/output/dspace/dspace/bb60c7d200b81f9c3cc39ab1f2acf8393a428bde/dspace-api/src/main/java/org/dspace/app/orcid/model/OrcidEntityType.java/right.java
}
