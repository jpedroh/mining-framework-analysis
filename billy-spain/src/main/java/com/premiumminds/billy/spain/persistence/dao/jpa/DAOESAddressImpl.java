package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOAddressImpl;
import com.premiumminds.billy.spain.persistence.dao.DAOESAddress;
import com.premiumminds.billy.spain.persistence.entities.ESAddressEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESAddressEntity;

public class DAOESAddressImpl extends DAOAddressImpl implements DAOESAddress {
  @Inject public DAOESAddressImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESAddressEntity getEntityInstance() {
    return new JPAESAddressEntity();
  }

  @Override protected Class<JPAESAddressEntity> getEntityClass() {
    return JPAESAddressEntity.class;
  }
}