package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOBusiness;
import com.premiumminds.billy.core.persistence.entities.BusinessEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPABusinessEntity;

public class DAOBusinessImpl extends AbstractDAO<BusinessEntity, JPABusinessEntity> implements DAOBusiness {
  @Inject public DAOBusinessImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPABusinessEntity> getEntityClass() {
    return JPABusinessEntity.class;
  }

  @Override public BusinessEntity getEntityInstance() {
    return new JPABusinessEntity();
  }
}