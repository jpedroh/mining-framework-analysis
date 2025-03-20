package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOAddressImpl;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTAddress;
import com.premiumminds.billy.portugal.persistence.entities.PTAddressEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTAddressEntity;

public class DAOPTAddressImpl extends DAOAddressImpl implements DAOPTAddress {
  @Inject public DAOPTAddressImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTAddressEntity getEntityInstance() {
    return new JPAPTAddressEntity();
  }

  @Override protected Class<JPAPTAddressEntity> getEntityClass() {
    return JPAPTAddressEntity.class;
  }
}