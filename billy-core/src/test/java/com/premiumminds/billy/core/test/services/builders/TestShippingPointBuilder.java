package com.premiumminds.billy.core.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.persistence.dao.DAOShippingPoint;
import com.premiumminds.billy.core.services.entities.Address;
import com.premiumminds.billy.core.services.entities.ShippingPoint;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.core.test.fixtures.MockShippingPointEntity;

public class TestShippingPointBuilder extends AbstractTest {
  private static final String SHIPPINGPOINT_YML = AbstractTest.YML_CONFIGS_DIR + "ShippingPoint.yml";

  @Test public void doTest() {
    MockShippingPointEntity mockShippingPoint = this.createMockEntity(MockShippingPointEntity.class, TestShippingPointBuilder.SHIPPINGPOINT_YML);
    Mockito.when(this.getInstance(DAOShippingPoint.class).getEntityInstance()).thenReturn(new MockShippingPointEntity());
    ShippingPoint.Builder builder = this.getInstance(ShippingPoint.Builder.class);
    Address.Builder mockAddressBuilder = this.getMock(Address.Builder.class);
    Mockito.when(mockAddressBuilder.build()).thenReturn(mockShippingPoint.getAddress());
    builder.setAddress(mockAddressBuilder).setDate(mockShippingPoint.getDate()).setDeliveryId(mockShippingPoint.getDeliveryId()).setLocationId(mockShippingPoint.getLocationId()).setUCR(mockShippingPoint.getUCR()).setWarehouseId(mockShippingPoint.getWarehouseId());
    ShippingPoint shippingPoint = builder.build();
    Assert.assertTrue(shippingPoint != null);
    Assert.assertEquals(mockShippingPoint.getDeliveryId(), shippingPoint.getDeliveryId());
    Assert.assertEquals(mockShippingPoint.getLocationId(), shippingPoint.getLocationId());
    Assert.assertEquals(mockShippingPoint.getUCR(), shippingPoint.getUCR());
    Assert.assertEquals(mockShippingPoint.getWarehouseId(), shippingPoint.getWarehouseId());
    Assert.assertEquals(mockShippingPoint.getDate(), shippingPoint.getDate());
    Assert.assertEquals(mockShippingPoint.getAddress(), shippingPoint.getAddress());
  }
}