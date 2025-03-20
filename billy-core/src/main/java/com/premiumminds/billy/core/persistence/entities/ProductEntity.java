package com.premiumminds.billy.core.persistence.entities;
import java.util.List;
import com.premiumminds.billy.core.services.entities.Product;
import com.premiumminds.billy.core.services.entities.Tax;

public interface ProductEntity extends Product, BaseEntity {
  public void setProductCode(String code);

  public void setProductGroup(String group);

  public void setDescription(String description);

  public void setType(ProductType type);

  public void setCommodityCode(String code);

  public void setNumberCode(String code);

  public void setValuationMethod(String method);

  public void setUnitOfMeasure(String unit);

  @Override public <T extends Tax> List<T> getTaxes();
}