package com.premiumminds.billy.spain.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESShippingPoint;
import com.premiumminds.billy.spain.persistence.entities.ESAddressEntity;
import com.premiumminds.billy.spain.services.entities.ESAddress;
import com.premiumminds.billy.spain.services.entities.ESShippingPoint;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESShippingPointEntity;

public class TestESShippingPointBuilder extends ESAbstractTest {
  private static final String ESSHIPPINGPOINT_YML = AbstractTest.YML_CONFIGS_DIR + "ESShippingPoint.yml";

  @Test public void doTest() {
    MockESShippingPointEntity mockShippingPoint = this.createMockEntity(MockESShippingPointEntity.class, TestESShippingPointBuilder.ESSHIPPINGPOINT_YML);
    Mockito.when(this.getInstance(DAOESShippingPoint.class).getEntityInstance()).thenReturn(new MockESShippingPointEntity());
    ESShippingPoint.Builder builder = this.getInstance(ESShippingPoint.Builder.class);
    ESAddress.Builder mockAddressBuilder = this.getMock(ESAddress.Builder.class);
    Mockito.when(mockAddressBuilder.build()).thenReturn((ESAddressEntity) mockShippingPoint.getAddress());
    builder.setAddress(mockAddressBuilder).setDate(mockShippingPoint.getDate()).setDeliveryId(mockShippingPoint.getDeliveryId()).setLocationId(mockShippingPoint.getLocationId()).setWarehouseId(mockShippingPoint.getWarehouseId());
    ESShippingPoint shippingPoint = builder.build();
    Assert.assertTrue(shippingPoint != null);
    Assert.assertEquals(mockShippingPoint.getDeliveryId(), shippingPoint.getDeliveryId());
    Assert.assertEquals(mockShippingPoint.getLocationId(), shippingPoint.getLocationId());
    Assert.assertEquals(mockShippingPoint.getWarehouseId(), shippingPoint.getWarehouseId());
    Assert.assertEquals(mockShippingPoint.getDate(), shippingPoint.getDate());
    Assert.assertEquals(mockShippingPoint.getAddress(), shippingPoint.getAddress());
  }
}