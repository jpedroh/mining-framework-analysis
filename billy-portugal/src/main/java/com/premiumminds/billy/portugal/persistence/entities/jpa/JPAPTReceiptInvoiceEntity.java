package com.premiumminds.billy.portugal.persistence.entities.jpa;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTReceiptInvoiceEntity;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "RECEIPT_INVOICE") public class JPAPTReceiptInvoiceEntity extends JPAPTInvoiceEntity implements PTReceiptInvoiceEntity {
  private static final long serialVersionUID = 1L;

  @Override public List<PTInvoiceEntry> getEntries() {
    return super.getEntries();
  }
}