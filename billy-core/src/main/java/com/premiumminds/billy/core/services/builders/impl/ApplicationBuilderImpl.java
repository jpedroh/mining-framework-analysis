package com.premiumminds.billy.core.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOApplication;
import com.premiumminds.billy.core.persistence.entities.ApplicationEntity;
import com.premiumminds.billy.core.persistence.entities.ContactEntity;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.builders.ApplicationBuilder;
import com.premiumminds.billy.core.services.entities.Application;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;

public class ApplicationBuilderImpl<TBuilder extends ApplicationBuilderImpl<TBuilder, TApplication>, TApplication extends Application> extends AbstractBuilder<TBuilder, TApplication> implements ApplicationBuilder<TBuilder, TApplication> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  protected DAOApplication daoApplication;

  @Inject public ApplicationBuilderImpl(DAOApplication daoApplication) {
    super(daoApplication);
    this.daoApplication = daoApplication;
  }

  @Override public TBuilder setName(String name) {
    BillyValidator.notBlank(name, ApplicationBuilderImpl.LOCALIZER.getString("field.application_name"));
    this.getTypeInstance().setName(name);
    return this.getBuilder();
  }

  @Override public TBuilder setVersion(String version) {
    BillyValidator.notBlank(version, ApplicationBuilderImpl.LOCALIZER.getString("field.version"));
    this.getTypeInstance().setVersion(version);
    return this.getBuilder();
  }

  @Override public TBuilder setDeveloperCompanyName(String name) {
    BillyValidator.notBlank(name, ApplicationBuilderImpl.LOCALIZER.getString("field.developer_name"));
    this.getTypeInstance().setDeveloperCompanyName(name);
    return this.getBuilder();
  }

  @Override public TBuilder setDeveloperCompanyTaxIdentifier(String id) {
    BillyValidator.notBlank(id, ApplicationBuilderImpl.LOCALIZER.getString("field.developer_tax_id"));
    this.getTypeInstance().setDeveloperCompanyTaxIdentifier(id);
    return this.getBuilder();
  }

  @Override public <T extends Contact> TBuilder addContact(Builder<T> contactBuilder) {
    BillyValidator.notNull(contactBuilder, ApplicationBuilderImpl.LOCALIZER.getString("field.application_contact"));
    ContactEntity contact = (ContactEntity) contactBuilder.build();
    this.getTypeInstance().getContacts().add(contact);
    if (this.getTypeInstance().getContacts().size() == 1) {
      this.setMainContact(contactBuilder);
    }
    return this.getBuilder();
  }

  @Override public <T extends Contact> TBuilder setMainContact(Builder<T> contactBuilder) {
    BillyValidator.notNull(contactBuilder, ApplicationBuilderImpl.LOCALIZER.getString("field.application_main_contact"));
    this.getTypeInstance().setMainContact((ContactEntity) contactBuilder.build());
    return this.getBuilder();
  }

  @Override public TBuilder setWebsiteAddress(String website) {
    BillyValidator.notBlank(website, ApplicationBuilderImpl.LOCALIZER.getString("field.application_website"));
    this.getTypeInstance().setWebsiteAddress(website);
    return this.getBuilder();
  }

  @Override protected void validateInstance() throws javax.validation.ValidationException {
    ApplicationEntity application = this.getTypeInstance();
    BillyValidator.mandatory(application.getName(), ApplicationBuilderImpl.LOCALIZER.getString("field.application_name"));
    BillyValidator.mandatory(application.getVersion(), ApplicationBuilderImpl.LOCALIZER.getString("field.version"));
    BillyValidator.mandatory(application.getDeveloperCompanyName(), ApplicationBuilderImpl.LOCALIZER.getString("field.developer_name"));
    BillyValidator.mandatory(application.getDeveloperCompanyTaxIdentifier(), ApplicationBuilderImpl.LOCALIZER.getString("field.developer_tax_id"));
  }

  @SuppressWarnings(value = { "unchecked" }) @Override protected ApplicationEntity getTypeInstance() {
    return (ApplicationEntity) super.getTypeInstance();
  }
}