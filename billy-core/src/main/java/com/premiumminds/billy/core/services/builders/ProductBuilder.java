package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Product;
import com.premiumminds.billy.core.services.entities.Product.ProductType;

public interface ProductBuilder<TBuilder extends ProductBuilder<TBuilder, TProduct>, TProduct extends Product> extends Builder<TProduct> {
  public TBuilder setProductCode(String code);

  public TBuilder setProductGroup(String group);

  public TBuilder setDescription(String description);

  public TBuilder setType(ProductType type);

  public TBuilder setCommodityCode(String code);

  public TBuilder setNumberCode(String code);

  public TBuilder setValuationMethod(String method);

  public TBuilder setUnitOfMeasure(String unit);

  public TBuilder addTaxUID(UID tax);
}