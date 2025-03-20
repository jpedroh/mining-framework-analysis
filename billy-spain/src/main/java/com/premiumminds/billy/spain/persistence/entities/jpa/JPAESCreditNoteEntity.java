package com.premiumminds.billy.spain.persistence.entities.jpa;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.spain.Config;
import com.premiumminds.billy.spain.persistence.entities.ESCreditNoteEntity;
import com.premiumminds.billy.spain.services.entities.ESCreditNoteEntry;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "CREDIT_NOTE") public class JPAESCreditNoteEntity extends JPAESGenericInvoiceEntity implements ESCreditNoteEntity {
  private static final long serialVersionUID = 1L;

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ESCreditNoteEntry> getEntries() {
    return (List<ESCreditNoteEntry>) super.getEntries();
  }
}