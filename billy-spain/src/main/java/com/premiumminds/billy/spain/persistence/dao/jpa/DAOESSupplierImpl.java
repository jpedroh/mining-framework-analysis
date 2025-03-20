package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOSupplierImpl;
import com.premiumminds.billy.spain.persistence.dao.DAOESSupplier;
import com.premiumminds.billy.spain.persistence.entities.ESSupplierEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESSupplierEntity;

public class DAOESSupplierImpl extends DAOSupplierImpl implements DAOESSupplier {
  @Inject public DAOESSupplierImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESSupplierEntity getEntityInstance() {
    return new JPAESSupplierEntity();
  }

  @Override protected Class<JPAESSupplierEntity> getEntityClass() {
    return JPAESSupplierEntity.class;
  }
}