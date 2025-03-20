package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOBankAccount;
import com.premiumminds.billy.core.persistence.entities.BankAccountEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPABankAccountEntity;

public class DAOBankAccountImpl extends AbstractDAO<BankAccountEntity, JPABankAccountEntity> implements DAOBankAccount {
  @Inject public DAOBankAccountImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPABankAccountEntity> getEntityClass() {
    return JPABankAccountEntity.class;
  }

  @Override public BankAccountEntity getEntityInstance() {
    return new JPABankAccountEntity();
  }
}