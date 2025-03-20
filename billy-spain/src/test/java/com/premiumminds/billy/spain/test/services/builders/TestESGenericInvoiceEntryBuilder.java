package com.premiumminds.billy.spain.test.services.builders;
import java.util.Currency;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESGenericInvoice;
import com.premiumminds.billy.spain.persistence.dao.DAOESGenericInvoiceEntry;
import com.premiumminds.billy.spain.persistence.dao.DAOESProduct;
import com.premiumminds.billy.spain.persistence.dao.DAOESRegionContext;
import com.premiumminds.billy.spain.persistence.entities.ESProductEntity;
import com.premiumminds.billy.spain.services.entities.ESGenericInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESRegionContext;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESGenericInvoiceEntity;
import com.premiumminds.billy.spain.test.fixtures.MockESGenericInvoiceEntryEntity;

public class TestESGenericInvoiceEntryBuilder extends ESAbstractTest {
  private static final String ES_GENERIC_INVOICE_ENTRY_YML = AbstractTest.YML_CONFIGS_DIR + "ESGenericInvoiceEntry.yml";

  private static final String ES_GENERIC_INVOICE_YML = AbstractTest.YML_CONFIGS_DIR + "ESGenericInvoice.yml";

  @Test public void doTest() {
    MockESGenericInvoiceEntryEntity mock = this.createMockEntity(MockESGenericInvoiceEntryEntity.class, TestESGenericInvoiceEntryBuilder.ES_GENERIC_INVOICE_ENTRY_YML);
    mock.currency = Currency.getInstance("EUR");
    Mockito.when(this.getInstance(DAOESGenericInvoiceEntry.class).getEntityInstance()).thenReturn(new MockESGenericInvoiceEntryEntity());
    MockESGenericInvoiceEntity mockInvoice = this.createMockEntity(MockESGenericInvoiceEntity.class, TestESGenericInvoiceEntryBuilder.ES_GENERIC_INVOICE_YML);
    Mockito.when(this.getInstance(DAOESGenericInvoice.class).get(Matchers.any(UID.class))).thenReturn(mockInvoice);
    Mockito.when(this.getInstance(DAOESProduct.class).get(Matchers.any(UID.class))).thenReturn((ESProductEntity) mock.getProduct());
    Mockito.when(this.getInstance(DAOESRegionContext.class).isSubContext(Matchers.any(ESRegionContext.class), Matchers.any(ESRegionContext.class))).thenReturn(true);
    mock.getDocumentReferences().add(mockInvoice);
    ESGenericInvoiceEntry.Builder builder = this.getInstance(ESGenericInvoiceEntry.Builder.class);
    builder.setDescription(mock.getDescription()).addDocumentReferenceUID(mock.getDocumentReferences().get(0).getUID()).setQuantity(mock.getQuantity()).setShippingCostsAmount(mock.getShippingCostsAmount()).setUnitAmount(AmountType.WITH_TAX, mock.getUnitAmountWithTax()).setUnitOfMeasure(mock.getUnitOfMeasure()).setProductUID(mock.getProduct().getUID()).setTaxPointDate(mock.getTaxPointDate()).setCurrency(Currency.getInstance("EUR"));
    ESGenericInvoiceEntry entry = builder.build();
    if (entry.getAmountType().compareTo(AmountType.WITHOUT_TAX) == 0) {
      Assert.assertTrue(mock.getUnitAmountWithoutTax().compareTo(entry.getUnitAmountWithoutTax()) == 0);
    } else {
      Assert.assertTrue(mock.getUnitAmountWithTax().compareTo(entry.getUnitAmountWithTax()) == 0);
    }
    Assert.assertTrue(mock.getUnitDiscountAmount().compareTo(entry.getUnitDiscountAmount()) == 0);
    Assert.assertTrue(mock.getUnitTaxAmount().compareTo(entry.getUnitTaxAmount()) == 0);
    Assert.assertTrue(mock.getAmountWithTax().compareTo(entry.getAmountWithTax()) == 0);
    Assert.assertTrue(mock.getAmountWithoutTax().compareTo(entry.getAmountWithoutTax()) == 0);
    Assert.assertTrue(mock.getTaxAmount().compareTo(entry.getTaxAmount()) == 0);
    Assert.assertTrue(mock.getDiscountAmount().compareTo(entry.getDiscountAmount()) == 0);
  }
}