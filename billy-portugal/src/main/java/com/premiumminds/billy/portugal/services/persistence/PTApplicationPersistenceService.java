package com.premiumminds.billy.portugal.services.persistence;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.persistence.services.PersistenceService;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTApplication;
import com.premiumminds.billy.portugal.persistence.entities.PTApplicationEntity;
import com.premiumminds.billy.portugal.services.entities.PTApplication;

public class PTApplicationPersistenceService implements PersistenceService<PTApplication> {
  protected final DAOPTApplication daoApplication;

  @Inject public PTApplicationPersistenceService(DAOPTApplication daoApplication) {
    this.daoApplication = daoApplication;
  }

  @Override public PTApplication create(final Builder<PTApplication> builder) {
    try {
      return new TransactionWrapper<PTApplication>(this.daoApplication) {
        @Override public PTApplication runTransaction() throws Exception {
          PTApplicationEntity entity = (PTApplicationEntity) builder.build();
          return (PTApplication) PTApplicationPersistenceService.this.daoApplication.create(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public PTApplication update(final Builder<PTApplication> builder) {
    try {
      return new TransactionWrapper<PTApplication>(this.daoApplication) {
        @Override public PTApplication runTransaction() throws Exception {
          PTApplicationEntity entity = (PTApplicationEntity) builder.build();
          return (PTApplication) PTApplicationPersistenceService.this.daoApplication.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public PTApplication get(final UID uid) {
    try {
      return new TransactionWrapper<PTApplication>(this.daoApplication) {
        @Override public PTApplication runTransaction() throws Exception {
          return PTApplicationPersistenceService.this.daoApplication.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }
}