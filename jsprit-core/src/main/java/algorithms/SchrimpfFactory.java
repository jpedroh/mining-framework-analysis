package algorithms;
import java.net.URL;
import util.Resource;
import basics.VehicleRoutingAlgorithm;
import basics.VehicleRoutingProblem;
import basics.io.AlgorithmConfig;
import basics.io.AlgorithmConfigXmlReader;

/**
 * Factory that creates the {@link VehicleRoutingAlgorithm} as proposed by Schrimpf et al., 2000 with the following parameters:
 * 
 * <p>
 * R&R_random (prob=0.5, F=0.5);
 * R&R_radial (prob=0.5, F=0.3);
 * threshold-accepting with exponentialDecayFunction (alpha=0.1, warmup-iterations=100);
 * nuOfIterations=2000
 * 
 * <p>Gerhard Schrimpf, Johannes Schneider, Hermann Stamm- Wilbrandt, and Gunter Dueck. 
 * Record breaking optimization results using the ruin and recreate principle. 
 * Journal of Computational Physics, 159(2):139 – 171, 2000. ISSN 0021-9991. doi: 10.1006/jcph.1999. 6413. 
 * URL http://www.sciencedirect.com/science/article/ pii/S0021999199964136
 * 
 * <p>algorithm-xml-config is available at src/main/resources/schrimpf.xml. 
 * 
 * @author stefan schroeder
 *
 */
public class SchrimpfFactory {
  /**
	 * Creates the {@link VehicleRoutingAlgorithm}.
	 * 
	 * @param vrp
	 * @return algorithm
	 */
  public VehicleRoutingAlgorithm createAlgorithm(VehicleRoutingProblem vrp) {
    AlgorithmConfig algorithmConfig = new AlgorithmConfig();
    URL resource = Resource.getAsURL("schrimpf.xml");
    new AlgorithmConfigXmlReader(algorithmConfig).read(resource);
    return VehicleRoutingAlgorithms.createAlgorithm(vrp, algorithmConfig);
  }
}