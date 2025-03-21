package org.dspace.app.rest.model;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dspace.app.rest.RestResourceController;

/**
 * Find out your authentication status.
 */
public class AuthenticationStatusRest extends BaseObjectRest<Integer> {
  private boolean okay;

  private boolean authenticated;

  private String authenticationMethod;

  public static final String NAME = "status";

  private EPersonRest ePersonRest;

  private List<GroupRest> specialGroups;

  public static final String SPECIALGROUPS = "specialGroups";

  public static final String CATEGORY = RestAddressableModel.AUTHENTICATION;

  @Override public String getCategory() {
    return CATEGORY;
  }

  @Override public String getType() {
    return NAME;
  }

  @Override @JsonIgnore public String getTypePlural() {
    return getType();
  }

  public Class getController() {
    return RestResourceController.class;
  }

  public AuthenticationStatusRest() {
    setOkay(true);
    setAuthenticated(false);
  }

  public AuthenticationStatusRest(EPersonRest eperson) {
    setOkay(true);
    if (eperson != null) {
      setAuthenticated(true);
      this.ePersonRest = eperson;
    }
  }

  @LinkRest(name = "eperson") @JsonIgnore public EPersonRest getEPersonRest() {
    return ePersonRest;
  }

  public void setEPersonRest(EPersonRest ePersonRest) {
    this.ePersonRest = ePersonRest;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }

  public boolean isOkay() {
    return okay;
  }

  public void setOkay(boolean okay) {
    this.okay = okay;
  }

  public String getAuthenticationMethod() {
    return authenticationMethod;
  }

  public void setAuthenticationMethod(final String authenticationMethod) {
    this.authenticationMethod = authenticationMethod;
  }

  public void setSpecialGroups(List<GroupRest> groupList) {
    this.specialGroups = groupList;
  }

  @LinkRest(name = "specialGroups") @JsonIgnore public List<GroupRest> getSpecialGroups() {
    return specialGroups;
  }
}