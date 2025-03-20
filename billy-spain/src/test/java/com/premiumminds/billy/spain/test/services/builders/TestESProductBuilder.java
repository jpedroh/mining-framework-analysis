package com.premiumminds.billy.spain.test.services.builders;
import java.util.Currency;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import com.premiumminds.billy.core.persistence.entities.TaxEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESProduct;
import com.premiumminds.billy.spain.persistence.dao.DAOESTax;
import com.premiumminds.billy.spain.services.entities.ESProduct;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESProductEntity;

public class TestESProductBuilder extends ESAbstractTest {
  private static final String ESPRODUCT_YML = AbstractTest.YML_CONFIGS_DIR + "ESProduct.yml";

  @Test public void doTest() {
    MockESProductEntity mockProduct = this.createMockEntity(MockESProductEntity.class, TestESProductBuilder.ESPRODUCT_YML);
    Mockito.when(this.getInstance(DAOESProduct.class).getEntityInstance()).thenReturn(new MockESProductEntity());
    for (TaxEntity tax : mockProduct.getTaxes()) {
      tax.setCurrency(Currency.getInstance("EUR"));
      Mockito.when(this.getInstance(DAOESTax.class).get(Matchers.any(UID.class))).thenReturn(tax);
    }
    ESProduct.Builder builder = this.getInstance(ESProduct.Builder.class);
    builder.addTaxUID(mockProduct.getTaxes().get(0).getUID()).setCommodityCode(mockProduct.getCommodityCode()).setDescription(mockProduct.getDescription()).setNumberCode(mockProduct.getNumberCode()).setProductCode(mockProduct.getProductCode()).setProductGroup(mockProduct.getProductGroup()).setType(mockProduct.getType()).setUnitOfMeasure(mockProduct.getUnitOfMeasure()).setValuationMethod(mockProduct.getValuationMethod());
    ESProduct product = builder.build();
    Assert.assertTrue(product != null);
    Assert.assertEquals(mockProduct.getCommodityCode(), product.getCommodityCode());
    Assert.assertEquals(mockProduct.getDescription(), product.getDescription());
    Assert.assertEquals(mockProduct.getNumberCode(), product.getNumberCode());
    Assert.assertEquals(mockProduct.getProductCode(), product.getProductCode());
    Assert.assertEquals(mockProduct.getProductGroup(), product.getProductGroup());
    Assert.assertEquals(mockProduct.getUnitOfMeasure(), product.getUnitOfMeasure());
    Assert.assertEquals(mockProduct.getValuationMethod(), product.getValuationMethod());
  }
}