package com.premiumminds.billy.spain.persistence.entities.jpa;
import com.premiumminds.billy.core.persistence.entities.GenericInvoiceEntity;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESCreditReceiptEntryEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CREDIT_RECEIPT_ENTRY") public class JPAESCreditReceiptEntryEntity extends JPAESGenericInvoiceEntryEntity implements ESCreditReceiptEntryEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  @OneToOne(fetch = FetchType.EAGER, targetEntity = JPAESReceiptEntity.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }) @JoinColumn(name = "ID_ESRECEIPT", referencedColumnName = "ID") protected GenericInvoiceEntity receiptReference;

  @Column(name = "REASON") protected String reason;

  @Override public String getReason() {
    return this.reason;
  }

  @Override public GenericInvoiceEntity getReference() {
    return this.receiptReference;
  }

  @Override public void setReference(GenericInvoiceEntity reference) {
    this.receiptReference = reference;
  }

  @Override public void setReason(String reason) {
    this.reason = reason;
  }
}