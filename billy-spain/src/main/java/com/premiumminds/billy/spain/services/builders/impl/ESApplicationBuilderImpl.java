package com.premiumminds.billy.spain.services.builders.impl;
import javax.inject.Inject;
import javax.validation.ValidationException;
import com.premiumminds.billy.core.services.builders.impl.ApplicationBuilderImpl;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.spain.persistence.dao.DAOESApplication;
import com.premiumminds.billy.spain.persistence.entities.ESApplicationEntity;
import com.premiumminds.billy.spain.services.builders.ESApplicationBuilder;
import com.premiumminds.billy.spain.services.entities.ESApplication;

public class ESApplicationBuilderImpl<TBuilder extends ESApplicationBuilderImpl<TBuilder, TApplication>, TApplication extends ESApplication> extends ApplicationBuilderImpl<TBuilder, TApplication> implements ESApplicationBuilder<TBuilder, TApplication> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  @Inject public ESApplicationBuilderImpl(DAOESApplication daoESApplication) {
    super(daoESApplication);
  }

  @Override protected ESApplicationEntity getTypeInstance() {
    return (ESApplicationEntity) super.getTypeInstance();
  }

  @Override protected void validateInstance() throws ValidationException {
    super.validateInstance();
  }
}