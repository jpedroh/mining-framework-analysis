package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Business;
import com.premiumminds.billy.spain.persistence.dao.DAOESBusiness;
import com.premiumminds.billy.spain.persistence.dao.DAOESRegionContext;
import com.premiumminds.billy.spain.services.builders.impl.ESBusinessBuilderImpl;

public interface ESBusiness extends Business {
  public static class Builder extends ESBusinessBuilderImpl<Builder, ESBusiness> {
    @Inject public Builder(DAOESBusiness daoESBusiness, DAOESRegionContext daoESRegionContext) {
      super(daoESBusiness, daoESRegionContext);
    }
  }
}