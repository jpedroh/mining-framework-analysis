package com.premiumminds.billy.portugal.services.builders.impl;
import javax.inject.Inject;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.TaxBuilderImpl;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTRegionContext;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTTax;
import com.premiumminds.billy.portugal.persistence.entities.PTRegionContextEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTTaxEntity;
import com.premiumminds.billy.portugal.services.builders.PTTaxBuilder;
import com.premiumminds.billy.portugal.services.entities.PTTax;

public class PTTaxBuilderImpl<TBuilder extends PTTaxBuilderImpl<TBuilder, TTax>, TTax extends PTTax> extends TaxBuilderImpl<TBuilder, TTax> implements PTTaxBuilder<TBuilder, TTax> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  @Inject public PTTaxBuilderImpl(DAOPTTax daoPTTax, DAOPTRegionContext daoPTContext) {
    super(daoPTTax, daoPTContext);
  }

  @Override protected PTTaxEntity getTypeInstance() {
    return (PTTaxEntity) super.getTypeInstance();
  }

  @Override protected void validateInstance() throws BillyValidationException {
    PTTaxEntity e = this.getTypeInstance();
    BillyValidator.mandatory(e.getContext(), PTTaxBuilderImpl.LOCALIZER.getString("field.tax_context"));
    BillyValidator.mandatory(e.getTaxRateType(), PTTaxBuilderImpl.LOCALIZER.getString("field.tax_rate_type"));
    BillyValidator.mandatory(e.getCode(), PTTaxBuilderImpl.LOCALIZER.getString("field.tax_code"));
    BillyValidator.mandatory(e.getDescription(), PTTaxBuilderImpl.LOCALIZER.getString("field.tax_description"));
    if (!((DAOPTTax) this.daoTax).getTaxes((PTRegionContextEntity) e.getContext(), e.getValidFrom(), e.getValidTo()).isEmpty()) {
      throw new BillyValidationException();
    }
    super.validateInstance();
  }
}