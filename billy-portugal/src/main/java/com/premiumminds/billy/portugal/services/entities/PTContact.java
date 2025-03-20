package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTContact;
import com.premiumminds.billy.portugal.services.builders.impl.PTContactBuilderImpl;

public interface PTContact extends Contact {
  public static class Builder extends PTContactBuilderImpl<Builder, PTContact> {
    @Inject public Builder(DAOPTContact daoPTContact) {
      super(daoPTContact);
    }
  }
}