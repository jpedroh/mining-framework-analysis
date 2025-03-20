package com.premiumminds.billy.portugal.test.util;
import com.google.inject.Injector;
import com.premiumminds.billy.portugal.persistence.entities.PTSupplierEntity;
import com.premiumminds.billy.portugal.services.entities.PTAddress;
import com.premiumminds.billy.portugal.services.entities.PTContact;
import com.premiumminds.billy.portugal.services.entities.PTSupplier;

public class PTSupplierTestUtil {
  private static final Boolean SELF_BILLING = false;

  private static final String NUMBER = "123456789";

  private static final String NAME = "Supplier";

  protected static final String PT_COUNTRY_CODE = "PT";

  private Injector injector;

  private PTAddressTestUtil address;

  private PTContactTestUtil contact;

  public PTSupplierTestUtil(Injector injector) {
    this.injector = injector;
    this.address = new PTAddressTestUtil(injector);
    this.contact = new PTContactTestUtil(injector);
  }

  public PTSupplierEntity getSupplierEntity() {
    PTAddress.Builder addressBuilder = this.address.getAddressBuilder();
    PTContact.Builder contactBuilder = this.contact.getContactBuilder();
    return this.getSupplierEntity(PTSupplierTestUtil.NAME, PTSupplierTestUtil.NUMBER, PTSupplierTestUtil.SELF_BILLING, addressBuilder, contactBuilder);
  }

  public PTSupplierEntity getSupplierEntity(String name, String taxNumber, boolean selfBillingAgree, PTAddress.Builder addressBuilder, PTContact.Builder contactBuilder) {
    return (PTSupplierEntity) this.getSupplierBuilder(name, taxNumber, selfBillingAgree, addressBuilder, contactBuilder).build();
  }

  public PTSupplier.Builder getSupplierBuilder(String name, String taxNumber, boolean selfBillingAgree, PTAddress.Builder addressBuilder, PTContact.Builder contactBuilder) {
    PTSupplier.Builder supplierBuilder = this.injector.getInstance(PTSupplier.Builder.class);
    supplierBuilder.addAddress(addressBuilder).addContact(contactBuilder).setBillingAddress(addressBuilder).setMainContact(contactBuilder).setSelfBillingAgreement(selfBillingAgree).setTaxRegistrationNumber(taxNumber, PTSupplierTestUtil.PT_COUNTRY_CODE).setName(name).setMainAddress(addressBuilder);
    return supplierBuilder;
  }
}