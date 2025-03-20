package io.buji.pac4j.subject;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import java.io.Serializable;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The principal to store the pac4j profiles.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jPrincipal implements Principal, Serializable {
  private final String principalNameAttribute;

  private final LinkedHashMap<String, CommonProfile> profiles;

  public Pac4jPrincipal(final LinkedHashMap<String, CommonProfile> profiles) {
    this.profiles = profiles;
    this.principalNameAttribute = null;
  }

  /**
     * Construct a Pac4jPrincipal and specify which attribute in the CommonProfile
     * should be used for the principal name.
     * 
     * @param profiles A map containing all of the CommonProfiles created by Pac4j
     *          authorization.
     * @param principalNameAttribute The attribute name in the CommonProfile that 
     *          holds the principal name. A null or blank value means
     *          that CommonProfile.getId() should be used as the principal name.
     */
  public Pac4jPrincipal(final LinkedHashMap<String, CommonProfile> profiles, String principalNameAttribute) {
    this.profiles = profiles;
    this.principalNameAttribute = CommonHelper.isBlank(principalNameAttribute) ? null : principalNameAttribute.trim();
  }

  /**
     * Get the main profile of the authenticated user.
     *
     * @return the main profile
     */
  public CommonProfile getProfile() {
    return ProfileHelper.flatIntoOneProfile(this.profiles).get();
  }

  /**
     * Get all the profiles of the authenticated user.
     *
     * @return the list of profiles
     */
  public List<CommonProfile> getProfiles() {
    return ProfileHelper.flatIntoAProfileList(this.profiles);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Pac4jPrincipal that = (Pac4jPrincipal) o;
    return profiles != null ? profiles.equals(that.profiles) : that.profiles == null;
  }

  @Override public int hashCode() {
    return profiles != null ? profiles.hashCode() : 0;
  }

  @Override public String getName() {
    CommonProfile profile = this.getProfile();
    if (null == principalNameAttribute) {
      return profile.getId();
    }
    Object attrValue = profile.getAttribute(principalNameAttribute);
    return 
<<<<<<< /usr/src/app/output/bujiio/buji-pac4j/1c7eb2acf1643212c399c9485bd30e3fdd5d88a8/src/main/java/io/buji/pac4j/subject/Pac4jPrincipal.java/left.java
    profile.getId()
=======
    (null == attrValue) ? null : String.valueOf(attrValue)
>>>>>>> /usr/src/app/output/bujiio/buji-pac4j/1c7eb2acf1643212c399c9485bd30e3fdd5d88a8/src/main/java/io/buji/pac4j/subject/Pac4jPrincipal.java/right.java
    ;
  }

  @Override public String toString() {
    return CommonHelper.toString(this.getClass(), "profiles", getProfiles());
  }
}