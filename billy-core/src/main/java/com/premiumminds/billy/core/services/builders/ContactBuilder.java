package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.entities.Contact;

public interface ContactBuilder<TBuilder extends ContactBuilder<TBuilder, TContact>, TContact extends Contact> extends Builder<TContact> {
  public TBuilder setName(String name);

  public TBuilder setTelephone(String telephone);

  public TBuilder setMobile(String mobile);

  public TBuilder setFax(String fax);

  public TBuilder setEmail(String email);

  public TBuilder setWebsite(String website);
}