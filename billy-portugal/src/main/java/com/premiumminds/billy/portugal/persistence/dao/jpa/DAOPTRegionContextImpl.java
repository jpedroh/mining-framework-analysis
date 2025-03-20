package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOContextImpl;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTRegionContext;
import com.premiumminds.billy.portugal.persistence.entities.PTRegionContextEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTRegionContextEntity;

public class DAOPTRegionContextImpl extends DAOContextImpl implements DAOPTRegionContext {
  @Inject public DAOPTRegionContextImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTRegionContextEntity getEntityInstance() {
    return new JPAPTRegionContextEntity();
  }

  @Override protected Class<JPAPTRegionContextEntity> getEntityClass() {
    return JPAPTRegionContextEntity.class;
  }
}