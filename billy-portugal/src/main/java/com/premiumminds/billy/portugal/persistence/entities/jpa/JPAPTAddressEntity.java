package com.premiumminds.billy.portugal.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAAddressEntity;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTAddressEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "ADDRESS") public class JPAPTAddressEntity extends JPAAddressEntity implements PTAddressEntity {
  private static final long serialVersionUID = 1L;
}