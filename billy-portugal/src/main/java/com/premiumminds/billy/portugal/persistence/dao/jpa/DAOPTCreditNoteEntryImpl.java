package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCreditNoteEntry;
import com.premiumminds.billy.portugal.persistence.entities.PTCreditNoteEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTCreditNoteEntryEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTCreditNoteEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTCreditNoteEntryEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.QJPAPTCreditNoteEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.QJPAPTCreditNoteEntryEntity;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQuery;

public class DAOPTCreditNoteEntryImpl extends AbstractDAOPTGenericInvoiceEntryImpl<PTCreditNoteEntryEntity, JPAPTCreditNoteEntryEntity> implements DAOPTCreditNoteEntry {
  @Inject public DAOPTCreditNoteEntryImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTCreditNoteEntryEntity getEntityInstance() {
    return new JPAPTCreditNoteEntryEntity();
  }

  @Override protected Class<JPAPTCreditNoteEntryEntity> getEntityClass() {
    return JPAPTCreditNoteEntryEntity.class;
  }

  @Override public PTCreditNoteEntity checkCreditNote(PTInvoice invoice) {
    QJPAPTCreditNoteEntity creditNoteEntity = QJPAPTCreditNoteEntity.jPAPTCreditNoteEntity;
    return new JPAQuery<JPAPTCreditNoteEntity>(this.getEntityManager()).from(creditNoteEntity).where(new QJPAPTCreditNoteEntryEntity(JPAPTCreditNoteEntryEntity.class, creditNoteEntity.entries.any().getMetadata(), PathInits.DIRECT2).reference.id.eq(invoice.getID())).select(creditNoteEntity).fetchFirst();
  }
}