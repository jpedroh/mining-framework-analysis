package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOPaymentImpl;
import com.premiumminds.billy.spain.persistence.dao.DAOESPayment;
import com.premiumminds.billy.spain.persistence.entities.ESPaymentEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESPaymentEntity;

public class DAOESPaymentImpl extends DAOPaymentImpl implements DAOESPayment {
  @Inject public DAOESPaymentImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESPaymentEntity getEntityInstance() {
    return new JPAESPaymentEntity();
  }

  @Override protected Class<JPAESPaymentEntity> getEntityClass() {
    return JPAESPaymentEntity.class;
  }
}