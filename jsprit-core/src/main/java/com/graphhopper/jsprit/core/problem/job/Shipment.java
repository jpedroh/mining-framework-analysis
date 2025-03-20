package com.graphhopper.jsprit.core.problem.job;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliveryActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindowsImpl;

/**
 * Shipment is an implementation of Job and consists of a pickup and a delivery
 * of something.
 * <p>
 * <p>
 * It distinguishes itself from {@link Service} as two locations are involved a
 * pickup where usually something is loaded to the transport unit and a delivery
 * where something is unloaded.
 * <p>
 * <p>
 * By default serviceTimes of both pickup and delivery is 0.0 and timeWindows of
 * both is [0.0, Double.MAX_VALUE],
 * <p>
 * <p>
 * A shipment can be built with a builder. You can get an instance of the
 * builder by coding <code>Shipment.Builder.newInstance(...)</code>. This way
 * you can specify the shipment. Once you build the shipment, it is immutable,
 * i.e. fields/attributes cannot be changed anymore and you can only 'get' the
 * specified values.
 * <p>
 * <p>
 * Note that two shipments are equal if they have the same id.
 *
 * @author schroeder
 */
public class Shipment extends AbstractJob {
  public static final String DELIVERY_ACTIVITY_NAME = "deliverShipment";

  public static final class Builder extends BuilderBase<Shipment, Builder> {
    public static Builder newInstance(String id) {
      return new Builder(id);
    }

    public Builder(String id) {
      super(id);
    }

    @Override protected Shipment createInstance() {
      return new Shipment(this);
    }


<<<<<<< Unknown file: This is a bug in JDime.
=======
    /**
         * Set priority to shipment. Only 1 (high) to 10 (low) are allowed.
         * <p>
         * Default is 2 = medium.
         *
         * @param priority
         * @return builder
         */
    public Builder setPriority(int priority) {
      if (priority < 1 || priority > 10) {
        throw new IllegalArgumentException("incorrect priority. only 1 (very high) to 10 (very low) are allowed");
      }
      this.priority = priority;
      return this;
    }
>>>>>>> /usr/src/app/output/jsprit/jsprit/9fe0b3315e0376479ad9be196dc988ad75af06ac/jsprit-core/src/main/java/com/graphhopper/jsprit/core/problem/job/Shipment.java/right.java
  }

  public static final String PICKUP_ACTIVITY_NAME = "pickupShipment";

  public static abstract class BuilderBase<T extends Shipment, B extends BuilderBase<T, B>> extends JobBuilder<T, B> {
    private double pickupServiceTime = 0.0;

    private double deliveryServiceTime = 0.0;

    private Location pickupLocation;

    private Location deliveryLocation;

    protected TimeWindowsImpl deliveryTimeWindows = new TimeWindowsImpl();

    private TimeWindowsImpl pickupTimeWindows = new TimeWindowsImpl();

    /**
         * Returns new instance of this builder.
         *
         * @param id the id of the shipment which must be a unique identifier
         *           among all jobs
         * @return the builder
         */
    public BuilderBase(String id) {
      super(id);
      pickupTimeWindows = new TimeWindowsImpl();
      deliveryTimeWindows = new TimeWindowsImpl();
    }

    /**
         * Sets pickup location.
         *
         * @param pickupLocation pickup location
         * @return builder
         */
    @SuppressWarnings(value = { "unchecked" }) public B setPickupLocation(Location pickupLocation) {
      this.pickupLocation = pickupLocation;
      return (B) this;
    }

    /**
         * Sets pickupServiceTime.
         * <p>
         * <p>
         * ServiceTime is intended to be the time the implied activity takes at
         * the pickup-location.
         *
         * @param serviceTime the service time / duration the pickup of the associated
         *                    shipment takes
         * @return builder
         * @throws IllegalArgumentException if servicTime < 0.0
         */
    @SuppressWarnings(value = { "unchecked" }) public B setPickupServiceTime(double serviceTime) {
      if (serviceTime < 0.0) {
        throw new IllegalArgumentException("serviceTime must not be < 0.0");
      }
      pickupServiceTime = serviceTime;
      return (B) this;
    }

    /**
         * Sets the timeWindow for the pickup, i.e. the time-period in which a
         * pickup operation is allowed to START.
         * <p>
         * <p>
         * By default timeWindow is [0.0, Double.MAX_VALUE}
         *
         * @param timeWindow the time window within the pickup operation/activity can
         *                   START
         * @return builder
         * @throws IllegalArgumentException if timeWindow is null
         */
    @SuppressWarnings(value = { "unchecked" }) public B setPickupTimeWindow(TimeWindow timeWindow) {
      if (timeWindow == null) {
        throw new IllegalArgumentException("pickup time-window must not be null");
      }
      pickupTimeWindows.clear();
      pickupTimeWindows.add(timeWindow);
      return (B) this;
    }

