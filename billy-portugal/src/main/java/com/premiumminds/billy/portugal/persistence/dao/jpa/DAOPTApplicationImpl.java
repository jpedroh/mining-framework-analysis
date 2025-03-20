package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOApplicationImpl;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTApplication;
import com.premiumminds.billy.portugal.persistence.entities.PTApplicationEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTApplicationEntity;

public class DAOPTApplicationImpl extends DAOApplicationImpl implements DAOPTApplication {
  @Inject public DAOPTApplicationImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTApplicationEntity getEntityInstance() {
    return new JPAPTApplicationEntity();
  }

  @Override protected Class<JPAPTApplicationEntity> getEntityClass() {
    return JPAPTApplicationEntity.class;
  }

  @Override public PTApplicationEntity get(UID uid) throws NoResultException {
    return (PTApplicationEntity) super.get(uid);
  }
}