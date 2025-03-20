package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTGenericInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTGenericInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTGenericInvoiceEntity;

public class DAOPTGenericInvoiceImpl extends AbstractDAOPTGenericInvoiceImpl<PTGenericInvoiceEntity, JPAPTGenericInvoiceEntity> implements DAOPTGenericInvoice {
  @Inject public DAOPTGenericInvoiceImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTGenericInvoiceEntity getEntityInstance() {
    return new JPAPTGenericInvoiceEntity();
  }

  @Override protected Class<? extends JPAPTGenericInvoiceEntity> getEntityClass() {
    return JPAPTGenericInvoiceEntity.class;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) @Override public <T extends PTGenericInvoiceEntity> T findByNumber(UID uidBusiness, String number) {
    QJPAPTGenericInvoiceEntity invoice = QJPAPTGenericInvoiceEntity.jPAPTGenericInvoiceEntity;
    return (T) this.checkEntity(this.createQuery().from(invoice).where(this.toDSL(invoice.business, QJPAPTBusinessEntity.class).uid.eq(uidBusiness.toString()).and(invoice.number.eq(number))).singleResult(invoice), PTGenericInvoiceEntity.class);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/right.java
}