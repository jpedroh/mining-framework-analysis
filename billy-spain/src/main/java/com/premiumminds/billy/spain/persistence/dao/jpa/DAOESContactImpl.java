package com.premiumminds.billy.spain.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOContactImpl;
import com.premiumminds.billy.spain.persistence.dao.DAOESContact;
import com.premiumminds.billy.spain.persistence.entities.ESContactEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESContactEntity;

public class DAOESContactImpl extends DAOContactImpl implements DAOESContact {
  @Inject public DAOESContactImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public ESContactEntity getEntityInstance() {
    return new JPAESContactEntity();
  }

  @Override protected Class<JPAESContactEntity> getEntityClass() {
    return JPAESContactEntity.class;
  }
}