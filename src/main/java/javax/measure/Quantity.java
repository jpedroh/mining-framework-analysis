package javax.measure;

/**
 * Represents a quantitative property of a phenomenon, body, or substance, that
 * can be quantified by measurement. {@link javax.measure.quantity.Mass Mass},
 * time, distance, heat, and angular separation are among the familiar examples
 * of quantitative properties.
 * <p>
 * <code> {@literal Unit<Mass>} pound = ... {@literal Quantity<Length>} size = ... {@literal Sensor<Temperature>}<br>
 * thermometer = ... {@literal Vector3D<Speed>} aircraftSpeed = ... </code>
 * </p>
 *
 * @apiNote This interface places no restrictions on the mutability of
 *          implementations, however immutability is strongly recommended. All
 *          implementations must be {@link Comparable}.
 *
 * @param <Q>
 *            The type of the quantity.
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:martin.desruisseaux@geomatys.com">Martin
 *         Desruisseaux</a>
 * @author <a href="mailto:werner@uom.technology">Werner Keil</a>
 * @author <a href="mailto:otaviopolianasantana@gmail.com">Otavio Santana</a>
 * @see Unit
 * @see <a href="http://en.wikipedia.org/wiki/Quantity">Wikipedia: Quantity</a>
 * @see <a href="http://martinfowler.com/eaaDev/quantity.html">Martin Fowler -
 *      Quantity</a>
 * @version 1.0.2, April 13, 2018
 * @since 1.0
 */
public interface Quantity<Q extends Quantity<Q>> {
  /**
	 * Returns the sum of this {@code Quantity} with the one specified.
	 *
	 * @param augend
	 *            the {@code Quantity} to be added.
	 * @return {@code this + augend}.
	 */
  Quantity<Q> add(Quantity<Q> augend);

  /**
	 * Returns the difference between this {@code Quantity} and the one specified.
	 *
	 * @param subtrahend
	 *            the {@code Quantity} to be subtracted.
	 * @return <code>this - that</code>.
	 */
  Quantity<Q> subtract(Quantity<Q> subtrahend);

  /**
	 * Returns the product of this {@code Quantity} divided by the {@code Quantity}
	 * specified.
	 *
	 * @throws ClassCastException
	 *             if the type of an element in the specified operation is
	 *             incompatible with this quantity
	 *             (<a href="#optional-restrictions">optional</a>)
	 *
	 * @param divisor
	 *            the {@code Quantity} divisor.
	 * @return <code>this / that</code>.
	 */
  Quantity<?> divide(Quantity<?> divisor);

  /**
	 * Returns the product of this {@code Quantity} divided by the {@code Number}
	 * specified.
	 *
	 * @param divisor
	 *            the {@code Number} divisor.
	 * @return <code>this / that</code>.
	 */
  Quantity<Q> divide(Number divisor);

  /**
	 * Returns the product of this {@code Quantity} with the one specified.
	 *
	 * @throws ClassCastException
	 *             if the type of an element in the specified operation is
	 *             incompatible with this quantity
	 *             (<a href="#optional-restrictions">optional</a>)
	 *
	 * @param multiplier
	 *            the {@code Quantity} multiplier.
	 * @return <code>this * multiplier</code>.
	 */
  Quantity<?> multiply(Quantity<?> multiplier);

  /**
	 * Returns the product of this {@code Quantity} with the {@code Number} value
	 * specified.
	 *
	 * @param multiplier
	 *            the {@code Number} multiplier.
	 * @return <code>this * multiplier</code>.
	 */
  Quantity<Q> multiply(Number multiplier);

  /**
	 * Returns a {@code Quantity} whose unit is {@code unit.inverse()}.
	 *
	 * @return {@code Quantity with this.getUnit().inverse()}.
	 */
  Quantity<?> inverse();

  /**
	 * Returns this {@code Quantity} converted into another (compatible)
	 * {@code Unit}.
	 *
	 * @param unit
	 *            the {@code Unit} to convert to.
	 * @return the converted result.
	 */
  Quantity<Q> to(Unit<Q> unit);

  /**
	 * Casts this quantity to a parameterized unit of specified nature or throw a
	 * <code>ClassCastException</code> if the dimension of the specified quantity
	 * and this measure unit's dimension do not match. For example:
	 * <p>
	 * <code>
	 *     {@literal Quantity<Length>} length = Quantities.getQuantity("2 km").asType(Length.class);
	 * </code> or <code>
	 *     {@literal Quantity<Speed>} C = length.multiply(299792458).divide(second).asType(Speed.class);
	 * </code>
	 * </p>
	 *
	 * @param <T>
	 *            The type of the quantity.
	 * @param type
	 *            the quantity class identifying the nature of the quantity.
	 * @return this quantity parameterized with the specified type.
	 * @throws ClassCastException
	 *             if the dimension of this unit is different from the specified
	 *             quantity dimension.
	 * @throws UnsupportedOperationException
	 *             if the specified quantity class does not have a SI unit for the
	 *             quantity.
	 * @see Unit#asType(Class)
	 */
  <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException;

  /**
	 * Returns the value of this {@code Quantity}.
	 *
	 * @return a value.
	 */
  Number getValue();

  /**
	 * Returns the unit of this {@code Quantity}.
	 *
	 * @return the unit (shall not be {@code null}).
	 */
  Unit<Q> getUnit();

  /**
	 * Convenient method equivalent to {@link #to(javax.measure.Unit)
	 * to(getUnit().toSystemUnit())}.
	 *
	 * @return this quantity or a new quantity equivalent to this quantity stated in
	 *         SI units.
	 * @throws ArithmeticException
	 *             if the result is inexact and the quotient has a non-terminating
	 *             decimal expansion.
	 */
  default Quantity<Q> toSystemUnit() {
    return to(getUnit().getSystemUnit());
  }
}