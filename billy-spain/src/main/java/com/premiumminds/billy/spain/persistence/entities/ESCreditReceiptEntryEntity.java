package com.premiumminds.billy.spain.persistence.entities;
import com.premiumminds.billy.spain.services.entities.ESCreditReceiptEntry;
import com.premiumminds.billy.spain.services.entities.ESReceipt;

public interface ESCreditReceiptEntryEntity extends ESGenericInvoiceEntryEntity, ESCreditReceiptEntry {
  public void setReference(ESReceipt reference);

  public void setReason(String reason);
}