package com.premiumminds.billy.spain.persistence.entities;
import java.util.List;
import com.premiumminds.billy.spain.services.entities.ESCreditReceipt;
import com.premiumminds.billy.spain.services.entities.ESCreditReceiptEntry;
import com.premiumminds.billy.spain.services.entities.ESPayment;

public interface ESCreditReceiptEntity extends ESGenericInvoiceEntity, ESCreditReceipt {
  @Override public List<ESCreditReceiptEntry> getEntries();

  @Override public List<ESPayment> getPayments();
}