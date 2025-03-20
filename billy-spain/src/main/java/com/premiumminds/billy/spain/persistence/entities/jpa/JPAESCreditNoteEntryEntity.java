package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntryEntity;
import com.premiumminds.billy.spain.services.entities.ESInvoice;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CREDIT_NOTE_ENTRY") public class JPAESCreditNoteEntryEntity extends JPAESGenericInvoiceEntryEntity implements ESCreditNoteEntryEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  @OneToOne(fetch = FetchType.EAGER, targetEntity = JPAESInvoiceEntity.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) @JoinColumn(name = "ID_ESINVOICE", referencedColumnName = "ID") protected ESInvoice reference;

  @Column(name = "REASON") protected String reason;

  @Override public String getReason() {
    return this.reason;
  }

  @Override public ESInvoice getReference() {
    return this.reference;
  }

  @Override public void setReference(ESInvoice reference) {
    this.reference = reference;
  }

  @Override public void setReason(String reason) {
    this.reason = reason;
  }
}