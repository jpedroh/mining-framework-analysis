package com.premiumminds.billy.portugal.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.ShippingPointBuilderImpl;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTShippingPoint;
import com.premiumminds.billy.portugal.persistence.entities.PTShippingPointEntity;
import com.premiumminds.billy.portugal.services.builders.PTShippingPointBuilder;
import com.premiumminds.billy.portugal.services.entities.PTShippingPoint;

public class PTShippingPointBuilderImpl<TBuilder extends PTShippingPointBuilderImpl<TBuilder, TShippingPoint>, TShippingPoint extends PTShippingPoint> extends ShippingPointBuilderImpl<TBuilder, TShippingPoint> implements PTShippingPointBuilder<TBuilder, TShippingPoint> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  @Inject public PTShippingPointBuilderImpl(DAOPTShippingPoint daoPTShippingPoint) {
    super(daoPTShippingPoint);
  }

  @Override protected PTShippingPointEntity getTypeInstance() {
    return (PTShippingPointEntity) super.getTypeInstance();
  }

  @Override protected void validateInstance() throws BillyValidationException {
    super.validateInstance();
  }
}