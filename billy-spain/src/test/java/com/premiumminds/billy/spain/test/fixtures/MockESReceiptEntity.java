package com.premiumminds.billy.spain.test.fixtures;
import java.util.List;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntity;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESPayment;

public class MockESReceiptEntity extends MockESGenericInvoiceEntity implements ESReceiptEntity {
  private static final long serialVersionUID = 1L;

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESGenericInvoiceEntry> getEntries() {
    return (List<ESGenericInvoiceEntry>) (List<?>) super.getEntries();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESPayment> getPayments() {
    return super.getPayments();
  }
}