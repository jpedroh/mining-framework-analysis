package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.entities.GenericInvoiceEntity;
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditReceiptEntry;
import com.premiumminds.billy.spain.persistence.entities.ESCreditReceiptEntity;
import com.premiumminds.billy.spain.persistence.entities.ESCreditReceiptEntryEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESCreditReceiptEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESCreditReceiptEntryEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditReceiptEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditReceiptEntryEntity;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.jpa.impl.JPAQuery;

public class DAOESCreditReceiptEntryImpl extends AbstractDAOESGenericInvoiceEntryImpl<ESCreditReceiptEntryEntity, JPAESCreditReceiptEntryEntity> implements DAOESCreditReceiptEntry {
  @Inject public DAOESCreditReceiptEntryImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESCreditReceiptEntryEntity getEntityInstance() {
    return new JPAESCreditReceiptEntryEntity();
  }

  @Override protected Class<JPAESCreditReceiptEntryEntity> getEntityClass() {
    return JPAESCreditReceiptEntryEntity.class;
  }

  @Override public ESCreditReceiptEntity checkCreditReceipt(GenericInvoiceEntity receipt) {
    QJPAESCreditReceiptEntity creditReceiptEntity = QJPAESCreditReceiptEntity.jPAESCreditReceiptEntity;
    return new JPAQuery<JPAESCreditReceiptEntity>(this.getEntityManager()).from(creditReceiptEntity).where(new QJPAESCreditReceiptEntryEntity(JPAESCreditReceiptEntryEntity.class, creditReceiptEntity.entries.any().getMetadata(), PathInits.DIRECT2).receiptReference.id.eq(receipt.getID())).select(creditReceiptEntity).fetchFirst();
  }
}