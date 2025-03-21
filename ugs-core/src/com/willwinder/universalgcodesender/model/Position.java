package com.willwinder.universalgcodesender.model;
import com.willwinder.universalgcodesender.model.UnitUtils.Units;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.vecmath.Point3d;
import java.util.Objects;
import javax.vecmath.Tuple3d;

public class Position extends Point3d {
  public static final Position ZERO = new Position(0, 0, 0, Units.MM);

  private final Units units;

  public Position(Units units) {
    this.units = units;
  }

  public Position(Position other) {
    this(other.x, other.y, other.z, other.units);
  }

  public Position(double x, double y, double z, Units units) {
    super(x, y, z);
    this.units = units;
  }

  @Override public boolean equals(final Object other) {
    if (other instanceof Position) {
      return equals((Position) other);
    }
    return false;
  }

  @Override public boolean equals(final Tuple3d o) {
    if (o instanceof Position) {
      return super.equals(o) && units == ((Position) o).units;
    }
    return super.equals(o);
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    int hash = 3;
    hash = 83 * hash + Objects.hashCode(this.units);
    return hash;
  }

  public Units getUnits() {
    return units;
  }

  public Position getPositionIn(Units units) {
    double scale = UnitUtils.scaleUnits(this.units, units);
    return new Position(x * scale, y * scale, z * scale, units);
  }

  public double get(Axis axis) {
    switch (axis) {
      case X:
      return getX();
      case Y:
      return getY();
      case Z:
      return getZ();
      default:
      return 0;
    }
  }

  public void set(Axis axis, double value) {
    switch (axis) {
      case X:
      setX(value);
      break;
      case Y:
      setY(value);
      break;
      case Z:
      setZ(value);
      break;
      default:
    }
  }

  /**
     * Rotates this point around the center with the given angle in radians and returns a new position
     *
     * @param center the XY position to rotate around
     * @param radians the radians to rotate clock wise
     * @return a new rotated position
     */
  public Position rotate(Position center, double radians) {
    double cosA = Math.cos(radians);
    double sinA = Math.sin(radians);
    return new Position(center.x + (cosA * (x - center.x) + sinA * (y - center.y)), center.y + (-sinA * (x - center.x) + cosA * (y - center.y)), z, units);
  }
}