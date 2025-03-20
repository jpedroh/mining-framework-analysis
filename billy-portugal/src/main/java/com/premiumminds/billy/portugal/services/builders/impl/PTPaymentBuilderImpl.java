package com.premiumminds.billy.portugal.services.builders.impl;
import java.math.BigDecimal;
import java.util.Date;
import javax.inject.Inject;
import javax.validation.ValidationException;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.PaymentBuilderImpl;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTPayment;
import com.premiumminds.billy.portugal.persistence.entities.PTPaymentEntity;
import com.premiumminds.billy.portugal.services.builders.PTPaymentBuilder;
import com.premiumminds.billy.portugal.services.entities.PTPayment;

public class PTPaymentBuilderImpl<TBuilder extends PTPaymentBuilderImpl<TBuilder, TPayment>, TPayment extends PTPayment> extends PaymentBuilderImpl<TBuilder, TPayment> implements PTPaymentBuilder<TBuilder, TPayment> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/portugal/i18n/FieldNames");

  @Inject public PTPaymentBuilderImpl(DAOPTPayment daoPTPayment) {
    super(daoPTPayment);
  }

  @Override protected PTPaymentEntity getTypeInstance() {
    return (PTPaymentEntity) super.getTypeInstance();
  }

  @Override public TBuilder setPaymentAmount(BigDecimal amount) {
    BillyValidator.notNull(amount, PTPaymentBuilderImpl.LOCALIZER.getString("field.payment_amount"));
    this.getTypeInstance().setPaymentAmount(amount);
    return this.getBuilder();
  }

  @Override public TBuilder setPaymentMethod(Enum<?> method) {
    BillyValidator.notNull(method, PTPaymentBuilderImpl.LOCALIZER.getString("field.payment_method"));
    this.getTypeInstance().setPaymentMethod(method);
    return this.getBuilder();
  }

  @Override public TBuilder setPaymentDate(Date date) {
    BillyValidator.notNull(date, PTPaymentBuilderImpl.LOCALIZER.getString("field.payment_date"));
    this.getTypeInstance().setPaymentDate(date);
    return this.getBuilder();
  }

  @Override protected void validateInstance() throws BillyValidationException, ValidationException {
    super.validateInstance();
    PTPaymentEntity p = this.getTypeInstance();
    BillyValidator.mandatory(p.getPaymentAmount(), PTPaymentBuilderImpl.LOCALIZER.getString("field.payment_amount"));
    BillyValidator.mandatory(p.getPaymentMethod(), PTPaymentBuilderImpl.LOCALIZER.getString("field.payment_method"));
    BillyValidator.mandatory(p.getPaymentDate(), PTPaymentBuilderImpl.LOCALIZER.getString("field.payment_date"));
  }
}