    /**
         * Sets delivery location.
         *
         * @param deliveryLocation delivery location
         * @return builder
         */
    @SuppressWarnings(value = { "unchecked" }) public B setDeliveryLocation(Location deliveryLocation) {
      this.deliveryLocation = deliveryLocation;
      return (B) this;
    }

    /**
         * Sets the delivery service-time.
         * <p>
         * <p>
         * ServiceTime is intended to be the time the implied activity takes at
         * the delivery-location.
         *
         * @param deliveryServiceTime the service time / duration of shipment's delivery
         * @return builder
         * @throws IllegalArgumentException if serviceTime < 0.0
         */
    @SuppressWarnings(value = { "unchecked" }) public B setDeliveryServiceTime(double deliveryServiceTime) {
      if (deliveryServiceTime < 0.0) {
        throw new IllegalArgumentException("deliveryServiceTime must not be < 0.0");
      }
      this.deliveryServiceTime = deliveryServiceTime;
      return (B) this;
    }

    /**
         * Sets the timeWindow for the delivery, i.e. the time-period in which a
         * delivery operation is allowed to start.
         * <p>
         * <p>
         * By default timeWindow is [0.0, Double.MAX_VALUE}
         *
         * @param timeWindow the time window within the associated delivery is allowed
         *                   to START
         * @return builder
         * @throws IllegalArgumentException if timeWindow is null
         */
    @SuppressWarnings(value = { "unchecked" }) public B setDeliveryTimeWindow(TimeWindow timeWindow) {
      if (timeWindow == null) {
        throw new IllegalArgumentException("delivery time-window must not be null");
      }
      deliveryTimeWindows.clear();
      deliveryTimeWindows.add(timeWindow);
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addDeliveryTimeWindow(TimeWindow timeWindow) {
      if (timeWindow == null) {
        throw new IllegalArgumentException("time-window arg must not be null");
      }
      deliveryTimeWindows.add(timeWindow);
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addDeliveryTimeWindow(double earliest, double latest) {
      addDeliveryTimeWindow(TimeWindow.newInstance(earliest, latest));
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addPickupTimeWindow(TimeWindow timeWindow) {
      if (timeWindow == null) {
        throw new IllegalArgumentException("time-window arg must not be null");
      }
      pickupTimeWindows.add(timeWindow);
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addPickupTimeWindow(double earliest, double latest) {
      addPickupTimeWindow(TimeWindow.newInstance(earliest, latest));
      return (B) this;
    }

    @Override protected void validate() {
      if (pickupLocation == null) {
        throw new IllegalArgumentException("pickup location is missing");
      }
      if (deliveryLocation == null) {
        throw new IllegalArgumentException("delivery location is missing");
      }
      if (pickupTimeWindows.isEmpty()) {
        pickupTimeWindows.add(TimeWindow.ETERNITY);
      }
      if (deliveryTimeWindows.isEmpty()) {
        deliveryTimeWindows.add(TimeWindow.ETERNITY);
      }
    }

    public double getPickupServiceTime() {
      return pickupServiceTime;
    }

    public double getDeliveryServiceTime() {
      return deliveryServiceTime;
    }

    public Location getPickupLocation() {
      return pickupLocation;
    }

    public Location getDeliveryLocation() {
      return deliveryLocation;
    }

    public TimeWindowsImpl getDeliveryTimeWindows() {
      return deliveryTimeWindows;
    }

    public TimeWindowsImpl getPickupTimeWindows() {
      return pickupTimeWindows;
    }
  }

  Shipment(BuilderBase<? extends Shipment, ?> builder) {
    super(builder);
  }

  @Override protected void createActivities(JobBuilder<?, ?> builder) {
    Builder shipmentBuilder = (Builder) builder;
    JobActivityList list = new SequentialJobActivityList(this);
    list.addActivity(new PickupActivity(this, PICKUP_ACTIVITY_NAME, shipmentBuilder.getPickupLocation(), shipmentBuilder.getPickupServiceTime(), shipmentBuilder.getCapacity(), shipmentBuilder.getPickupTimeWindows().getTimeWindows()));
    list.addActivity(new DeliveryActivity(this, DELIVERY_ACTIVITY_NAME, shipmentBuilder.getDeliveryLocation(), shipmentBuilder.getDeliveryServiceTime(), shipmentBuilder.getCapacity().invert(), shipmentBuilder.getDeliveryTimeWindows().getTimeWindows()));
    setActivities(list);
  }

  public PickupActivity getPickupActivity() {
    return (PickupActivity) getActivityList().findByType(PICKUP_ACTIVITY_NAME).get();
  }

  public DeliveryActivity getDeliveryActivity() {
    return (DeliveryActivity) getActivityList().findByType(DELIVERY_ACTIVITY_NAME).get();
  }

  @Override @Deprecated public SizeDimension getSize() {
    return getPickupActivity().getLoadChange();
  }
}