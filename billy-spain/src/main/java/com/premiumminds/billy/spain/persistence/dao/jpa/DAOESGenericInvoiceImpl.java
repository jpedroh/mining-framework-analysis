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
}