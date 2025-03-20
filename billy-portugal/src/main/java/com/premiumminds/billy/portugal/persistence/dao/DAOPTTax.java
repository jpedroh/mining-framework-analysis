package com.premiumminds.billy.portugal.persistence.dao;
import java.util.Date;
import java.util.List;
import com.premiumminds.billy.core.persistence.dao.DAOTax;
import com.premiumminds.billy.portugal.persistence.entities.PTRegionContextEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTTaxEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTTaxEntity;

public interface DAOPTTax extends DAOTax {
  @Override public PTTaxEntity getEntityInstance();

  public List<JPAPTTaxEntity> getTaxes(PTRegionContextEntity context, Date validFrom, Date validTo);

  public List<JPAPTTaxEntity> getTaxesForSAFTPT(PTRegionContextEntity context, Date validFrom, Date validTo);
}