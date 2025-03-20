package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.DAOContact;
import com.premiumminds.billy.core.persistence.entities.ContactEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAContactEntity;

public class DAOContactImpl extends AbstractDAO<ContactEntity, JPAContactEntity> implements DAOContact {
  @Inject public DAOContactImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override protected Class<? extends JPAContactEntity> getEntityClass() {
    return JPAContactEntity.class;
  }

  @Override public ContactEntity getEntityInstance() {
    return new JPAContactEntity();
  }
}