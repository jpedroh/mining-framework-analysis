package com.premiumminds.billy.spain.services.persistence;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.DAOTicket;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.persistence.dao.DAOESSimpleInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESSimpleInvoiceEntity;
import com.premiumminds.billy.spain.services.entities.ESSimpleInvoice;

public class ESSimpleInvoicePersistenceService {
  protected final DAOESSimpleInvoice daoInvoice;

  protected final DAOTicket daoTicket;

  @Inject public ESSimpleInvoicePersistenceService(DAOESSimpleInvoice daoInvoice, DAOTicket daoTicket) {
    this.daoInvoice = daoInvoice;
    this.daoTicket = daoTicket;
  }

  public ESSimpleInvoice update(final Builder<ESSimpleInvoice> builder) {
    try {
      return new TransactionWrapper<ESSimpleInvoice>(this.daoInvoice) {
        @Override public ESSimpleInvoice runTransaction() throws Exception {
          ESSimpleInvoiceEntity entity = (ESSimpleInvoiceEntity) builder.build();
          return ESSimpleInvoicePersistenceService.this.daoInvoice.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESSimpleInvoice get(final UID uid) {
    try {
      return new TransactionWrapper<ESSimpleInvoice>(this.daoInvoice) {
        @Override public ESSimpleInvoice runTransaction() throws Exception {
          return ESSimpleInvoicePersistenceService.this.daoInvoice.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESSimpleInvoice getWithTicket(final UID ticketUID) {
    try {
      return new TransactionWrapper<ESSimpleInvoice>(this.daoInvoice) {
        @Override public ESSimpleInvoice runTransaction() throws NoResultException, BillyRuntimeException {
          UID objectUID = ESSimpleInvoicePersistenceService.this.daoTicket.getObjectEntityUID(ticketUID.getValue());
          return ESSimpleInvoicePersistenceService.this.daoInvoice.get(objectUID);
        }
      }.execute();
    } catch (NoResultException e) {
      throw e;
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }
}