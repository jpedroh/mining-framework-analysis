package com.premiumminds.billy.spain.services.builders;
import com.premiumminds.billy.core.services.builders.ContactBuilder;
import com.premiumminds.billy.spain.services.entities.ESContact;

public interface ESContactBuilder<TBuilder extends ESContactBuilder<TBuilder, TContact>, TContact extends ESContact> extends ContactBuilder<TBuilder, TContact> {
}