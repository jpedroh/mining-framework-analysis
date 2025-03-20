package com.premiumminds.billy.spain.persistence.entities.jpa;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAPaymentEntity;
import com.premiumminds.billy.core.util.PaymentMechanism;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESPaymentEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "PAYMENT") public class JPAESPaymentEntity extends JPAPaymentEntity implements ESPaymentEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  @Column(name = "PAYMENT_AMOUNT") protected BigDecimal paymentAmount;

  @Override public BigDecimal getPaymentAmount() {
    return this.paymentAmount;
  }

  @Override public void setPaymentAmount(BigDecimal amount) {
    this.paymentAmount = amount;
  }

  @Override public PaymentMechanism getPaymentMethod() {
    return PaymentMechanism.valueOf(this.paymentMethod);
  }
}