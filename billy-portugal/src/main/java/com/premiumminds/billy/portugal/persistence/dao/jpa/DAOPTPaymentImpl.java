package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOPaymentImpl;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTPayment;
import com.premiumminds.billy.portugal.persistence.entities.PTPaymentEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTPaymentEntity;

public class DAOPTPaymentImpl extends DAOPaymentImpl implements DAOPTPayment {
  @Inject public DAOPTPaymentImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTPaymentEntity getEntityInstance() {
    return new JPAPTPaymentEntity();
  }

  @Override protected Class<JPAPTPaymentEntity> getEntityClass() {
    return JPAPTPaymentEntity.class;
  }
}