package pythagoras.f;
import java.util.NoSuchElementException;

/**
 * Provides most of the implementation of {@link IRectangle}, obtaining only the location and
 * dimensions from the derived class.
 */
public abstract class AbstractRectangle extends RectangularShape implements IRectangle {
  @Override public Point getLocation() {
    return getLocation(new Point());
  }

  @Override public Point getLocation(Point target) {
    target.setLocation(getX(), getY());
    return target;
  }

  @Override public Dimension getSize() {
    return getSize(new Dimension());
  }

  @Override public Dimension getSize(Dimension target) {
    target.setSize(getWidth(), getHeight());
    return target;
  }

  @Override public Rectangle intersection(float rx, float ry, float rw, float rh) {
    float x1 = Math.max(getX(), rx);
    float y1 = Math.max(getY(), ry);
    float x2 = Math.min(getMaxX(), rx + rw);
    float y2 = Math.min(getMaxY(), ry + rh);
    return new Rectangle(x1, y1, x2 - x1, y2 - y1);
  }

  @Override public Rectangle intersection(IRectangle r) {
    return intersection(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  @Override public Rectangle union(IRectangle r) {
    Rectangle rect = new Rectangle(this);
    rect.add(r);
    return rect;
  }

  @Override public boolean intersectsLine(float x1, float y1, float x2, float y2) {
    return Lines.lineIntersectsRect(x1, y1, x2, y2, getX(), getY(), getWidth(), getHeight());
  }

  @Override public boolean intersectsLine(ILine l) {
    return intersectsLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
  }

  @Override public int outcode(float px, float py) {
    int code = 0;
    if (getWidth() <= 0) {
      code |= OUT_LEFT | OUT_RIGHT;
    } else {
      if (px < getX()) {
        code |= OUT_LEFT;
      } else {
        if (px > getMaxX()) {
          code |= OUT_RIGHT;
        }
      }
    }
    if (getHeight() <= 0) {
      code |= OUT_TOP | OUT_BOTTOM;
    } else {
      if (py < getY()) {
        code |= OUT_TOP;
      } else {
        if (py > getMaxY()) {
          code |= OUT_BOTTOM;
        }
      }
    }
    return code;
  }

  @Override public int outcode(IPoint p) {
    return outcode(p.getX(), p.getY());
  }

  @Override public Rectangle clone() {
    return new Rectangle(this);
  }

  @Override public boolean contains(float px, float py) {
    if (isEmpty()) {
      return false;
    }
    float x = getX(), y = getY();
    if (px < x || py < y) {
      return false;
    }
    px -= x;
    py -= y;
    return px < getWidth() && py < getHeight();
  }

  @Override public boolean contains(float rx, float ry, float rw, float rh) {
    if (isEmpty()) {
      return false;
    }
    float x1 = getX(), y1 = getY(), x2 = x1 + getWidth(), y2 = y1 + getHeight();
    return (x1 <= rx) && (rx + rw <= x2) && (y1 <= ry) && (ry + rh <= y2);
  }

  @Override public boolean intersects(float rx, float ry, float rw, float rh) {
    if (isEmpty()) {
      return false;
    }
    float x1 = getX(), y1 = getY(), x2 = x1 + getWidth(), y2 = y1 + getHeight();
    return (rx + rw > x1) && (rx < x2) && (ry + rh > y1) && (ry < y2);
  }

  @Override public PathIterator getPathIterator(AffineTransform t) {
    return new Iterator(this, t);
  }

  @Override public PathIterator getPathIterator(AffineTransform t, float flatness) {
    return new Iterator(this, t);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractRectangle) {
      AbstractRectangle r = (AbstractRectangle) obj;
      return r.getX() == getX() && r.getY() == getY() && r.getWidth() == getWidth() && r.getHeight() == getHeight();
    }
    return false;
  }

  @Override public int hashCode() {
    return Float.floatToIntBits(getX()) ^ Float.floatToIntBits(getY()) ^ Float.floatToIntBits(getWidth()) ^ Float.floatToIntBits(getHeight());
  }

  @Override public String toString() {
    return Dimensions.dimenToString(getWidth(), getHeight()) + Points.pointToString(getX(), getY());
  }

  protected static class Iterator implements PathIterator {
    private float x, y, width, height;

    private AffineTransform t;

    /** The current segmenet index. */
    private int index;

    Iterator(IRectangle r, AffineTransform at) {
      this.x = r.getX();
      this.y = r.getY();
      this.width = r.getWidth();
      this.height = r.getHeight();
      this.t = at;
      if (width < 0f || height < 0f) {
        index = 6;
      }
    }

    @Override public int getWindingRule() {
      return WIND_NON_ZERO;
    }

    @Override public boolean isDone() {
      return index > 5;
    }

    @Override public void next() {
      index++;
    }

    @Override public int currentSegment(float[] coords) {
      if (isDone()) {
        throw new NoSuchElementException("Iterator out of bounds");
      }
      if (index == 5) {
        return SEG_CLOSE;
      }
      int type;
      if (index == 0) {
        type = SEG_MOVETO;
        coords[0] = x;
        coords[1] = y;
      } else {
        type = SEG_LINETO;
        switch (index) {
          case 1:
          coords[0] = x + width;
          coords[1] = y;
          break;
          case 2:
          coords[0] = x + width;
          coords[1] = y + height;
          break;
          case 3:
          coords[0] = x;
          coords[1] = y + height;
          break;
          case 4:
          coords[0] = x;
          coords[1] = y;
          break;
        }
      }
      if (t != null) {
        t.transform(coords, 0, coords, 0, 1);
      }
      return type;
    }
  }
}