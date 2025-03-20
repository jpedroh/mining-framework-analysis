package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAProductEntity;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESProductEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "PRODUCT") public class JPAESProductEntity extends JPAProductEntity implements ESProductEntity {
  private static final long serialVersionUID = 1L;
}