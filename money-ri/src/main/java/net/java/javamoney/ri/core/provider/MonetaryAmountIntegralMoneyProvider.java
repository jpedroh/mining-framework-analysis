/*
 *  Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Contributors:
 *    Anatole Tresch - initial implementation.
 */
package net.java.javamoney.ri.core.provider;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.provider.MonetaryAmountProvider;

import net.java.javamoney.ri.cdi.qualifiers.Amount;
import net.java.javamoney.ri.core.IntegralMoney;

@Amount
public class MonetaryAmountIntegralMoneyProvider implements MonetaryAmountProvider {

	public Class<?> getNumberClass() {
		return IntegralMoney.getNumberClass();
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, Number number) {
		return IntegralMoney.valueOf(currency, number);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, byte value) {
		return IntegralMoney.valueOf(currency, value);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, short value) {
		return IntegralMoney.valueOf(currency, value);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, int value) {
		return IntegralMoney.valueOf(currency, value);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, float value) {
		return IntegralMoney.valueOf(currency, value);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, double value) {
		return IntegralMoney.valueOf(currency, value);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, long value) {
		return IntegralMoney.valueOf(currency, value);
	}

	@Override
	public MonetaryAmount get(CurrencyUnit currency, long major, long minor) {
		return IntegralMoney.valueOf(currency, new BigDecimal(major + '.' + minor));
	}

	@Override
	public MonetaryAmount zero(CurrencyUnit currency) {
		return IntegralMoney.zero(currency);
	}

}
