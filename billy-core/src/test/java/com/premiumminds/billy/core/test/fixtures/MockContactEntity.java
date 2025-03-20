package com.premiumminds.billy.core.test.fixtures;
import com.premiumminds.billy.core.persistence.entities.ContactEntity;

public class MockContactEntity extends MockBaseEntity implements ContactEntity {
  private static final long serialVersionUID = 1L;

  public String name;

  public String telephone;

  public String mobile;

  public String fax;

  public String email;

  public String website;

  public MockContactEntity() {
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public String getTelephone() {
    return this.telephone;
  }

  @Override public String getMobile() {
    return this.mobile;
  }

  @Override public String getFax() {
    return this.fax;
  }

  @Override public String getEmail() {
    return this.email;
  }

  @Override public String getWebsite() {
    return this.website;
  }

  @Override public void setName(String name) {
    this.name = name;
  }

  @Override public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  @Override public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Override public void setFax(String fax) {
    this.fax = fax;
  }

  @Override public void setEmail(String email) {
    this.email = email;
  }

  @Override public void setWebsite(String website) {
    this.website = website;
  }
}