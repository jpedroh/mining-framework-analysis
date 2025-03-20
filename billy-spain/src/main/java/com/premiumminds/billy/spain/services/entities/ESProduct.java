package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Product;
import com.premiumminds.billy.spain.persistence.dao.DAOESProduct;
import com.premiumminds.billy.spain.persistence.dao.DAOESTax;
import com.premiumminds.billy.spain.services.builders.impl.ESProductBuilderImpl;

public interface ESProduct extends Product {
  public static class Builder extends ESProductBuilderImpl<Builder, ESProduct> {
    @Inject public Builder(DAOESProduct daoESProduct, DAOESTax daoESTax) {
      super(daoESProduct, daoESTax);
    }
  }
}