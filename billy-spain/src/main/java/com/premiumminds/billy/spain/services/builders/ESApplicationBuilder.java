package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.ApplicationBuilder;
import com.premiumminds.billy.spain.services.entities.ESApplication;

public interface ESApplicationBuilder<TBuilder extends ESApplicationBuilder<TBuilder, TApplication>, TApplication extends ESApplication> extends ApplicationBuilder<TBuilder, TApplication> {
}