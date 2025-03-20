package com.premiumminds.billy.portugal.persistence.entities.jpa;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTCreditNoteEntryEntity;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CREDIT_NOTE_ENTRY") public class JPAPTCreditNoteEntryEntity extends JPAPTGenericInvoiceEntryEntity implements PTCreditNoteEntryEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  @OneToOne(fetch = FetchType.EAGER, targetEntity = JPAPTInvoiceEntity.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) @JoinColumn(name = "ID_PTINVOICE", referencedColumnName = "ID") protected PTInvoice reference;

  @Column(name = "REASON") protected String reason;

  @Override public String getReason() {
    return this.reason;
  }

  @Override public PTInvoice getReference() {
    return this.reference;
  }

  @Override public void setReference(PTInvoice reference) {
    this.reference = reference;
  }

  @Override public void setReason(String reason) {
    this.reason = reason;
  }
}