package com.premiumminds.billy.portugal.services.entities;
import javax.inject.Inject;
import com.premiumminds.billy.core.services.entities.Context;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTRegionContext;
import com.premiumminds.billy.portugal.services.builders.impl.PTRegionContextBuilderImpl;

public interface PTRegionContext extends Context {
  public static class Builder extends PTRegionContextBuilderImpl<Builder, PTRegionContext> {
    @Inject public Builder(DAOPTRegionContext daoPTRegionContext) {
      super(daoPTRegionContext);
    }
  }

  /**
     * @return ISO 3166-1-alpha-2
     */
  public String getRegionCode();

  @Override public <T extends Context> T getParentContext();
}