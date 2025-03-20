package com.graphhopper.jsprit.core.algorithm.recreate;
import static org.junit.Assert.assertEquals;
import com.graphhopper.jsprit.core.algorithm.state.InternalStates;
import static org.mockito.Mockito.mock;
import com.graphhopper.jsprit.core.problem.job.Job;
import static org.mockito.Mockito.when;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import org.junit.Before;
import com.graphhopper.jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;
import org.junit.Test;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;

public class JobInsertionConsideringFixCostsCalculatorTest {
  private JobInsertionConsideringFixCostsCalculator calc;

  private Vehicle oVehicle;

  private Vehicle nVehicle;

  private Job job;

  private VehicleRoute route;

  private RouteAndActivityStateGetter stateGetter;

  @Before public void doBefore() {
    JobInsertionCostsCalculator jobInsertionCosts = mock(JobInsertionCostsCalculator.class);
    job = mock(Job.class);
    when(job.getSize()).thenReturn(SizeDimension.Builder.newInstance().addDimension(0, 50).build());
    oVehicle = mock(Vehicle.class);
    VehicleType oType = VehicleTypeImpl.Builder.newInstance("otype").addCapacityDimension(0, 50).setFixedCost(50.0).build();
    when(oVehicle.getType()).thenReturn(oType);
    nVehicle = mock(Vehicle.class);
    VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 100).setFixedCost(100.0).build();
    when(nVehicle.getType()).thenReturn(type);
    InsertionData iData = new InsertionData(0.0, 1, 1, nVehicle, null);
    route = mock(VehicleRoute.class);
    when(jobInsertionCosts.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE)).thenReturn(iData);
    stateGetter = mock(RouteAndActivityStateGetter.class);
    when(stateGetter.getRouteState(route, InternalStates.MAXLOAD, SizeDimension.class)).thenReturn(SizeDimension.Builder.newInstance().build());
    calc = new JobInsertionConsideringFixCostsCalculator(jobInsertionCosts, stateGetter);
  }

  @Test public void whenOldVehicleIsNullAndSolutionComplete_itShouldReturnFixedCostsOfNewVehicle() {
    calc.setSolutionCompletenessRatio(1.0);
    calc.setWeightOfFixCost(1.0);
    assertEquals(100., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionIs0PercentComplete_itShouldReturnNoFixedCosts() {
    calc.setSolutionCompletenessRatio(0.0);
    calc.setWeightOfFixCost(1.0);
    assertEquals(0., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionIs50PercentComplete_itShouldReturnAvgOfRelFixedAndAbsFixedCostOfNewVehicle() {
    calc.setSolutionCompletenessRatio(0.5);
    calc.setWeightOfFixCost(1.0);
    assertEquals(37.5, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionIs75PercentComplete_itShouldReturnAvgOfRelFixedAndAbsFixedCostOfNewVehicle() {
    calc.setSolutionCompletenessRatio(0.75);
    calc.setWeightOfFixCost(1.0);
    assertEquals(65.625, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionCompleteAndWeightIs05_itShouldReturnHalfOfFixedCostsOfNewVehicle() {
    calc.setSolutionCompletenessRatio(1.0);
    calc.setWeightOfFixCost(.5);
    assertEquals(50., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionIs0PercentCompleteAndWeightIs05_itShouldReturnHalfOfNoFixedCosts() {
    calc.setSolutionCompletenessRatio(0.0);
    calc.setWeightOfFixCost(.5);
    assertEquals(0., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionIs50PercentCompleteAndWeightIs05_itShouldReturnHalfOfAvgOfRelFixedAndAbsFixedCostOfNewVehicle() {
    calc.setSolutionCompletenessRatio(0.5);
    calc.setWeightOfFixCost(.5);
    assertEquals(18.75, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNullAndSolutionIs75PercentCompleteAndWeightIs05_itShouldReturnHalfOfAvgOfRelFixedAndAbsFixedCostOfNewVehicle() {
    calc.setSolutionCompletenessRatio(0.75);
    calc.setWeightOfFixCost(0.5);
    assertEquals(32.8125, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionComplete_itShouldReturnHalfOfFixedCostsOfNewVehicle() {
    calc.setSolutionCompletenessRatio(1.0);
    calc.setWeightOfFixCost(1.0);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(50., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionIs0PercentComplete_itShouldReturnNoFixedCosts() {
    calc.setSolutionCompletenessRatio(0.0);
    calc.setWeightOfFixCost(1.0);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(0., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionIs50PercentComplete_itShouldCorrectVal() {
    calc.setSolutionCompletenessRatio(0.5);
    calc.setWeightOfFixCost(1.0);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(25., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionIs75PercentComplete_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.75);
    calc.setWeightOfFixCost(1.0);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(37.5, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionCompleteAndWeightIs05_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(1.0);
    calc.setWeightOfFixCost(.5);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(25., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionIs0PercentCompleteAndWeightIs05_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.0);
    calc.setWeightOfFixCost(.5);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(0., calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionIs50PercentCompleteAndWeightIs05_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.5);
    calc.setWeightOfFixCost(.5);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(12.5, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndSolutionIs75PercentCompleteAndWeightIs05_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.75);
    calc.setWeightOfFixCost(0.5);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(18.75, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndCurrentLoadIs25AndSolutionIs50PercentCompleteAndWeightIs05_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.5);
    calc.setWeightOfFixCost(.5);
    when(route.getVehicle()).thenReturn(oVehicle);
    when(stateGetter.getRouteState(route, InternalStates.MAXLOAD, SizeDimension.class)).thenReturn(SizeDimension.Builder.newInstance().addDimension(0, 25).build());
    assertEquals(12.5, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndCurrentLoadIs25AndSolutionIs75PercentCompleteAndWeightIs05_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.75);
    calc.setWeightOfFixCost(0.5);
    when(route.getVehicle()).thenReturn(oVehicle);
    assertEquals(18.75, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndCurrentLoadIs25AndSolutionIs50PercentCompleteAndWeightIs05WithMultipleCapDims_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(.5);
    calc.setWeightOfFixCost(.5);
    when(job.getSize()).thenReturn(SizeDimension.Builder.newInstance().addDimension(0, 50).addDimension(1, 0).build());
    VehicleType oType = VehicleTypeImpl.Builder.newInstance("otype").addCapacityDimension(0, 50).addCapacityDimension(1, 100).setFixedCost(50.0).build();
    when(oVehicle.getType()).thenReturn(oType);
    VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 100).addCapacityDimension(1, 400).setFixedCost(100.0).build();
    when(nVehicle.getType()).thenReturn(type);
    when(route.getVehicle()).thenReturn(oVehicle);
    when(stateGetter.getRouteState(route, InternalStates.MAXLOAD, SizeDimension.class)).thenReturn(SizeDimension.Builder.newInstance().addDimension(0, 25).addDimension(1, 100).build());
    assertEquals(7.8125, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }

  @Test public void whenOldVehicleIsNotNullAndCurrentLoadIs25AndSolutionIs75PercentCompleteAndWeightIs05WithMultipleCapDims_itShouldReturnCorrectVal() {
    calc.setSolutionCompletenessRatio(0.75);
    calc.setWeightOfFixCost(0.5);
    when(job.getSize()).thenReturn(SizeDimension.Builder.newInstance().addDimension(0, 50).addDimension(1, 0).build());
    VehicleType oType = VehicleTypeImpl.Builder.newInstance("otype").addCapacityDimension(0, 50).addCapacityDimension(1, 100).setFixedCost(50.0).build();
    when(oVehicle.getType()).thenReturn(oType);
    VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 100).addCapacityDimension(1, 400).setFixedCost(100.0).build();
    when(nVehicle.getType()).thenReturn(type);
    when(route.getVehicle()).thenReturn(oVehicle);
    when(stateGetter.getRouteState(route, InternalStates.MAXLOAD, SizeDimension.class)).thenReturn(SizeDimension.Builder.newInstance().addDimension(0, 25).addDimension(1, 100).build());
    assertEquals(15.234375, calc.getInsertionData(route, job, nVehicle, 0.0, null, Double.MAX_VALUE).getInsertionCost(), 0.01);
  }
}