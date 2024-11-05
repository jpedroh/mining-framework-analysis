package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.entities.GenericInvoiceEntity;
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditNoteEntry;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntryEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESCreditNoteEntryEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditNoteEntryEntity;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQuery;

public class DAOESCreditNoteEntryImpl extends AbstractDAOESGenericInvoiceEntryImpl<ESCreditNoteEntryEntity, JPAESCreditNoteEntryEntity> implements DAOESCreditNoteEntry {
  @Inject public DAOESCreditNoteEntryImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESCreditNoteEntryEntity getEntityInstance() {
    return new JPAESCreditNoteEntryEntity();
  }

  @Override protected Class<JPAESCreditNoteEntryEntity> getEntityClass() {
    return JPAESCreditNoteEntryEntity.class;
  }

  @Override public ESCreditNoteEntity checkCreditNote(GenericInvoiceEntity invoice) {
    QJPAESCreditNoteEntity creditNoteEntity = QJPAESCreditNoteEntity.jPAESCreditNoteEntity;
    return new JPAQuery<JPAESCreditNoteEntity>(this.getEntityManager()).from(creditNoteEntity).where(new QJPAESCreditNoteEntryEntity(JPAESCreditNoteEntryEntity.class, creditNoteEntity.entries.any().getMetadata(), PathInits.DIRECT2).invoiceReference.id.eq(invoice.getID())).select(creditNoteEntity).fetchFirst();
  }
}