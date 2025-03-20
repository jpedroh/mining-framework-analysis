package com.premiumminds.billy.portugal.test.util;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.entities.PTContactEntity;
import com.premiumminds.billy.portugal.services.entities.PTContact;

public class PTContactTestUtil {
  private static final String NAME = "name";

  private static final String TELEPHONE = "998887999";

  private static final String MOBILE = "999999999";

  private static final String EMAIL = "email@email.em";

  private static final String FAX = "9999999122";

  private static final String WEBSITE = "website@website.web";

  private Injector injector;

  public PTContactTestUtil(Injector injector) {
    this.injector = injector;
  }

  public PTContact.Builder getContactBuilder(String name, String telephone, String mobile, String fax, String email, String website) {
    PTContact.Builder contactBuilder = this.injector.getInstance(PTContact.Builder.class);
    contactBuilder.setName(name).setEmail(email).setMobile(mobile).setFax(fax).setTelephone(telephone).setWebsite(website);
    return contactBuilder;
  }

  public PTContactEntity getContactEntity(String uid) {
    PTContactEntity entity = (PTContactEntity) this.getContactBuilder().build();
    entity.setUID(new UID(uid));
    return entity;
  }

  public PTContactEntity getContactEntity() {
    return (PTContactEntity) this.getContactBuilder().build();
  }

  public PTContact.Builder getContactBuilder() {
    return this.getContactBuilder(PTContactTestUtil.NAME, PTContactTestUtil.TELEPHONE, PTContactTestUtil.MOBILE, PTContactTestUtil.FAX, PTContactTestUtil.EMAIL, PTContactTestUtil.WEBSITE);
  }
}