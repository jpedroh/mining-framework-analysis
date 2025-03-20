package com.premiumminds.billy.spain.persistence.dao;
import com.premiumminds.billy.core.persistence.dao.DAOProduct;
import com.premiumminds.billy.spain.persistence.entities.ESProductEntity;

public interface DAOESProduct extends DAOProduct {
  @Override public ESProductEntity getEntityInstance();
}