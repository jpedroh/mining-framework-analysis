package com.premiumminds.billy.france.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.france.persistence.dao.DAOFRCreditReceiptEntry;
import com.premiumminds.billy.france.persistence.entities.FRCreditReceiptEntity;
import com.premiumminds.billy.france.persistence.entities.FRCreditReceiptEntryEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.JPAFRCreditReceiptEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.JPAFRCreditReceiptEntryEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditReceiptEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditReceiptEntryEntity;
import com.premiumminds.billy.france.services.entities.FRReceipt;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQuery;

public class DAOFRCreditReceiptEntryImpl extends AbstractDAOFRGenericInvoiceEntryImpl<FRCreditReceiptEntryEntity, JPAFRCreditReceiptEntryEntity> implements DAOFRCreditReceiptEntry {
  @Inject public DAOFRCreditReceiptEntryImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public FRCreditReceiptEntryEntity getEntityInstance() {
    return new JPAFRCreditReceiptEntryEntity();
  }

  @Override protected Class<JPAFRCreditReceiptEntryEntity> getEntityClass() {
    return JPAFRCreditReceiptEntryEntity.class;
  }

  @Override public FRCreditReceiptEntity checkCreditReceipt(FRReceipt receipt) {
    QJPAFRCreditReceiptEntity creditReceiptEntity = QJPAFRCreditReceiptEntity.jPAFRCreditReceiptEntity;
    return new JPAQuery<JPAFRCreditReceiptEntity>(this.getEntityManager()).from(creditReceiptEntity).where(new QJPAFRCreditReceiptEntryEntity(JPAFRCreditReceiptEntryEntity.class, creditReceiptEntity.entries.any().getMetadata(), PathInits.DIRECT2).receiptReference.id.eq(receipt.getID())).select(creditReceiptEntity).fetchFirst();
  }
}