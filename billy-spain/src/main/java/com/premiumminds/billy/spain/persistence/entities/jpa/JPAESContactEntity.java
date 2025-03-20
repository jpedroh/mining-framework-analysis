package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAContactEntity;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESContactEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CONTACT") public class JPAESContactEntity extends JPAContactEntity implements ESContactEntity {
  private static final long serialVersionUID = 1L;
}