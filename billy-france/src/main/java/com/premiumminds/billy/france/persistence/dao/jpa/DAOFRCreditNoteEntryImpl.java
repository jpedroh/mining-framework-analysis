package com.premiumminds.billy.france.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.france.persistence.dao.DAOFRCreditNoteEntry;
import com.premiumminds.billy.france.persistence.entities.FRCreditNoteEntity;
import com.premiumminds.billy.france.persistence.entities.FRCreditNoteEntryEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.JPAFRCreditNoteEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.JPAFRCreditNoteEntryEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditNoteEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditNoteEntryEntity;
import com.premiumminds.billy.france.services.entities.FRInvoice;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQuery;

public class DAOFRCreditNoteEntryImpl extends AbstractDAOFRGenericInvoiceEntryImpl<FRCreditNoteEntryEntity, JPAFRCreditNoteEntryEntity> implements DAOFRCreditNoteEntry {
  @Inject public DAOFRCreditNoteEntryImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public FRCreditNoteEntryEntity getEntityInstance() {
    return new JPAFRCreditNoteEntryEntity();
  }

  @Override protected Class<JPAFRCreditNoteEntryEntity> getEntityClass() {
    return JPAFRCreditNoteEntryEntity.class;
  }

  @Override public FRCreditNoteEntity checkCreditNote(FRInvoice invoice) {
    QJPAFRCreditNoteEntity creditNoteEntity = QJPAFRCreditNoteEntity.jPAFRCreditNoteEntity;
    return new JPAQuery<JPAFRCreditNoteEntity>(this.getEntityManager()).from(creditNoteEntity).where(new QJPAFRCreditNoteEntryEntity(JPAFRCreditNoteEntryEntity.class, creditNoteEntity.entries.any().getMetadata(), PathInits.DIRECT2).invoiceReference.id.eq(invoice.getID())).select(creditNoteEntity).fetchFirst();
  }
}