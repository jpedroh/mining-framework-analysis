package com.premiumminds.billy.spain.persistence.entities.jpa;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESInvoiceEntity;
import com.premiumminds.billy.spain.services.entities.ESInvoiceEntry;
import com.premiumminds.billy.spain.services.entities.ESPayment;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "INVOICE") @Inheritance(strategy = InheritanceType.JOINED) public class JPAESInvoiceEntity extends JPAESGenericInvoiceEntity implements ESInvoiceEntity {
  private static final long serialVersionUID = 1L;

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESInvoiceEntry> getEntries() {
    return (List<ESInvoiceEntry>) super.getEntries();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESPayment> getPayments() {
    return super.getPayments();
  }
}