package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import basics.VehicleRoutingProblem;
import basics.algo.InsertionListener;
import basics.algo.VehicleRoutingAlgorithmListeners.PrioritizedVRAListener;
import basics.route.VehicleFleetManager;

public class BestInsertionBuilder {

	private VehicleRoutingProblem vrp;
	
	private StateManager stateManager;
	
	private boolean local = true;
	
	private ConstraintManager constraintManager;

	private VehicleFleetManager fleetManager;

	private double weightOfFixedCosts;

	private boolean considerFixedCosts = false;

	private ActivityInsertionCostsCalculator actInsertionCostsCalculator = null;

	private int forwaredLooking;

	private int memory;

	private ExecutorService executor;

	private int nuOfThreads;
	
	public BestInsertionBuilder(VehicleRoutingProblem vrp, VehicleFleetManager vehicleFleetManager, StateManager stateManager, ConstraintManager constraintManager) {
		super();
		this.vrp = vrp;
		this.stateManager = stateManager;
		this.constraintManager = constraintManager;
		this.fleetManager = vehicleFleetManager;
	}
		
	public BestInsertionBuilder setRouteLevel(int forwardLooking, int memory){

		local = false;
		this.forwaredLooking = forwardLooking;
		this.memory = memory;
		return this;
	};
	
	public BestInsertionBuilder setLocalLevel(){
		local = true;
		return this;
	};
	
	public BestInsertionBuilder considerFixedCosts(double weightOfFixedCosts){
		this.weightOfFixedCosts = weightOfFixedCosts;
		this.considerFixedCosts  = true;
		return this;
	}
	
	public BestInsertionBuilder setActivityInsertionCostCalculator(ActivityInsertionCostsCalculator activityInsertionCostsCalculator){
		this.actInsertionCostsCalculator = activityInsertionCostsCalculator;
		return this;
	};
	
	public BestInsertionBuilder setConcurrentMode(ExecutorService executor, int nuOfThreads){
		this.executor = executor;
		this.nuOfThreads = nuOfThreads;
		return this;
	}
	
	public InsertionStrategy build() {
		List<InsertionListener> iListeners = new ArrayList<InsertionListener>();
		List<PrioritizedVRAListener> algorithmListeners = new ArrayList<PrioritizedVRAListener>();
		CalculatorBuilder calcBuilder = new CalculatorBuilder(iListeners, algorithmListeners);
		if(local){
			calcBuilder.setLocalLevel();
		}
		else {
			calcBuilder.setRouteLevel(forwaredLooking, memory);
		}
		calcBuilder.setConstraintManager(constraintManager);
		calcBuilder.setStates(stateManager);
		calcBuilder.setVehicleRoutingProblem(vrp);
		calcBuilder.setVehicleFleetManager(fleetManager);
		calcBuilder.setActivityInsertionCostsCalculator(actInsertionCostsCalculator);
		if(considerFixedCosts) {
			calcBuilder.considerFixedCosts(weightOfFixedCosts);
		}
		JobInsertionCostsCalculator jobInsertions = calcBuilder.build();
		InsertionStrategy bestInsertion;
<<<<<<< /usr/src/app/output/jsprit/jsprit/d1dac2d622e978ac839e3ba49cc8037b40ae5762/jsprit-core/src/main/java/algorithms/BestInsertionBuilder.java/left.java
		if(executor == null){
			bestInsertion = new BestInsertion(jobInsertions);
			
		}
		else{
			bestInsertion = new BestInsertionConc(jobInsertions,executor,nuOfThreads);
		}
||||||| /usr/src/app/output/jsprit/jsprit/d1dac2d622e978ac839e3ba49cc8037b40ae5762/jsprit-core/src/main/java/algorithms/BestInsertionBuilder.java/base.java
=======
		if(executor == null){
			bestInsertion = new BestInsertion(jobInsertions);
			
		}
		else{
			bestInsertion = new BestInsertionConcurrent(jobInsertions,executor,nuOfThreads);
		}
>>>>>>> /usr/src/app/output/jsprit/jsprit/d1dac2d622e978ac839e3ba49cc8037b40ae5762/jsprit-core/src/main/java/algorithms/BestInsertionBuilder.java/right.java
		for(InsertionListener l : iListeners) bestInsertion.addListener(l);
		return bestInsertion;
	}

}
