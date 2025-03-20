package com.premiumminds.billy.spain.persistence.dao.jpa;
import com.premiumminds.billy.core.services.StringID;
import com.premiumminds.billy.core.services.entities.Business;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditReceipt;
import com.querydsl.jpa.JPAExpressions;
import com.premiumminds.billy.spain.persistence.entities.ESCreditReceiptEntity;
import com.querydsl.jpa.JPQLQuery;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESCreditReceiptEntity;
import java.util.ArrayList;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESBusinessEntity;
import java.util.List;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditReceiptEntity;
import javax.inject.Inject;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditReceiptEntryEntity;
import javax.inject.Provider;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESGenericInvoiceEntity;
import javax.persistence.EntityManager;
import com.premiumminds.billy.spain.services.entities.ESCreditReceipt;

public class DAOESCreditReceiptImpl extends AbstractDAOESGenericInvoiceImpl<ESCreditReceiptEntity, JPAESCreditReceiptEntity> implements DAOESCreditReceipt {
  @Inject public DAOESCreditReceiptImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESCreditReceiptEntity getEntityInstance() {
    return new JPAESCreditReceiptEntity();
  }

  @Override protected Class<JPAESCreditReceiptEntity> getEntityClass() {
    return JPAESCreditReceiptEntity.class;
  }

  @Override public List<ESCreditReceipt> findByReferencedDocument(StringID<Business> uidCompany, StringID<GenericInvoice> uidInvoice) {
    QJPAESCreditReceiptEntity creditReceipt = QJPAESCreditReceiptEntity.jPAESCreditReceiptEntity;
    QJPAESCreditReceiptEntryEntity entry = QJPAESCreditReceiptEntryEntity.jPAESCreditReceiptEntryEntity;
    QJPAESGenericInvoiceEntity receipt = QJPAESGenericInvoiceEntity.jPAESGenericInvoiceEntity;
    final JPQLQuery<String> invQ = JPAExpressions.select(receipt.uid).from(receipt).where(receipt.uid.eq(uidInvoice.getIdentifier()));
    final JPQLQuery<String> entQ = JPAExpressions.select(entry.uid).from(entry).where(this.toDSL(entry.receiptReference, QJPAESGenericInvoiceEntity.class).uid.in(invQ));
    return new ArrayList<>(this.createQuery().from(creditReceipt).where(this.toDSL(creditReceipt.business, QJPAESBusinessEntity.class).uid.eq(uidCompany.toString()).and(this.toDSL(creditReceipt.entries.any(), QJPAESCreditReceiptEntryEntity.class).uid.in(entQ))).select(creditReceipt).fetch());
  }
}