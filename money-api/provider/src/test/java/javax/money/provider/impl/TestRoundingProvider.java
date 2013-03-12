/*
 *  Copyright (c) 2012, 2013, Werner Keil, Credit Suisse (Anatole Tresch).
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
 * 
 * Contributors:
 *    Anatole Tresch - initial version.
 */
package javax.money.provider.impl;

import java.util.Enumeration;

import javax.money.CurrencyUnit;
import javax.money.Rounding;
import javax.money.provider.RoundingProvider;

/**
 * Empty pseudo implementation for testing only.
 * @author Anatole Tresch
 *
 */
public class TestRoundingProvider implements RoundingProvider {

	@Override
	public Rounding getRounding(CurrencyUnit currency) {
		// empty implementation
		return null;
	}

	@Override
	public Rounding getRounding(CurrencyUnit currency, Long timestamp) {
		// empty implementation
		return null;
	}

	@Override
	public Rounding getRounding(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getRoundingIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRoundingDefined(String id) {
		// TODO Auto-generated method stub
		return false;
	}

}
