package com.premiumminds.billy.spain.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.ContactBuilderImpl;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.spain.persistence.dao.DAOESContact;
import com.premiumminds.billy.spain.persistence.entities.ESContactEntity;
import com.premiumminds.billy.spain.services.builders.ESContactBuilder;
import com.premiumminds.billy.spain.services.entities.ESContact;

public class ESContactBuilderImpl<TBuilder extends ESContactBuilderImpl<TBuilder, TContact>, TContact extends ESContact> extends ContactBuilderImpl<TBuilder, TContact> implements ESContactBuilder<TBuilder, TContact> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  @Inject public ESContactBuilderImpl(DAOESContact daoESContact) {
    super(daoESContact);
  }

  @Override protected ESContactEntity getTypeInstance() {
    return (ESContactEntity) super.getTypeInstance();
  }

  @Override protected void validateInstance() throws BillyValidationException {
  }
}