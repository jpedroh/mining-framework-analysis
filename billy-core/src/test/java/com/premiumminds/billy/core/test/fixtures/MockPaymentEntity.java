package com.premiumminds.billy.core.test.fixtures;
import java.util.Date;
import com.premiumminds.billy.core.persistence.entities.PaymentEntity;

public class MockPaymentEntity extends MockBaseEntity implements PaymentEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  protected Enum<?> paymentMethod;

  protected Date paymentDate;

  @Override public Enum<?> getPaymentMethod() {
    return this.paymentMethod;
  }

  @Override public Date getPaymentDate() {
    return this.paymentDate;
  }

  @Override public void setPaymentMethod(Enum<?> method) {
    this.paymentMethod = method;
  }

  @Override public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }
}