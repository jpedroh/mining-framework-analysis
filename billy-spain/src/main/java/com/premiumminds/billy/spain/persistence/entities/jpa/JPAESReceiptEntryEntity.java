package com.premiumminds.billy.spain.persistence.entities.jpa;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntryEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "RECEIPT_ENTRY") @Inheritance(strategy = InheritanceType.JOINED) public class JPAESReceiptEntryEntity extends JPAESGenericInvoiceEntryEntity implements ESReceiptEntryEntity {
  private static final long serialVersionUID = 1L;
}