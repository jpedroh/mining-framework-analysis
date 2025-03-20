package com.premiumminds.billy.spain.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.spain.persistence.dao.DAOESContact;
import com.premiumminds.billy.spain.services.builders.impl.ESContactBuilderImpl;

public interface ESContact extends Contact {
  public static class Builder extends ESContactBuilderImpl<Builder, ESContact> {
    @Inject public Builder(DAOESContact daoESContact) {
      super(daoESContact);
    }
  }
}