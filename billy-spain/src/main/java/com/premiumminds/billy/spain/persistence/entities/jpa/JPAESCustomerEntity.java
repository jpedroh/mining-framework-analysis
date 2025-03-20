package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPACustomerEntity;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESCustomerEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CUSTOMER") public class JPAESCustomerEntity extends JPACustomerEntity implements ESCustomerEntity {
  private static final long serialVersionUID = 1L;

  @Column(name = "REFERRAL_NAME") protected String referralName;

  @Override public String getReferralName() {
    return this.referralName;
  }

  @Override public void setReferralName(String referralName) {
    this.referralName = referralName;
  }
}