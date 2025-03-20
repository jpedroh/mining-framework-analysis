package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOShippingPoint;
import com.premiumminds.billy.core.persistence.entities.ShippingPointEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAShippingPointEntity;

public class DAOShippingPointImpl extends AbstractDAO<ShippingPointEntity, JPAShippingPointEntity> implements DAOShippingPoint {
  @Inject public DAOShippingPointImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPAShippingPointEntity> getEntityClass() {
    return JPAShippingPointEntity.class;
  }

  @Override public ShippingPointEntity getEntityInstance() {
    return new JPAShippingPointEntity();
  }
}