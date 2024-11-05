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
import com.premiumminds.billy.france.persistence.dao.DAOFRCreditNote;
import com.premiumminds.billy.france.persistence.entities.FRCreditNoteEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.JPAFRCreditNoteEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRBusinessEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditNoteEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRCreditNoteEntryEntity;
import com.premiumminds.billy.france.persistence.entities.jpa.QJPAFRGenericInvoiceEntity;
import com.premiumminds.billy.france.services.entities.FRCreditNote;

public class DAOFRCreditNoteImpl extends AbstractDAOFRGenericInvoiceImpl<FRCreditNoteEntity, JPAFRCreditNoteEntity> implements DAOFRCreditNote {
  @Inject public DAOFRCreditNoteImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public FRCreditNoteEntity getEntityInstance() {
    return new JPAFRCreditNoteEntity();
  }

  @Override protected Class<JPAFRCreditNoteEntity> getEntityClass() {
    return JPAFRCreditNoteEntity.class;
  }

  @Override public List<FRCreditNote> findByReferencedDocument(StringID<Business> uidCompany, StringID<GenericInvoice> uidInvoice) {
    QJPAFRCreditNoteEntity creditNote = QJPAFRCreditNoteEntity.jPAFRCreditNoteEntity;
    QJPAFRCreditNoteEntryEntity entry = QJPAFRCreditNoteEntryEntity.jPAFRCreditNoteEntryEntity;
    QJPAFRGenericInvoiceEntity invoice = QJPAFRGenericInvoiceEntity.jPAFRGenericInvoiceEntity;
    final JPQLQuery<String> invQ = JPAExpressions.select(invoice.uid).from(invoice).where(invoice.uid.eq(uidInvoice.toString()));
    final JPQLQuery<String> entQ = JPAExpressions.select(entry.uid).from(entry).where(this.toDSL(entry.invoiceReference, QJPAFRGenericInvoiceEntity.class).uid.in(invQ));
    return new ArrayList<>(this.createQuery().from(creditNote).where(this.toDSL(creditNote.business, QJPAFRBusinessEntity.class).uid.eq(uidCompany.toString()).and(this.toDSL(creditNote.entries.any(), QJPAFRCreditNoteEntryEntity.class).uid.in(entQ))).select(creditNote).fetch());
  }
}