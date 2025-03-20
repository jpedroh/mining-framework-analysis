package com.premiumminds.billy.portugal.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.AddressBuilderImpl;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTAddress;
import com.premiumminds.billy.portugal.persistence.entities.PTAddressEntity;
import com.premiumminds.billy.portugal.services.builders.PTAddressBuilder;
import com.premiumminds.billy.portugal.services.entities.PTAddress;

public class PTAddressBuilderImpl<TBuilder extends PTAddressBuilderImpl<TBuilder, TAddress>, TAddress extends PTAddress> extends AddressBuilderImpl<TBuilder, TAddress> implements PTAddressBuilder<TBuilder, TAddress> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  @Inject protected PTAddressBuilderImpl(DAOPTAddress daoPTAddress) {
    super(daoPTAddress);
  }

  @Override protected PTAddressEntity getTypeInstance() {
    return (PTAddressEntity) super.getTypeInstance();
  }

  @Override protected void validateInstance() throws BillyValidationException {
    super.validateInstance();
    PTAddressEntity address = this.getTypeInstance();
    BillyValidator.mandatory(address.getDetails(), PTAddressBuilderImpl.LOCALIZER.getString("field.details"));
    BillyValidator.mandatory(address.getISOCountry(), PTAddressBuilderImpl.LOCALIZER.getString("field.country"));
  }
}