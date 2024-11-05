package com.premiumminds.billy.spain.persistence.dao.jpa;
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
import com.premiumminds.billy.spain.persistence.dao.DAOESCreditNote;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESBusinessEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditNoteEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESCreditNoteEntryEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.QJPAESGenericInvoiceEntity;
import com.premiumminds.billy.spain.services.entities.ESCreditNote;

public class DAOESCreditNoteImpl extends AbstractDAOESGenericInvoiceImpl<ESCreditNoteEntity, JPAESCreditNoteEntity> implements DAOESCreditNote {
  @Inject public DAOESCreditNoteImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESCreditNoteEntity getEntityInstance() {
    return new JPAESCreditNoteEntity();
  }

  @Override protected Class<JPAESCreditNoteEntity> getEntityClass() {
    return JPAESCreditNoteEntity.class;
  }

  @Override public List<ESCreditNote> findByReferencedDocument(StringID<Business> uidCompany, StringID<GenericInvoice> uidInvoice) {
    QJPAESCreditNoteEntity creditNote = QJPAESCreditNoteEntity.jPAESCreditNoteEntity;
    QJPAESCreditNoteEntryEntity entry = QJPAESCreditNoteEntryEntity.jPAESCreditNoteEntryEntity;
    QJPAESGenericInvoiceEntity invoice = QJPAESGenericInvoiceEntity.jPAESGenericInvoiceEntity;
    final JPQLQuery<String> invQ = JPAExpressions.select(invoice.uid).from(invoice).where(invoice.uid.eq(uidInvoice.toString()));
    final JPQLQuery<String> entQ = JPAExpressions.select(entry.uid).from(entry).where(this.toDSL(entry.invoiceReference, QJPAESGenericInvoiceEntity.class).uid.in(invQ));
    return new ArrayList<>(this.createQuery().from(creditNote).where(this.toDSL(creditNote.business, QJPAESBusinessEntity.class).uid.eq(uidCompany.toString()).and(this.toDSL(creditNote.entries.any(), QJPAESCreditNoteEntryEntity.class).uid.in(entQ))).select(creditNote).fetch());
  }
}