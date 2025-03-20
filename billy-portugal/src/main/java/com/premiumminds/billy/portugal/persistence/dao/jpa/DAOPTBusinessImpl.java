package com.premiumminds.billy.portugal.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import com.premiumminds.billy.core.persistence.dao.jpa.DAOBusinessImpl;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTBusinessEntity;

public class DAOPTBusinessImpl extends DAOBusinessImpl implements DAOPTBusiness {
  @Inject public DAOPTBusinessImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public PTBusinessEntity getEntityInstance() {
    return new JPAPTBusinessEntity();
  }

  @Override protected Class<JPAPTBusinessEntity> getEntityClass() {
    return JPAPTBusinessEntity.class;
  }

  @Override public PTBusinessEntity get(UID uid) throws NoResultException {
    return (PTBusinessEntity) super.get(uid);
  }
}