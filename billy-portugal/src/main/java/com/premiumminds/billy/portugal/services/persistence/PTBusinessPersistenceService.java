package com.premiumminds.billy.portugal.services.persistence;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.persistence.services.PersistenceService;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.services.entities.PTBusiness;

public class PTBusinessPersistenceService implements PersistenceService<PTBusiness> {
  protected final DAOPTBusiness daoBusiness;

  @Inject public PTBusinessPersistenceService(DAOPTBusiness daoBusiness) {
    this.daoBusiness = daoBusiness;
  }

  @Override public PTBusiness create(final Builder<PTBusiness> builder) {
    try {
      return new TransactionWrapper<PTBusiness>(this.daoBusiness) {
        @Override public PTBusiness runTransaction() throws Exception {
          PTBusinessEntity entity = (PTBusinessEntity) builder.build();
          return (PTBusiness) PTBusinessPersistenceService.this.daoBusiness.create(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public PTBusiness update(final Builder<PTBusiness> builder) {
    try {
      return new TransactionWrapper<PTBusiness>(this.daoBusiness) {
        @Override public PTBusiness runTransaction() throws Exception {
          PTBusinessEntity entity = (PTBusinessEntity) builder.build();
          return (PTBusiness) PTBusinessPersistenceService.this.daoBusiness.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public PTBusiness get(final UID uid) {
    try {
      return new TransactionWrapper<PTBusiness>(this.daoBusiness) {
        @Override public PTBusiness runTransaction() throws Exception {
          return PTBusinessPersistenceService.this.daoBusiness.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }
}