package com.premiumminds.billy.portugal.test.services.persistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.premiumminds.billy.core.exceptions.BillyUpdateException;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;

public class TestInvoiceUpdate extends PTPersistenceServiceAbstractTest {
  private PTInvoice issuedInvoice;

  @Before public void setUp() throws DocumentIssuingException {
    this.issuedInvoice = this.getNewIssuedInvoice();
  }

  @Test public void testSimpleUpdate() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    PTInvoice peristedInvoice = this.billy.invoices().persistence().get(this.issuedInvoice.getUID());
    Assert.assertEquals(false, peristedInvoice.isCancelled());
    builder.setCancelled(true);
    this.billy.invoices().persistence().update(builder);
    peristedInvoice = this.billy.invoices().persistence().get(this.issuedInvoice.getUID());
    Assert.assertEquals(true, peristedInvoice.isCancelled());
  }

  @Test(expected = BillyUpdateException.class) public void testBilledUpdate() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    PTInvoice peristedInvoice = this.billy.invoices().persistence().get(this.issuedInvoice.getUID());
    Assert.assertEquals(false, peristedInvoice.isBilled());
    builder = this.billy.invoices().builder(peristedInvoice);
    builder.setBilled(true);
    builder.setBilled(false);
  }

  @Test(expected = BillyUpdateException.class) public void testBusinessFailure() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    builder.setBusinessUID(new UID());
  }

  @Test(expected = BillyUpdateException.class) public void testCustomerFailure() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    builder.setCustomerUID(new UID());
  }

  @Test(expected = BillyUpdateException.class) public void testSourceBillingFailure() {
    PTInvoice.Builder builder = this.billy.invoices().builder(this.issuedInvoice);
    builder.setSourceBilling(SourceBilling.M);
  }
}