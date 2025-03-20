package com.premiumminds.billy.spain.persistence.entities.jpa;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntity;
import com.premiumminds.billy.spain.services.entities.ESPayment;
import com.premiumminds.billy.spain.services.entities.ESReceiptEntry;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "RECEIPT") @Inheritance(strategy = InheritanceType.JOINED) public class JPAESReceiptEntity extends JPAESGenericInvoiceEntity implements ESReceiptEntity {
  private static final long serialVersionUID = 1L;

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESReceiptEntry> getEntries() {
    return (List<ESReceiptEntry>) super.getEntries();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESPayment> getPayments() {
    return super.getPayments();
  }
}