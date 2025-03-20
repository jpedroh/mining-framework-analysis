package com.premiumminds.billy.core.services.builders.impl;
import javax.validation.ValidationException;
import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.persistence.entities.BaseEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.util.EntityFactory;

public abstract class AbstractBuilder<TBuilder extends AbstractBuilder<TBuilder, TType>, TType extends java.lang.Object> {
  private EntityFactory<?> factory;

  protected TType typeInstance;

  public AbstractBuilder(EntityFactory<?> entityFactory) {
    this.factory = entityFactory;
    this.setTypeInstance((TType) entityFactory.getEntityInstance());
  }

  @SuppressWarnings(value = { "unchecked" }) protected TBuilder getBuilder() {
    return (TBuilder) this;
  }

  protected abstract void validateInstance() throws BillyValidationException, ValidationException;

  protected <T extends TType> void setTypeInstance(T instance) {
    this.typeInstance = instance;
  }

  public void clear() {
    this.typeInstance = (TType) this.factory.getEntityInstance();
  }

  @SuppressWarnings(value = { "unchecked" }) protected <T extends TType> T getTypeInstance() {
    return (T) this.typeInstance;
  }

  public TBuilder setUID(UID uid) {
    ((BaseEntity) this.typeInstance).setUID(uid);
    return this.getBuilder();
  }

  public TType build() throws BillyValidationException, ValidationException {
    this.validateInstance();
    return this.getTypeInstance();
  }
}