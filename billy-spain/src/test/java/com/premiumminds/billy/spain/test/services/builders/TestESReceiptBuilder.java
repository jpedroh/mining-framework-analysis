package com.premiumminds.billy.spain.test.services.builders;
import java.util.Currency;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESCustomer;
import com.premiumminds.billy.spain.persistence.dao.DAOESPayment;
import com.premiumminds.billy.spain.persistence.dao.DAOESReceipt;
import com.premiumminds.billy.spain.persistence.dao.DAOESReceiptEntry;
import com.premiumminds.billy.spain.services.entities.ESPayment;
import com.premiumminds.billy.spain.services.entities.ESReceipt;
import com.premiumminds.billy.spain.services.entities.ESReceiptEntry;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESCustomerEntity;
import com.premiumminds.billy.spain.test.fixtures.MockESPaymentEntity;
import com.premiumminds.billy.spain.test.fixtures.MockESReceiptEntity;
import com.premiumminds.billy.spain.test.fixtures.MockESReceiptEntryEntity;

public class TestESReceiptBuilder extends ESAbstractTest {
  private static final String ES_RECEIPT_YML = AbstractTest.YML_CONFIGS_DIR + "ESInvoice.yml";

  private static final String ES_RECEIPT_ENTRY_YML = AbstractTest.YML_CONFIGS_DIR + "ESInvoiceEntry.yml";

  private static final String ES_CUSTOMER_YML = AbstractTest.YML_CONFIGS_DIR + "ESCustomer.yml";

  private static final String ES_PAYMENT_YML = AbstractTest.YML_CONFIGS_DIR + "ESPayment.yml";

  @Test public void doTest() {
    MockESReceiptEntity mock = this.createMockEntity(MockESReceiptEntity.class, TestESReceiptBuilder.ES_RECEIPT_YML);
    mock.setCurrency(Currency.getInstance("EUR"));
    MockESCustomerEntity mockCustomer = this.createMockEntity(MockESCustomerEntity.class, TestESReceiptBuilder.ES_CUSTOMER_YML);
    Mockito.when(this.getInstance(DAOESCustomer.class).get(Matchers.any(UID.class))).thenReturn(mockCustomer);
    Mockito.when(this.getInstance(DAOESReceipt.class).getEntityInstance()).thenReturn(new MockESReceiptEntity());
    MockESReceiptEntryEntity mockEntry = this.createMockEntity(MockESReceiptEntryEntity.class, TestESReceiptBuilder.ES_RECEIPT_ENTRY_YML);
    Mockito.when(this.getInstance(DAOESReceiptEntry.class).get(Matchers.any(UID.class))).thenReturn(mockEntry);
    @SuppressWarnings(value = { "unchecked" }) List<ESReceiptEntry> entries = (List<ESReceiptEntry>) (List<?>) mock.getEntries();
    entries.add(mockEntry);
    ESReceipt.Builder builder = this.getInstance(ESReceipt.Builder.class);
    ESReceiptEntry.Builder entry = this.getMock(ESReceiptEntry.Builder.class);
    MockESPaymentEntity mockPayment = this.createMockEntity(MockESPaymentEntity.class, TestESReceiptBuilder.ES_PAYMENT_YML);
    Mockito.when(this.getInstance(DAOESPayment.class).getEntityInstance()).thenReturn(new MockESPaymentEntity());
    ESPayment.Builder builderPayment = this.getInstance(ESPayment.Builder.class);
    builderPayment.setPaymentAmount(mockPayment.getPaymentAmount()).setPaymentDate(mockPayment.getPaymentDate()).setPaymentMethod(mockPayment.getPaymentMethod());
    Mockito.when(entry.build()).thenReturn(entries.get(0));
    builder.addEntry(entry).setBilled(mock.isBilled()).setCancelled(mock.isCancelled()).setBatchId(mock.getBatchId()).setDate(mock.getDate()).setGeneralLedgerDate(mock.getGeneralLedgerDate()).setOfficeNumber(mock.getOfficeNumber()).setPaymentTerms(mock.getPaymentTerms()).setSelfBilled(mock.selfBilled).setSettlementDate(mock.getSettlementDate()).setSettlementDescription(mock.getSettlementDescription()).setSettlementDiscount(mock.getSettlementDiscount()).setSourceId(mock.getSourceId()).setTransactionId(mock.getTransactionId()).addPayment(builderPayment);
    ESReceipt receipt = builder.build();
    Assert.assertTrue(receipt != null);
    Assert.assertTrue(receipt.getEntries() != null);
    Assert.assertEquals(receipt.getEntries().size(), mock.getEntries().size());
    Assert.assertTrue(receipt.isBilled() == mock.isBilled());
    Assert.assertTrue(receipt.isCancelled() == mock.isCancelled());
    Assert.assertEquals(mock.getGeneralLedgerDate(), receipt.getGeneralLedgerDate());
    Assert.assertEquals(mock.getBatchId(), receipt.getBatchId());
    Assert.assertEquals(mock.getDate(), receipt.getDate());
    Assert.assertEquals(mock.getPaymentTerms(), receipt.getPaymentTerms());
    Assert.assertTrue(mock.getAmountWithoutTax().compareTo(receipt.getAmountWithoutTax()) == 0);
    Assert.assertTrue(mock.getAmountWithTax().compareTo(receipt.getAmountWithTax()) == 0);
    Assert.assertTrue(mock.getTaxAmount().compareTo(receipt.getTaxAmount()) == 0);
    builder.setCustomerUID(mockCustomer.getUID());
    receipt = builder.build();
    Assert.assertTrue(receipt.getCustomer().getUID().equals(mockCustomer.getUID()));
  }
}