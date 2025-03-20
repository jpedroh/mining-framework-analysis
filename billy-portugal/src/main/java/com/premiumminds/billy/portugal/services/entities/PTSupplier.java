package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Supplier;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSupplier;
import com.premiumminds.billy.portugal.services.builders.impl.PTSupplierBuilderImpl;

public interface PTSupplier extends Supplier {
  public static class Builder extends PTSupplierBuilderImpl<Builder, PTSupplier> {
    @Inject public Builder(DAOPTSupplier daoPTSupplier) {
      super(daoPTSupplier);
    }
  }

  public String getReferralName();
}