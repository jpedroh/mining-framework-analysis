package com.ning.billing.recurly.model;
import javax.xml.bind.annotation.XmlElement;

public class SubscriptionUpdate extends AbstractSubscription {
  public static enum Timeframe {
    now,
    renewal
  }

  @XmlElement private Timeframe timeframe;

  public Timeframe getTimeframe() {
    return timeframe;
  }

  public void setTimeframe(final Timeframe timeframe) {
    this.timeframe = timeframe;
  }

  @Override public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SubscriptionUpdate)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    final SubscriptionUpdate that = (SubscriptionUpdate) o;
    if (timeframe != that.timeframe) {
      return false;
    }
    return true;
  }

  @Override public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (timeframe != null ? timeframe.hashCode() : 0);
    return result;
  }
}