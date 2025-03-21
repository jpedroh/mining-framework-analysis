/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.authorization.impl;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.dspace.app.rest.authorization.AuthorizationFeature;
import org.dspace.app.rest.authorization.AuthorizationFeatureDocumentation;
import org.dspace.app.rest.model.BaseObjectRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The manage versions feature. It can be used to verify
 * if the user can create/delete or update the version of an Item.
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
@Component
@AuthorizationFeatureDocumentation(name = CanManageVersionsFeature.NAME,
    description = "It can be used to verify if the user can create/delete or update the version of an Item")
public class CanManageVersionsFeature implements AuthorizationFeature {

    public static final String NAME = "canManageVersions";

    @Autowired
    private ItemService itemService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private ConfigurationService configurationService;


    @Override
    @SuppressWarnings("rawtypes")
    public boolean isAuthorized(Context context, BaseObjectRest object) throws SQLException {
        if (object instanceof ItemRest) {
            boolean isEnabled = configurationService.getBooleanProperty("versioning.enabled", true);
            if (!isEnabled || Objects.isNull(context.getCurrentUser())) {
                return false;
            }
<<<<<<< /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/authorization/impl/CanManageVersionsFeature.java/left.java
            Item item = itemService.find(context, UUID.fromString(((ItemRest) object).getUuid()));
            if (Objects.nonNull(item)) {
                boolean isBlockEntity = configurationService.getBooleanProperty("versioning.block.entity", true);
                boolean hasEntityType = StringUtils.isNotBlank(itemService.
                                        getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY));
                if (isBlockEntity && hasEntityType) {
                    return false;
                }
                return authorizeService.isAdmin(context, item);
||||||| /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/authorization/impl/CanManageVersionsFeature.java/base.java
            if (authorizeService.isAdmin(context)) {
                return true;
=======
            Item item = itemService.find(context, UUID.fromString(((ItemRest) object).getUuid()));
            if (Objects.nonNull(item)) {
                return authorizeService.isAdmin(context, item);
>>>>>>> /usr/src/app/output/dspace/dspace/c65314db9d4f1df5539b4785a5b234ee3ab8a2a5/dspace-server-webapp/src/main/java/org/dspace/app/rest/authorization/impl/CanManageVersionsFeature.java/right.java
            }
        }
        return false;
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[]{
            ItemRest.CATEGORY + "." + ItemRest.NAME
        };
    }

}
