package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.ProductBuilder;
import com.premiumminds.billy.spain.services.entities.ESProduct;

public interface ESProductBuilder<TBuilder extends ESProductBuilder<TBuilder, TProduct>, TProduct extends ESProduct> extends ProductBuilder<TBuilder, TProduct> {
}