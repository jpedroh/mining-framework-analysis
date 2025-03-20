package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOPayment;
import com.premiumminds.billy.core.persistence.entities.PaymentEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAPaymentEntity;

public class DAOPaymentImpl extends AbstractDAO<PaymentEntity, JPAPaymentEntity> implements DAOPayment {
  @Inject public DAOPaymentImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PaymentEntity getEntityInstance() {
    return new JPAPaymentEntity();
  }

  @Override protected Class<? extends JPAPaymentEntity> getEntityClass() {
    return JPAPaymentEntity.class;
  }
}