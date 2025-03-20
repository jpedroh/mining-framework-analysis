package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPABusinessEntity;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESBusinessEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "BUSINESS") public class JPAESBusinessEntity extends JPABusinessEntity implements ESBusinessEntity {
  private static final long serialVersionUID = 1L;
}