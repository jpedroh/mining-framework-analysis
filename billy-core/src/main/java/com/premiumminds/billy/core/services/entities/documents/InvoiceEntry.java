package com.premiumminds.billy.core.services.entities.documents;
import java.util.Collection;

public interface InvoiceEntry extends GenericInvoiceEntry {
  @Override public Collection<CreditNote> getDocumentReferences();
}