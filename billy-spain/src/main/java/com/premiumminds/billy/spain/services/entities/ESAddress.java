package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Address;
import com.premiumminds.billy.spain.persistence.dao.DAOESAddress;
import com.premiumminds.billy.spain.services.builders.impl.ESAddressBuilderImpl;

public interface ESAddress extends Address {
  public static class Builder extends ESAddressBuilderImpl<Builder, ESAddress> {
    @Inject public Builder(DAOESAddress daoESAddress) {
      super(daoESAddress);
    }
  }
}