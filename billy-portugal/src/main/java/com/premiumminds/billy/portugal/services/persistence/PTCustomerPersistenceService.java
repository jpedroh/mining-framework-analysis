package com.premiumminds.billy.portugal.services.persistence;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.persistence.services.PersistenceService;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCustomer;
import com.premiumminds.billy.portugal.persistence.entities.PTCustomerEntity;
import com.premiumminds.billy.portugal.services.entities.PTCustomer;

public class PTCustomerPersistenceService implements PersistenceService<PTCustomer> {
  protected final DAOPTCustomer daoCustomer;

  @Inject public PTCustomerPersistenceService(DAOPTCustomer daoCustomer) {
    this.daoCustomer = daoCustomer;
  }

  @Override public PTCustomer create(final Builder<PTCustomer> builder) {
    try {
      return new TransactionWrapper<PTCustomer>(this.daoCustomer) {
        @Override public PTCustomer runTransaction() throws Exception {
          PTCustomerEntity entity = (PTCustomerEntity) builder.build();
          return (PTCustomer) PTCustomerPersistenceService.this.daoCustomer.create(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public PTCustomer update(final Builder<PTCustomer> builder) {
    try {
      return new TransactionWrapper<PTCustomer>(this.daoCustomer) {
        @Override public PTCustomer runTransaction() throws Exception {
          PTCustomerEntity entity = (PTCustomerEntity) builder.build();
          return (PTCustomer) PTCustomerPersistenceService.this.daoCustomer.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public PTCustomer get(final UID uid) {
    try {
      return new TransactionWrapper<PTCustomer>(this.daoCustomer) {
        @Override public PTCustomer runTransaction() throws Exception {
          return (PTCustomer) PTCustomerPersistenceService.this.daoCustomer.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }
}