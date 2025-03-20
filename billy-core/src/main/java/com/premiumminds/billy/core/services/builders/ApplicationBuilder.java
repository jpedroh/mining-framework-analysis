package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.entities.Application;
import com.premiumminds.billy.core.services.entities.Contact;

public interface ApplicationBuilder<TBuilder extends ApplicationBuilder<TBuilder, TApplication>, TApplication extends Application> extends Builder<TApplication> {
  public TBuilder setName(String name);

  public TBuilder setVersion(String version);

  public TBuilder setDeveloperCompanyName(String name);

  public TBuilder setDeveloperCompanyTaxIdentifier(String id);

  public TBuilder setWebsiteAddress(String website);

  public <T extends Contact> TBuilder addContact(Builder<T> contactBuilder);

  public <T extends Contact> TBuilder setMainContact(Builder<T> contactBuilder);
}