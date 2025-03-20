package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Context;

public interface ContextBuilder<TBuilder extends ContextBuilder<TBuilder, TContext>, TContext extends Context> extends Builder<TContext> {
  public TBuilder setName(String name);

  public TBuilder setDescription(String description);

  public TBuilder setParentContextUID(UID parentUID);
}