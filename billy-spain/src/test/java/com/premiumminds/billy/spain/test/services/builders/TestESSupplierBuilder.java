package com.premiumminds.billy.spain.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.spain.persistence.dao.DAOESSupplier;
import com.premiumminds.billy.spain.persistence.entities.ESAddressEntity;
import com.premiumminds.billy.spain.persistence.entities.ESContactEntity;
import com.premiumminds.billy.spain.services.entities.ESAddress;
import com.premiumminds.billy.spain.services.entities.ESContact;
import com.premiumminds.billy.spain.services.entities.ESSupplier;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.fixtures.MockESSupplierEntity;

public class TestESSupplierBuilder extends ESAbstractTest {
  private static final String ESSUPPLIER_YML = AbstractTest.YML_CONFIGS_DIR + "ESSupplier.yml";

  @Test public void doTest() {
    MockESSupplierEntity mockSupplier = this.createMockEntity(MockESSupplierEntity.class, TestESSupplierBuilder.ESSUPPLIER_YML);
    Mockito.when(this.getInstance(DAOESSupplier.class).getEntityInstance()).thenReturn(new MockESSupplierEntity());
    ESAddress.Builder mockMainAddressBuilder = this.getMock(ESAddress.Builder.class);
    Mockito.when(mockMainAddressBuilder.build()).thenReturn((ESAddressEntity) mockSupplier.getMainAddress());
    ESAddress.Builder mockBillingAddressBuilder = this.getMock(ESAddress.Builder.class);
    Mockito.when(mockBillingAddressBuilder.build()).thenReturn((ESAddressEntity) mockSupplier.getBillingAddress());
    ESAddress.Builder mockShippingAddressBuilder = this.getMock(ESAddress.Builder.class);
    Mockito.when(mockShippingAddressBuilder.build()).thenReturn((ESAddressEntity) mockSupplier.getShippingAddress());
    ESContact.Builder mockMainContactBuilder = this.getMock(ESContact.Builder.class);
    Mockito.when(mockMainContactBuilder.build()).thenReturn((ESContactEntity) mockSupplier.getMainContact());
    ESContact.Builder mockContactBuilder1 = this.getMock(ESContact.Builder.class);
    Mockito.when(mockContactBuilder1.build()).thenReturn((ESContactEntity) mockSupplier.getContacts().get(0));
    ESContact.Builder mockContactBuilder2 = this.getMock(ESContact.Builder.class);
    Mockito.when(mockContactBuilder2.build()).thenReturn((ESContactEntity) mockSupplier.getContacts().get(1));
    ESSupplier.Builder builder = this.getInstance(ESSupplier.Builder.class);
    builder.addAddress(mockMainAddressBuilder).addAddress(mockShippingAddressBuilder).addAddress(mockBillingAddressBuilder).addContact(mockMainContactBuilder).setBillingAddress(mockBillingAddressBuilder).setMainAddress(mockMainAddressBuilder).setMainContact(mockMainContactBuilder).setName(mockSupplier.getName()).setSelfBillingAgreement(mockSupplier.hasSelfBillingAgreement()).setTaxRegistrationNumber(mockSupplier.getTaxRegistrationNumber(), ESAbstractTest.ES_COUNTRY_CODE).setShippingAddress(mockShippingAddressBuilder);
    ESSupplier supplier = builder.build();
    Assert.assertTrue(supplier != null);
    Assert.assertEquals(mockSupplier.getName(), supplier.getName());
    Assert.assertEquals(mockSupplier.getTaxRegistrationNumber(), supplier.getTaxRegistrationNumber());
    Assert.assertEquals(mockSupplier.getMainAddress(), supplier.getMainAddress());
    Assert.assertEquals(mockSupplier.getBankAccounts().size(), mockSupplier.getBankAccounts().size());
  }
}