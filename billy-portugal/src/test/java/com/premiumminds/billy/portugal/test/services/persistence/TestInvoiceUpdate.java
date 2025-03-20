package com.premiumminds.billy.portugal.test.services.persistence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.premiumminds.billy.core.exceptions.BillyUpdateException;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;

public class TestInvoiceUpdate extends PTPersistenceServiceAbstractTest {
  private PTInvoice issuedInvoice;

  @BeforeEach public void setUp() throws DocumentIssuingException {
    final String uid = new UID().toString();
    this.createSeries(uid);
    this.issuedInvoice = this.getNewIssuedInvoice(uid);
  }

  @Test public void testSimpleUpdate() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    PTInvoice peristedInvoice = this.billy.invoices().persistence().get(this.issuedInvoice.getUID());
    Assertions.assertEquals(false, peristedInvoice.isCancelled());
    builder.setCancelled(true);
    this.billy.invoices().persistence().update(builder);
    peristedInvoice = this.billy.invoices().persistence().get(this.issuedInvoice.getUID());
    Assertions.assertEquals(true, peristedInvoice.isCancelled());
  }

  @Test public void testBilledUpdate() {
    PTInvoice peristedInvoice = this.billy.invoices().persistence().get(this.issuedInvoice.getUID());
    Assertions.assertEquals(false, peristedInvoice.isBilled());
    PTInvoice.Builder builder = this.billy.invoices().builder(peristedInvoice);
    builder.setBilled(true);
    Assertions.assertThrows(BillyUpdateException.class, () -> builder.setBilled(false));
  }

  @Test public void testBusinessFailure() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    Assertions.assertThrows(BillyUpdateException.class, () -> builder.setBusinessUID(new UID()));
  }

  @Test public void testCustomerFailure() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    Assertions.assertThrows(BillyUpdateException.class, () -> builder.setCustomerUID(new UID()));
  }

  @Test public void testSourceBillingFailure() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    Assertions.assertThrows(BillyUpdateException.class, () -> builder.setSourceBilling(SourceBilling.M));
  }
}