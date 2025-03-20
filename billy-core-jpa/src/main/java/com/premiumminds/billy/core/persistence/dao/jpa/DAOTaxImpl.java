package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOTax;
import com.premiumminds.billy.core.persistence.entities.TaxEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPATaxEntity;

public class DAOTaxImpl extends AbstractDAO<TaxEntity, JPATaxEntity> implements DAOTax {
  @Inject public DAOTaxImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPATaxEntity> getEntityClass() {
    return JPATaxEntity.class;
  }

  @Override public TaxEntity getEntityInstance() {
    return new JPATaxEntity();
  }
}