package com.premiumminds.billy.spain.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.ShippingPointBuilderImpl;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.spain.persistence.dao.DAOESShippingPoint;
import com.premiumminds.billy.spain.persistence.entities.ESShippingPointEntity;
import com.premiumminds.billy.spain.services.builders.ESShippingPointBuilder;
import com.premiumminds.billy.spain.services.entities.ESShippingPoint;

public class ESShippingPointBuilderImpl<TBuilder extends ESShippingPointBuilderImpl<TBuilder, TShippingPoint>, TShippingPoint extends ESShippingPoint> extends ShippingPointBuilderImpl<TBuilder, TShippingPoint> implements ESShippingPointBuilder<TBuilder, TShippingPoint> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  @Inject public ESShippingPointBuilderImpl(DAOESShippingPoint daoESShippingPoint) {
    super(daoESShippingPoint);
  }

  @Override protected ESShippingPointEntity getTypeInstance() {
    return (ESShippingPointEntity) super.getTypeInstance();
  }

  @Override protected void validateInstance() throws BillyValidationException {
    super.validateInstance();
  }
}