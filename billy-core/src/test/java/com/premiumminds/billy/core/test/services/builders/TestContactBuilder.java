package com.premiumminds.billy.core.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.persistence.dao.DAOContact;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.core.test.fixtures.MockContactEntity;

public class TestContactBuilder extends AbstractTest {
  private static final String CONTACT_YML = AbstractTest.YML_CONFIGS_DIR + "Contact.yml";

  @Test public void doTest() {
    MockContactEntity mockContact = this.createMockEntity(MockContactEntity.class, TestContactBuilder.CONTACT_YML);
    Mockito.when(this.getInstance(DAOContact.class).getEntityInstance()).thenReturn(new MockContactEntity());
    Contact.Builder builder = this.getInstance(Contact.Builder.class);
    builder.setEmail(mockContact.getEmail()).setFax(mockContact.getFax()).setMobile(mockContact.getMobile()).setName(mockContact.getName()).setTelephone(mockContact.getTelephone()).setWebsite(mockContact.getWebsite());
    Contact contact = builder.build();
    assert (contact != null);
    Assert.assertEquals(mockContact.getName(), contact.getName());
    Assert.assertEquals(mockContact.getTelephone(), contact.getTelephone());
    Assert.assertEquals(mockContact.getMobile(), contact.getMobile());
    Assert.assertEquals(mockContact.getFax(), contact.getFax());
    Assert.assertEquals(mockContact.getEmail(), contact.getEmail());
    Assert.assertEquals(mockContact.getWebsite(), contact.getWebsite());
  }
}