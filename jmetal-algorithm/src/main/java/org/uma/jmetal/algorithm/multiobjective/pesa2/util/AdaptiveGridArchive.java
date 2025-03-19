package org.uma.jmetal.algorithm.multiobjective.pesa2.util;
import java.util.Comparator;
import java.util.Iterator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AdaptiveGrid;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/**
 * This class implements an archive (solution list) based on an adaptive grid used in PAES
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings(value = { "serial" }) public class AdaptiveGridArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private AdaptiveGrid<S> grid;

  private Comparator<S> dominanceComparator;

  /**
   * Constructor.
   *
   * @param maxSize    The maximum size of the setArchive
   * @param bisections The maximum number of bi-divisions for the adaptive
   *                   grid.
   * @param objectives The number of objectives.
   */
  public AdaptiveGridArchive(int maxSize, int bisections, int objectives) {
    super(maxSize);
    dominanceComparator = new DominanceWithConstraintsComparator<S>();
    grid = new AdaptiveGrid<S>(bisections, objectives);
  }

  /**
   * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
   * is dominated by any member of the setArchive then it is discarded. If the
   * <code>Solution</code> dominates some members of the setArchive, these are
   * removed. If the setArchive is full and the <code>Solution</code> has to be
   * inserted, one <code>Solution</code> of the most populated hypercube of the
   * adaptive grid is removed.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  @Override public boolean add(S solution) {
    Iterator<S> iterator = getSolutionList().iterator();
    while (iterator.hasNext()) {
      S element = iterator.next();
      int flag = dominanceComparator.compare(solution, element);
      if (flag == -1) {
        iterator.remove();
        int location = grid.location(element);
        if (grid.getLocationDensity(location) > 1) {
          grid.removeSolution(location);
        } else {
          grid.updateGrid(getSolutionList());
        }
      } else {
        if (flag == 1) {
          return false;
        }
      }
    }
    if (this.size() == 0) {
      this.getSolutionList().add(solution);
      grid.updateGrid(getSolutionList());
      return true;
    }
    if (this.getSolutionList().size() < this.getMaxSize()) {
      grid.updateGrid(solution, getSolutionList());
      int location;
      location = grid.location(solution);
      grid.addSolution(location);
      getSolutionList().add(solution);
      return true;
    }
    grid.updateGrid(solution, getSolutionList());
    int location = grid.location(solution);
    if (location == grid.getMostPopulatedHypercube()) {
      return false;
    } else {
      prune();
      grid.addSolution(location);
      getSolutionList().add(solution);
    }
    return true;
  }

  public AdaptiveGrid<S> getGrid() {
    return grid;
  }

  public void prune() {
    Iterator<S> iterator = getSolutionList().iterator();
    while (iterator.hasNext()) {
      S element = iterator.next();
      int location = grid.location(element);
      if (location == grid.getMostPopulatedHypercube()) {
        iterator.remove();
        grid.removeSolution(location);
        return;
      }
    }
  }

  @Override public Comparator<S> getComparator() {
    return null;
  }

  @Override public void computeDensityEstimator() {
  }
}