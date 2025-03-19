package org.uma.jmetal.util.archive.impl;
import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.comparator.SpatialSpreadDeviationComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.SpatialSpreadDeviation;

/**
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
@SuppressWarnings(value = { "serial" }) public class SpatialSpreadDeviationArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> crowdingDistanceComparator;

  private DensityEstimator<S> crowdingDistance;

  public SpatialSpreadDeviationArchive(int maxSize) {
    super(maxSize);
    crowdingDistanceComparator = new SpatialSpreadDeviationComparator<S>();
    crowdingDistance = new SpatialSpreadDeviation<S>();
  }

  @Override public void prune() {
    if (getSolutionList().size() > getMaxSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), crowdingDistanceComparator);
      getSolutionList().remove(worst);
    }
  }

  @Override public Comparator<S> getComparator() {
    return crowdingDistanceComparator;
  }

  @Override public void computeDensityEstimator() {
    crowdingDistance.computeDensityEstimator(getSolutionList());
  }
}