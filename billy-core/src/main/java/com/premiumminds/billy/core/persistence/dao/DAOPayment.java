package com.premiumminds.billy.core.persistence.dao;
import com.premiumminds.billy.core.persistence.entities.PaymentEntity;

public interface DAOPayment extends DAO<PaymentEntity> {
  @Override public PaymentEntity getEntityInstance();
}