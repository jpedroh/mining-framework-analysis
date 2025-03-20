package com.premiumminds.billy.spain.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESContact;
import com.premiumminds.billy.spain.services.entities.ESContact;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESContactEntity;

public class TestESContactBuilder extends ESAbstractTest {
  private static final String ESCONTACT_YML = AbstractTest.YML_CONFIGS_DIR + "ESContact.yml";

  @Test public void doTest() {
    MockESContactEntity mockContact = this.createMockEntity(MockESContactEntity.class, TestESContactBuilder.ESCONTACT_YML);
    Mockito.when(this.getInstance(DAOESContact.class).getEntityInstance()).thenReturn(new MockESContactEntity());
    ESContact.Builder builder = this.getInstance(ESContact.Builder.class);
    builder.setEmail(mockContact.getEmail()).setFax(mockContact.getFax()).setMobile(mockContact.getMobile()).setName(mockContact.getName()).setTelephone(mockContact.getTelephone()).setWebsite(mockContact.getWebsite());
    ESContact contact = builder.build();
    assert (contact != null);
    Assert.assertEquals(mockContact.getEmail(), contact.getEmail());
    Assert.assertEquals(mockContact.getFax(), contact.getFax());
    Assert.assertEquals(mockContact.getMobile(), contact.getMobile());
    Assert.assertEquals(mockContact.getName(), contact.getName());
    Assert.assertEquals(mockContact.getTelephone(), contact.getTelephone());
    Assert.assertEquals(mockContact.getWebsite(), contact.getWebsite());
  }
}