package com.premiumminds.billy.core.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOContext;
import com.premiumminds.billy.core.services.builders.impl.ContextBuilderImpl;

/**
 * @author Francisco Vargas
 *
 *         The Billy services entity for a context. A context is an aggregation
 *         concept which might represent a region context, political context,
 *         financial context and so on.
 *
 *         The purpose of this entity is to create relationships between
 *         different concepts. For instance, a {@link Tax} entity needs a
 *         defined context over which it is valid. An example would be a tax for
 *         Portugal which is only applied in a Portuguese context.
 */
public interface Context extends Entity {
  public static class Builder extends ContextBuilderImpl<Builder, Context> {
    @Inject public Builder(DAOContext daoContext) {
      super(daoContext);
    }
  }

  public String getName();

  public String getDescription();

  public <T extends Context> T getParentContext();
}