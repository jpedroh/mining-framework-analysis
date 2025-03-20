package com.premiumminds.billy.spain.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import com.premiumminds.billy.core.persistence.entities.ContextEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESRegionContext;
import com.premiumminds.billy.spain.services.entities.ESRegionContext;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESRegionContextEntity;

public class TestESRegionContextBuilder extends ESAbstractTest {
  private static final String ESCONTEXT_YML = AbstractTest.YML_CONFIGS_DIR + "ESContext.yml";

  @Test public void testRegionCode() {
    MockESRegionContextEntity mockRegionContextEntity = this.createMockEntity(MockESRegionContextEntity.class, TestESRegionContextBuilder.ESCONTEXT_YML);
    Mockito.when(this.getInstance(DAOESRegionContext.class).getEntityInstance()).thenReturn(new MockESRegionContextEntity());
    Mockito.when(this.getInstance(DAOESRegionContext.class).get(Matchers.any(UID.class))).thenReturn((ContextEntity) mockRegionContextEntity.getParentContext());
    ESRegionContext.Builder builder = this.getInstance(ESRegionContext.Builder.class);
    builder.setDescription(mockRegionContextEntity.getDescription()).setName(mockRegionContextEntity.getName()).setParentContextUID(mockRegionContextEntity.getParentContext().getUID());
    ESRegionContext regionContex = builder.build();
    Assert.assertTrue(regionContex != null);
    Assert.assertTrue(regionContex.getParentContext() != null);
    Assert.assertEquals(regionContex.getDescription(), mockRegionContextEntity.getDescription());
    Assert.assertEquals(regionContex.getName(), mockRegionContextEntity.getName());
    Assert.assertEquals(regionContex.getParentContext().getUID(), mockRegionContextEntity.getParentContext().getUID());
  }
}