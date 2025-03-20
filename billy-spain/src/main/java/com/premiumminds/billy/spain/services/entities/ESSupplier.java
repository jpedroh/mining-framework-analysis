package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Supplier;
import com.premiumminds.billy.spain.persistence.dao.DAOESSupplier;
import com.premiumminds.billy.spain.services.builders.impl.ESSupplierBuilderImpl;

public interface ESSupplier extends Supplier {
  public static class Builder extends ESSupplierBuilderImpl<Builder, ESSupplier> {
    @Inject public Builder(DAOESSupplier daoESSupplier) {
      super(daoESSupplier);
    }
  }

  public String getReferralName();
}