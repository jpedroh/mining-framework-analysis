package com.premiumminds.billy.core.persistence.entities.jpa;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.Config;
import com.premiumminds.billy.core.persistence.entities.PaymentEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "PAYMENT") public class JPAPaymentEntity extends JPABaseEntity implements PaymentEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  @Column(name = "PAYMENT_METHOD") protected String paymentMethod;

  @Temporal(value = TemporalType.TIMESTAMP) @Column(name = "PAYMENT_DATE") protected Date paymentDate;

  public <T extends Enum<T>> T getPaymentMethod(Class<T> enumType) {
    return Enum.valueOf(enumType, this.paymentMethod);
  }

  @Override public Date getPaymentDate() {
    return this.paymentDate;
  }

  @Override public void setPaymentMethod(Enum<?> method) {
    this.paymentMethod = method.toString();
  }

  @Override public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }

  @Override public <T extends Enum<?>> T getPaymentMethod() {
    return null;
  }
}