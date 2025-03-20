package com.premiumminds.billy.portugal.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAContactEntity;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTContactEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CONTACT") public class JPAPTContactEntity extends JPAContactEntity implements PTContactEntity {
  private static final long serialVersionUID = 1L;
}