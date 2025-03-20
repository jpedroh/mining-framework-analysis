package com.premiumminds.billy.spain.services.persistence;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.persistence.services.PersistenceService;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.persistence.dao.DAOESProduct;
import com.premiumminds.billy.spain.persistence.entities.ESProductEntity;
import com.premiumminds.billy.spain.services.entities.ESProduct;

public class ESProductPersistenceService implements PersistenceService<ESProduct> {
  protected final DAOESProduct daoProduct;

  @Inject public ESProductPersistenceService(DAOESProduct daoProduct) {
    this.daoProduct = daoProduct;
  }

  @Override public ESProduct create(final Builder<ESProduct> builder) {
    try {
      return new TransactionWrapper<ESProduct>(this.daoProduct) {
        @Override public ESProduct runTransaction() throws Exception {
          ESProductEntity entity = (ESProductEntity) builder.build();
          return (ESProduct) ESProductPersistenceService.this.daoProduct.create(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public ESProduct update(final Builder<ESProduct> builder) {
    try {
      return new TransactionWrapper<ESProduct>(this.daoProduct) {
        @Override public ESProduct runTransaction() throws Exception {
          ESProductEntity entity = (ESProductEntity) builder.build();
          return (ESProduct) ESProductPersistenceService.this.daoProduct.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public ESProduct get(final UID uid) {
    try {
      return new TransactionWrapper<ESProduct>(this.daoProduct) {
        @Override public ESProduct runTransaction() throws Exception {
          return (ESProduct) ESProductPersistenceService.this.daoProduct.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public boolean exists(final UID uid) {
    return this.daoProduct.exists(uid);
  }
}