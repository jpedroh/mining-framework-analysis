package com.premiumminds.billy.portugal.services.builders;
import com.premiumminds.billy.core.services.builders.ShippingPointBuilder;
import com.premiumminds.billy.portugal.services.entities.PTShippingPoint;

public interface PTShippingPointBuilder<TBuilder extends PTShippingPointBuilder<TBuilder, TShippingPoint>, TShippingPoint extends PTShippingPoint> extends ShippingPointBuilder<TBuilder, TShippingPoint> {
}