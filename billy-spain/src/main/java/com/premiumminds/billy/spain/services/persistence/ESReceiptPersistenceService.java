package com.premiumminds.billy.spain.services.persistence;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.DAOTicket;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.persistence.dao.DAOESReceipt;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntity;
import com.premiumminds.billy.spain.services.entities.ESReceipt;

public class ESReceiptPersistenceService {
  private final DAOESReceipt daoReceipt;

  private final DAOTicket daoTicket;

  @Inject public ESReceiptPersistenceService(DAOESReceipt daoReceipt, DAOTicket daoTicket) {
    this.daoReceipt = daoReceipt;
    this.daoTicket = daoTicket;
  }

  public ESReceipt update(final Builder<ESReceipt> builder) {
    try {
      return new TransactionWrapper<ESReceipt>(this.daoReceipt) {
        @Override public ESReceipt runTransaction() throws Exception {
          ESReceiptEntity entity = (ESReceiptEntity) builder.build();
          return ESReceiptPersistenceService.this.daoReceipt.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESReceipt get(final UID uid) {
    try {
      return new TransactionWrapper<ESReceipt>(this.daoReceipt) {
        @Override public ESReceipt runTransaction() throws Exception {
          return ESReceiptPersistenceService.this.daoReceipt.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESReceipt getWithTicket(final UID ticketUID) throws NoResultException {
    try {
      return new TransactionWrapper<ESReceipt>(this.daoReceipt) {
        @Override public ESReceipt runTransaction() throws Exception {
          UID receiptUID = ESReceiptPersistenceService.this.daoTicket.getObjectEntityUID(ticketUID.getValue());
          return ESReceiptPersistenceService.this.daoReceipt.get(receiptUID);
        }
      }.execute();
    } catch (NoResultException e) {
      throw e;
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESReceipt findByNumber(final UID uidBusiness, final String number) {
    try {
      return new TransactionWrapper<ESReceipt>(this.daoReceipt) {
        @Override public ESReceipt runTransaction() throws Exception {
          return ESReceiptPersistenceService.this.daoReceipt.findByNumber(uidBusiness, number);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }
}