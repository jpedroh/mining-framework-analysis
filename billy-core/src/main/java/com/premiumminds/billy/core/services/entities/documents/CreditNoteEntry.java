package com.premiumminds.billy.core.services.entities.documents;
import java.util.Collection;

public interface CreditNoteEntry extends GenericInvoiceEntry {
  @Override public Collection<? extends GenericInvoice> getDocumentReferences();
}