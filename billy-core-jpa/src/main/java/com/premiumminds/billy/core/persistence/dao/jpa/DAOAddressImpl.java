package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOAddress;
import com.premiumminds.billy.core.persistence.entities.AddressEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAAddressEntity;

public class DAOAddressImpl extends AbstractDAO<AddressEntity, JPAAddressEntity> implements DAOAddress {
  @Inject public DAOAddressImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPAAddressEntity> getEntityClass() {
    return JPAAddressEntity.class;
  }

  @Override public AddressEntity getEntityInstance() {
    return new JPAAddressEntity();
  }
}