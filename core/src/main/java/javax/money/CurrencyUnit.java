/*
<<<<<<< LEFT
 * Copyright (c) 2012, Credit Suisse (Anatole Tresch), Werner Keil
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
=======
 * Copyright (c) 2012-2013, Credit Suisse
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-354 nor the names of its contributors
>>>>>>> RIGHT
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.money;

import java.util.Currency;
import java.util.ServiceLoader;
import javax.money.spi.CurrencyUnitProvider;


/**
 * * A unit of currency.
 * <p>
 * This class represents a unit of currency such as the British Pound, Euro or
 * US Dollar, BitCoins or other.
 * <p>
 * The set of loaded currencies is provided by an instances of
 * {@link CurrencyUnitProvider}. The providers used are registered using the
 * {@link ServiceLoader} feature.
 *
 * @author Werner Keil
 * @author Stephen Colebourne
 * @author Anatole Tresch
 */
public interface CurrencyUnit extends Comparable<CurrencyUnit> {
    /**
     * Defines the name space for the currency code. If the CurrencyUnit is an
     * instance of {@link java.util.Currency} this method returns 'ISO-4217',
     * whereas for other currency schemes, e.g. virtual currencies or internal
     * legacy currencies different values are possible.
     *
     * @return the three letter ISO-4217 or equivalent currency code, never null
     */
    public abstract String getNamespace();

    /**
     * Gets the currency code, the effective code depends on the currency and
     * the name space. It is possible that the two currency may have the same
     * code, but different name spaces.
     * <p>
     * Each currency is uniquely identified within its name space by this code.
     *
     * @return the display name of this currency for the default locale
     */
    public abstract String getCurrencyCode();

    /**
     * Gets the ISO-4217 numeric currency code.
     * <p>
     * The numeric code is an alternative to the standard three letter code.
     * 
     * @return the numeric currency code
     */
    public int getNumericCode();

    /**
     * Gets the number of fractional digits typically used by this currency.
     * <p>
     * Different currencies have different numbers of fractional digits by
     * default. * For example, 'GBP' has 2 fractional digits, but 'JPY' has
     * zero. * Pseudo-currencies are indicated by -1. *
     * <p>
     * This method matches the API of {@link Currency}. * The alternative method
     * {@link #getDecimalPlaces()} may be more useful. * *
     *
     * @return the decimal places, from 0 to 9 (normally 0, 2 or 3)
     */
    public abstract int getDefaultFractionDigits();

    /**
     * Gets the increment used for rounding, along with
     * {@link #getDefaultFractionDigits()} rounding is defined.
     *
     * @return the currency code, never null
     */
    /**
     * Checks if this is a virtual currency, such as BitCoins or Linden Dollars.
     *
     * @return the fractional digits, from 0 to 9 (normally 0, 2 or 3), or -1 for pseudo-currencies
     */
    public abstract boolean isVirtual();

    /**
     * Get the timestamp from when this currency instance is valid from.<br/>
     * This is useful for historic currencies.
     *
     * @return the JDK currency instance, never null
     */
    public abstract long getValidFrom();

    /**
     * Get the timestamp until when this currency instance is valid from.<br/>
     * This is useful for historic currencies.
     *
     * @param locale
     * 		the locale to get the symbol for, not null
     */
    public abstract long getValidUntil();
}