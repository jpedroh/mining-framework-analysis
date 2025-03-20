package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.spain.persistence.dao.DAOESGenericInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESGenericInvoiceEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESGenericInvoiceEntity;

public class DAOESGenericInvoiceImpl extends AbstractDAOESGenericInvoiceImpl<ESGenericInvoiceEntity, JPAESGenericInvoiceEntity> implements DAOESGenericInvoice {
  @Inject public DAOESGenericInvoiceImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESGenericInvoiceEntity getEntityInstance() {
    return new JPAESGenericInvoiceEntity();
  }

  @Override protected Class<? extends JPAESGenericInvoiceEntity> getEntityClass() {
    return JPAESGenericInvoiceEntity.class;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @SuppressWarnings(value = { "unchecked" }) @Override public <T extends ESGenericInvoiceEntity> T findByNumber(UID uidBusiness, String number) {
    QJPAESGenericInvoiceEntity invoice = QJPAESGenericInvoiceEntity.jPAESGenericInvoiceEntity;
    return (T) this.checkEntity(this.createQuery().from(invoice).where(this.toDSL(invoice.business, QJPAESBusinessEntity.class).uid.eq(uidBusiness.toString()).and(invoice.number.eq(number))).singleResult(invoice), ESGenericInvoiceEntity.class);
  }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/persistence/dao/jpa/DAOESGenericInvoiceImpl.java/right.java
}