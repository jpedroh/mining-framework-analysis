package com.premiumminds.billy.portugal.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTShippingPoint;
import com.premiumminds.billy.portugal.persistence.entities.PTAddressEntity;
import com.premiumminds.billy.portugal.services.entities.PTAddress;
import com.premiumminds.billy.portugal.services.entities.PTShippingPoint;
import com.premiumminds.billy.portugal.test.PTAbstractTest;
import com.premiumminds.billy.portugal.test.fixtures.MockPTShippingPointEntity;

public class TestPTShippingPointBuilder extends PTAbstractTest {
  private static final String PTSHIPPINGPOINT_YML = AbstractTest.YML_CONFIGS_DIR + "PTShippingPoint.yml";

  @Test public void doTest() {
    MockPTShippingPointEntity mockShippingPoint = this.createMockEntity(MockPTShippingPointEntity.class, TestPTShippingPointBuilder.PTSHIPPINGPOINT_YML);
    Mockito.when(this.getInstance(DAOPTShippingPoint.class).getEntityInstance()).thenReturn(new MockPTShippingPointEntity());
    PTShippingPoint.Builder builder = this.getInstance(PTShippingPoint.Builder.class);
    PTAddress.Builder mockAddressBuilder = this.getMock(PTAddress.Builder.class);
    Mockito.when(mockAddressBuilder.build()).thenReturn((PTAddressEntity) mockShippingPoint.getAddress());
    builder.setAddress(mockAddressBuilder).setDate(mockShippingPoint.getDate()).setDeliveryId(mockShippingPoint.getDeliveryId()).setLocationId(mockShippingPoint.getLocationId()).setWarehouseId(mockShippingPoint.getWarehouseId());
    PTShippingPoint shippingPoint = builder.build();
    Assert.assertTrue(shippingPoint != null);
    Assert.assertEquals(mockShippingPoint.getDeliveryId(), shippingPoint.getDeliveryId());
    Assert.assertEquals(mockShippingPoint.getLocationId(), shippingPoint.getLocationId());
    Assert.assertEquals(mockShippingPoint.getWarehouseId(), shippingPoint.getWarehouseId());
    Assert.assertEquals(mockShippingPoint.getDate(), shippingPoint.getDate());
    Assert.assertEquals(mockShippingPoint.getAddress(), shippingPoint.getAddress());
  }
}