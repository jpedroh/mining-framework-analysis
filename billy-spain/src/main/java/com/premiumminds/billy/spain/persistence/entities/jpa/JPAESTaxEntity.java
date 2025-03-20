package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPATaxEntity;
import com.premiumminds.billy.core.services.entities.Context;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESTaxEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "TAX") public class JPAESTaxEntity extends JPATaxEntity implements ESTaxEntity {
  private static final long serialVersionUID = 1L;

  @Override public Context getContext() {
    return super.getContext();
  }
}