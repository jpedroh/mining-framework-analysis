package com.premiumminds.billy.spain.test.util;
import java.math.BigDecimal;
import java.util.Date;
import com.google.inject.Injector;
import com.premiumminds.billy.core.util.PaymentMechanism;
import com.premiumminds.billy.spain.persistence.entities.ESPaymentEntity;
import com.premiumminds.billy.spain.services.entities.ESPayment;

public class ESPaymentTestUtil {
  private static final BigDecimal AMOUNT = new BigDecimal(20);

  private static final Date DATE = new Date();

  private static final Enum<PaymentMechanism> METHOD = PaymentMechanism.CASH;

  private Injector injector;

  public ESPaymentTestUtil(Injector injector) {
    this.injector = injector;
  }

  public ESPayment.Builder getPaymentBuilder(BigDecimal amount, Date date, Enum<?> method) {
    ESPayment.Builder paymentBuilder = this.injector.getInstance(ESPayment.Builder.class);
    paymentBuilder.setPaymentAmount(amount).setPaymentDate(date).setPaymentMethod(method);
    return paymentBuilder;
  }

  public ESPayment.Builder getPaymentBuilder() {
    return this.getPaymentBuilder(ESPaymentTestUtil.AMOUNT, ESPaymentTestUtil.DATE, ESPaymentTestUtil.METHOD);
  }

  public ESPaymentEntity getPaymentEntity(BigDecimal amount, Date date, Enum<?> method) {
    return (ESPaymentEntity) this.getPaymentBuilder(amount, date, method).build();
  }

  public ESPaymentEntity getPaymentEntity() {
    return (ESPaymentEntity) this.getPaymentBuilder().build();
  }
}