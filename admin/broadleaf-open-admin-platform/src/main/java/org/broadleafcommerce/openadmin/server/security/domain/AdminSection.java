package org.broadleafcommerce.openadmin.server.security.domain;
import java.io.Serializable;
import java.util.List;

/**
 * @author elbertbautista
 */
public interface AdminSection extends Serializable {
  Long getId();

  String getName();

  void setName(String name);

  String getSectionKey();

  void setSectionKey(String sectionKey);

  String getUrl();

  void setUrl(String url);

  List<AdminPermission> getPermissions();

  void setPermissions(List<AdminPermission> permissions);

  /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
  @Deprecated void setDisplayController(String displayController);

  /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
  @Deprecated String getDisplayController();

  AdminModule getModule();

  void setModule(AdminModule module);

  /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
  @Deprecated Boolean getUseDefaultHandler();

  /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
  @Deprecated void setUseDefaultHandler(Boolean useDefaultHandler);

  String getCeilingEntity();

  void setCeilingEntity(String ceilingEntity);

  Integer getDisplayOrder();

  void setDisplayOrder(Integer displayOrder);

  boolean isFolderable();

  void setFolderable(boolean folderable);

  boolean isFolderedByDefault();

  void setFolderedByDefault(boolean folderedByDefault);
}