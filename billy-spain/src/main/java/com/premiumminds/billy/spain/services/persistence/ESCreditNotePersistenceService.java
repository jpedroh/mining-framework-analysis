package com.premiumminds.billy.spain.services.persistence;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import com.premiumminds.billy.core.exceptions.BillyRuntimeException;
import com.premiumminds.billy.core.persistence.dao.DAOTicket;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.core.persistence.services.PersistenceService;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.util.NotImplemented;
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditNote;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntity;
import com.premiumminds.billy.spain.services.entities.ESCreditNote;

public class ESCreditNotePersistenceService implements PersistenceService<ESCreditNote> {
  protected final DAOESCreditNote daoCreditNote;

  protected final DAOTicket daoTicket;

  @Inject public ESCreditNotePersistenceService(DAOESCreditNote daoCreditNote, DAOTicket daoTicket) {
    this.daoCreditNote = daoCreditNote;
    this.daoTicket = daoTicket;
  }

  @Override @NotImplemented public ESCreditNote create(final Builder<ESCreditNote> builder) {
    return null;
  }

  @Override public ESCreditNote update(final Builder<ESCreditNote> builder) {
    try {
      return new TransactionWrapper<ESCreditNote>(this.daoCreditNote) {
        @Override public ESCreditNote runTransaction() throws Exception {
          ESCreditNoteEntity entity = (ESCreditNoteEntity) builder.build();
          return ESCreditNotePersistenceService.this.daoCreditNote.update(entity);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  @Override public ESCreditNote get(final UID uid) {
    try {
      return new TransactionWrapper<ESCreditNote>(this.daoCreditNote) {
        @Override public ESCreditNote runTransaction() throws Exception {
          return ESCreditNotePersistenceService.this.daoCreditNote.get(uid);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESCreditNote getWithTicket(final UID ticketUID) throws NoResultException, BillyRuntimeException {
    try {
      return new TransactionWrapper<ESCreditNote>(this.daoCreditNote) {
        @Override public ESCreditNote runTransaction() throws Exception {
          UID objectUID = ESCreditNotePersistenceService.this.daoTicket.getObjectEntityUID(ticketUID.getValue());
          return ESCreditNotePersistenceService.this.daoCreditNote.get(objectUID);
        }
      }.execute();
    } catch (NoResultException e) {
      throw e;
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public ESCreditNote findByNumber(final UID uidBusiness, final String number) {
    try {
      return new TransactionWrapper<ESCreditNote>(this.daoCreditNote) {
        @Override public ESCreditNote runTransaction() throws Exception {
          return ESCreditNotePersistenceService.this.daoCreditNote.findByNumber(uidBusiness, number);
        }
      }.execute();
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }

  public List<ESCreditNote> findByReferencedDocument(final UID uidCompany, final UID uidInvoice) {
    try {
      return new TransactionWrapper<List<ESCreditNote>>(this.daoCreditNote) {
        @Override public List<ESCreditNote> runTransaction() throws Exception {
          return ESCreditNotePersistenceService.this.daoCreditNote.findByReferencedDocument(uidCompany, uidInvoice);
        }
      }.execute();
    } catch (NoResultException e) {
      throw e;
    } catch (Exception e) {
      throw new BillyRuntimeException(e);
    }
  }
}