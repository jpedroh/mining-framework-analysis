package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOContext;
import com.premiumminds.billy.core.persistence.entities.ContextEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAContextEntity;
import com.premiumminds.billy.core.services.entities.Context;

public class DAOContextImpl extends AbstractDAO<ContextEntity, JPAContextEntity> implements DAOContext {
  @Inject public DAOContextImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPAContextEntity> getEntityClass() {
    return JPAContextEntity.class;
  }

  @Override public ContextEntity getEntityInstance() {
    return new JPAContextEntity();
  }

  @Override public boolean isSubContext(Context sub, Context context) {
    if (sub.getParentContext() == null) {
      return false;
    }
    if (sub.getUID().equals(context.getUID())) {
      return true;
    }
    if (sub.getParentContext().getUID().equals(context.getUID())) {
      return true;
    }
    return this.isSubContext(sub.getParentContext(), context);
  }
}