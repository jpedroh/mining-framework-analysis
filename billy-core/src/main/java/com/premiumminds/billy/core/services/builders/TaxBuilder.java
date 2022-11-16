/*
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy core.
 *
 * billy core is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy core is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy core. If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.core.services.builders;

import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.StringID;
import com.premiumminds.billy.core.services.entities.Context;
import com.premiumminds.billy.core.services.entities.Tax;
import com.premiumminds.billy.core.services.entities.Tax.TaxRateType;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public interface TaxBuilder<TBuilder extends TaxBuilder<TBuilder, TTax>, TTax extends Tax> extends Builder<TTax> {

    public TBuilder setContextUID(StringID<Context> uid);

    public TBuilder setDesignation(String designation);

    public TBuilder setDescription(String description);

    public TBuilder setCode(String code);

    public TBuilder setValidFrom(Date from);

    public TBuilder setValidTo(Date to);

    public TBuilder setTaxRate(TaxRateType rateType, BigDecimal amount);

    public TBuilder setCurrency(Currency currency);

    public TBuilder setValue(BigDecimal value);
}
