package com.premiumminds.billy.france.persistence.dao.jpa;
import java.util.ArrayList;
import com.querydsl.jpa.JPAExpressions;
import java.util.List;
import com.querydsl.jpa.JPQLQuery;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.services.StringID;
import com.premiumminds.billy.core.services.entities.Business;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.france.persistence.dao.DAOFRCreditReceipt;
import com.premiumminds.billy.france.persistence.entities.FRCreditReceiptEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.JPAFRCreditReceiptEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRBusinessEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditReceiptEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditReceiptEntryEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRGenericInvoiceEntity;
import com.premiumminds.billy.france.services.entities.FRCreditReceipt;

public class DAOFRCreditReceiptImpl extends AbstractDAOFRGenericInvoiceImpl<FRCreditReceiptEntity, JPAFRCreditReceiptEntity> implements DAOFRCreditReceipt {
  @Inject public DAOFRCreditReceiptImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public FRCreditReceiptEntity getEntityInstance() {
    return new JPAFRCreditReceiptEntity();
  }

  @Override protected Class<JPAFRCreditReceiptEntity> getEntityClass() {
    return JPAFRCreditReceiptEntity.class;
  }

  @Override public List<FRCreditReceipt> findByReferencedDocument(StringID<Business> uidCompany, StringID<GenericInvoice> uidInvoice) {
    QJPAFRCreditReceiptEntity creditReceipt = QJPAFRCreditReceiptEntity.jPAFRCreditReceiptEntity;
    QJPAFRCreditReceiptEntryEntity entry = QJPAFRCreditReceiptEntryEntity.jPAFRCreditReceiptEntryEntity;
    QJPAFRGenericInvoiceEntity receipt = QJPAFRGenericInvoiceEntity.jPAFRGenericInvoiceEntity;
    final JPQLQuery<String> invQ = JPAExpressions.select(receipt.uid).from(receipt).where(receipt.uid.eq(uidInvoice.getIdentifier()));
    final JPQLQuery<String> entQ = JPAExpressions.select(entry.uid).from(entry).where(this.toDSL(entry.receiptReference, QJPAFRGenericInvoiceEntity.class).uid.in(invQ));
    return new ArrayList<>(this.createQuery().from(creditReceipt).where(this.toDSL(creditReceipt.business, QJPAFRBusinessEntity.class).uid.eq(uidCompany.getIdentifier()).and(this.toDSL(creditReceipt.entries.any(), QJPAFRCreditReceiptEntryEntity.class).uid.in(entQ))).select(creditReceipt).fetch());
  }
}