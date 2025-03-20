package com.premiumminds.billy.portugal.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntryEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "INVOICE_ENTRY") public class JPAPTInvoiceEntryEntity extends JPAPTGenericInvoiceEntryEntity implements PTInvoiceEntryEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;
}