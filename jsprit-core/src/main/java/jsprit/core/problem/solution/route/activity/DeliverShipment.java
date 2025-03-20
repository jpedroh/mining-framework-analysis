package jsprit.core.problem.solution.route.activity;
import jsprit.core.problem.AbstractActivity;
import jsprit.core.problem.Capacity;
import jsprit.core.problem.Location;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Shipment;

public final class DeliverShipment extends AbstractActivity implements DeliveryActivity {
  private Shipment shipment;

  private double endTime;

  private double arrTime;

  private Capacity capacity;

  private double earliest = 0;

  private double latest = Double.MAX_VALUE;

  public DeliverShipment(Shipment shipment) {
    super();
    this.shipment = shipment;
    this.capacity = Capacity.invert(shipment.getSize());
  }

  @Deprecated public DeliverShipment(DeliverShipment deliveryShipmentActivity) {
    this.shipment = (Shipment) deliveryShipmentActivity.getJob();
    this.arrTime = deliveryShipmentActivity.getArrTime();
    this.endTime = deliveryShipmentActivity.getEndTime();
    this.capacity = deliveryShipmentActivity.getSize();
    setIndex(deliveryShipmentActivity.getIndex());
    this.earliest = deliveryShipmentActivity.getTheoreticalEarliestOperationStartTime();
    this.latest = deliveryShipmentActivity.getTheoreticalLatestOperationStartTime();
  }

  @Override public Job getJob() {
    return shipment;
  }

  @Override public void setTheoreticalEarliestOperationStartTime(double earliest) {
    this.earliest = earliest;
  }

  @Override public void setTheoreticalLatestOperationStartTime(double latest) {
    this.latest = latest;
  }

  @Override public String getName() {
    return "deliverShipment";
  }

  @Override public String getLocationId() {
    return shipment.getDeliveryLocation().getId();
  }

  @Override public Location getLocation() {
    return shipment.getDeliveryLocation();
  }

  @Override public double getTheoreticalEarliestOperationStartTime() {
    return earliest;
  }

  @Override public double getTheoreticalLatestOperationStartTime() {
    return latest;
  }

  @Override public double getOperationTime() {
    return shipment.getDeliveryServiceTime();
  }

  @Override public double getArrTime() {
    return arrTime;
  }

  @Override public double getEndTime() {
    return endTime;
  }

  @Override public void setArrTime(double arrTime) {
    this.arrTime = arrTime;
  }

  @Override public void setEndTime(double endTime) {
    this.endTime = endTime;
  }

  @Override public TourActivity duplicate() {
    return new DeliverShipment(this);
  }

  public String toString() {
    return "[type=" + getName() + "][locationId=" + getLocationId() + "][size=" + getSize().toString() + "][twStart=" + Activities.round(getTheoreticalEarliestOperationStartTime()) + "][twEnd=" + Activities.round(getTheoreticalLatestOperationStartTime()) + "]";
  }

  @Override public Capacity getSize() {
    return capacity;
  }
}