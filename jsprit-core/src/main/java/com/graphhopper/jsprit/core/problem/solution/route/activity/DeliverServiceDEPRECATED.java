package com.graphhopper.jsprit.core.problem.solution.route.activity;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Service.BuilderBase;

public final class DeliverServiceDEPRECATED extends DeliveryActivityNEW {
  public DeliverServiceDEPRECATED(Service service, BuilderBase<? extends Service, ?> builder) {
    super(service, builder.getType(), builder.getLocation(), builder.getServiceTime(), builder.getCapacity().invert(), builder.getTimeWindows().getTimeWindows());
  }

  public DeliverServiceDEPRECATED(Delivery delivery) {
    super(delivery, delivery.getType(), delivery.getLocation(), delivery.getServiceDuration(), delivery.getSize().invert(), delivery.getServiceTimeWindows());
  }

  public DeliverServiceDEPRECATED(DeliverServiceDEPRECATED sourceActivity) {
    super(sourceActivity);
  }

  @Override public Delivery getJob() {
    return (Delivery) super.getJob();
  }
}