package com.premiumminds.billy.spain.services.builders.impl;
import java.math.BigDecimal;
import java.util.Date;
import javax.inject.Inject;
import javax.validation.ValidationException;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.PaymentBuilderImpl;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;
import com.premiumminds.billy.spain.persistence.dao.DAOESPayment;
import com.premiumminds.billy.spain.persistence.entities.ESPaymentEntity;
import com.premiumminds.billy.spain.services.builders.ESPaymentBuilder;
import com.premiumminds.billy.spain.services.entities.ESPayment;

public class ESPaymentBuilderImpl<TBuilder extends ESPaymentBuilderImpl<TBuilder, TPayment>, TPayment extends ESPayment> extends PaymentBuilderImpl<TBuilder, TPayment> implements ESPaymentBuilder<TBuilder, TPayment> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/spain/i18n/FieldNames");

  @Inject public ESPaymentBuilderImpl(DAOESPayment daoESPayment) {
    super(daoESPayment);
  }

  @Override protected ESPaymentEntity getTypeInstance() {
    return (ESPaymentEntity) super.getTypeInstance();
  }

  @Override public TBuilder setPaymentAmount(BigDecimal amount) {
    BillyValidator.notNull(amount, ESPaymentBuilderImpl.LOCALIZER.getString("field.payment_amount"));
    this.getTypeInstance().setPaymentAmount(amount);
    return this.getBuilder();
  }

  @Override public TBuilder setPaymentMethod(Enum<?> method) {
    BillyValidator.notNull(method, ESPaymentBuilderImpl.LOCALIZER.getString("field.payment_method"));
    this.getTypeInstance().setPaymentMethod(method);
    return this.getBuilder();
  }

  @Override public TBuilder setPaymentDate(Date date) {
    BillyValidator.notNull(date, ESPaymentBuilderImpl.LOCALIZER.getString("field.payment_date"));
    this.getTypeInstance().setPaymentDate(date);
    return this.getBuilder();
  }

  @Override protected void validateInstance() throws BillyValidationException, ValidationException {
    super.validateInstance();
    ESPaymentEntity p = this.getTypeInstance();
    BillyValidator.mandatory(p.getPaymentAmount(), ESPaymentBuilderImpl.LOCALIZER.getString("field.payment_amount"));
    BillyValidator.mandatory(p.getPaymentMethod(), ESPaymentBuilderImpl.LOCALIZER.getString("field.payment_method"));
    BillyValidator.mandatory(p.getPaymentDate(), ESPaymentBuilderImpl.LOCALIZER.getString("field.payment_date"));
  }
}