package jsprit.core.problem.solution.route.activity;
import jsprit.core.problem.Location;
import jsprit.core.problem.job.Service;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiceActivityTest {
  private Service service;

  private ServiceActivity serviceActivity;

  @Before public void doBefore() {
    service = Service.Builder.newInstance("service").setLocation(Location.newInstance("loc")).setTimeWindow(TimeWindow.newInstance(1., 2.)).addSizeDimension(0, 10).addSizeDimension(1, 100).addSizeDimension(2, 1000).build();
    serviceActivity = ServiceActivity.newInstance(service);
    serviceActivity.setTheoreticalEarliestOperationStartTime(service.getTimeWindow().getStart());
    serviceActivity.setTheoreticalLatestOperationStartTime(service.getTimeWindow().getEnd());
  }

  @Test public void whenCallingCapacity_itShouldReturnCorrectCapacity() {
    assertEquals(10, serviceActivity.getSize().get(0));
    assertEquals(100, serviceActivity.getSize().get(1));
    assertEquals(1000, serviceActivity.getSize().get(2));
  }

  @Test public void whenStartIsIniWithEarliestStart_itShouldBeSetCorrectly() {
    assertEquals(1., serviceActivity.getTheoreticalEarliestOperationStartTime(), 0.01);
  }

  @Test public void whenStartIsIniWithLatestStart_itShouldBeSetCorrectly() {
    assertEquals(2., serviceActivity.getTheoreticalLatestOperationStartTime(), 0.01);
  }

  @Test public void whenSettingArrTime_itShouldBeSetCorrectly() {
    serviceActivity.setArrTime(4.0);
    assertEquals(4., serviceActivity.getArrTime(), 0.01);
  }

  @Test public void whenSettingEndTime_itShouldBeSetCorrectly() {
    serviceActivity.setEndTime(5.0);
    assertEquals(5., serviceActivity.getEndTime(), 0.01);
  }

  @Test public void whenIniLocationId_itShouldBeSetCorrectly() {
    assertEquals("loc", serviceActivity.getLocation().getId());
  }

  @Test public void whenCopyingStart_itShouldBeDoneCorrectly() {
    ServiceActivity copy = (ServiceActivity) serviceActivity.duplicate();
    assertEquals(1., copy.getTheoreticalEarliestOperationStartTime(), 0.01);
    assertEquals(2., copy.getTheoreticalLatestOperationStartTime(), 0.01);
    assertEquals("loc", copy.getLocation().getId());
    assertTrue(copy != serviceActivity);
  }

  @Test public void whenTwoDeliveriesHaveTheSameUnderlyingJob_theyAreEqual() {
    Service s1 = Service.Builder.newInstance("s").setLocation(Location.newInstance("loc")).build();
    Service s2 = Service.Builder.newInstance("s").setLocation(Location.newInstance("loc")).build();
    ServiceActivity d1 = ServiceActivity.newInstance(s1);
    ServiceActivity d2 = ServiceActivity.newInstance(s2);
    assertTrue(d1.equals(d2));
  }

  @Test public void whenTwoDeliveriesHaveTheDifferentUnderlyingJob_theyAreNotEqual() {
    Service s1 = Service.Builder.newInstance("s").setLocation(Location.newInstance("loc")).build();
    Service s2 = Service.Builder.newInstance("s1").setLocation(Location.newInstance("loc")).build();
    ServiceActivity d1 = ServiceActivity.newInstance(s1);
    ServiceActivity d2 = ServiceActivity.newInstance(s2);
    assertFalse(d1.equals(d2));
  }
}