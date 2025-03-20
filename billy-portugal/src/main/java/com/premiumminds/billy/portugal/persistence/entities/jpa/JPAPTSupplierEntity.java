package com.premiumminds.billy.portugal.persistence.entities.jpa;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPASupplierEntity;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTSupplierEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "SUPPLIER") public class JPAPTSupplierEntity extends JPASupplierEntity implements PTSupplierEntity {
  private static final long serialVersionUID = 1L;

  @Column(name = "REFERRAL_NAME") protected String referralName;

  @Override public String getReferralName() {
    return this.referralName;
  }

  @Override public void setReferralName(String referralName) {
    this.referralName = referralName;
  }
}