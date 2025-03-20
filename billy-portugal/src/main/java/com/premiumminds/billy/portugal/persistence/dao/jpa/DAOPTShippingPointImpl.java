package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOShippingPointImpl;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTShippingPoint;
import com.premiumminds.billy.portugal.persistence.entities.PTShippingPointEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTShippingPointEntity;

public class DAOPTShippingPointImpl extends DAOShippingPointImpl implements DAOPTShippingPoint {
  @Inject public DAOPTShippingPointImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTShippingPointEntity getEntityInstance() {
    return new JPAPTShippingPointEntity();
  }

  @Override protected Class<JPAPTShippingPointEntity> getEntityClass() {
    return JPAPTShippingPointEntity.class;
  }
}