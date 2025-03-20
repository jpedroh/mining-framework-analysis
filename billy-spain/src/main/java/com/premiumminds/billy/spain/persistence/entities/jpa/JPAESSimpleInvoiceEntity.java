package com.premiumminds.billy.spain.persistence.entities.jpa;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESSimpleInvoiceEntity;
import com.premiumminds.billy.spain.services.entities.ESInvoiceEntry;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "SIMPLE_INVOICE") public class JPAESSimpleInvoiceEntity extends JPAESInvoiceEntity implements ESSimpleInvoiceEntity {
  private static final long serialVersionUID = 1L;

  @Column(name = "CLIENT_TYPE") protected CLIENTTYPE clientType;

  @Override public List<ESInvoiceEntry> getEntries() {
    return super.getEntries();
  }

  @Override public CLIENTTYPE getClientType() {
    return this.clientType;
  }

  @Override public void setClientType(CLIENTTYPE type) {
    this.clientType = type;
  }
}