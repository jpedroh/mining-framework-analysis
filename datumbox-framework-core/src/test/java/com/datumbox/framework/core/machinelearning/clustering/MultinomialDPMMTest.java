package com.datumbox.framework.core.machinelearning.clustering;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.common.dataobjects.Dataframe;
import com.datumbox.framework.core.machinelearning.validators.ClustererValidator;
import com.datumbox.framework.tests.Constants;
import com.datumbox.framework.tests.Datasets;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

/**
 * Test cases for MultinomialDPMM.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class MultinomialDPMMTest extends AbstractTest {
  /**
     * Test of validate method, of class MultinomialDPMM.
     */
  @Test public void testValidate() {
    logger.info("validate");
    Configuration conf = Configuration.getConfiguration();
    Dataframe[] data = Datasets.multinomialClusters(conf);
    Dataframe trainingData = data[0];
    Dataframe validationData = data[1];
    String dbName = this.getClass().getSimpleName();
    MultinomialDPMM instance = new MultinomialDPMM(dbName, conf);
    MultinomialDPMM.TrainingParameters param = new MultinomialDPMM.TrainingParameters();
    param.setAlpha(0.01);
    param.setMaxIterations(100);
    param.setInitializationMethod(MultinomialDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
    param.setAlphaWords(1);
    instance.fit(trainingData, param);
    instance.close();
    instance = new MultinomialDPMM(dbName, conf);
    ClustererValidator.ValidationMetrics vm = instance.validate(validationData);
    double expResult = 1.0;
    double result = vm.getPurity();
    assertEquals(expResult, result, Constants.DOUBLE_ACCURACY_HIGH);
    instance.delete();
    trainingData.delete();
    validationData.delete();
  }

  /**
     * Test of kFoldCrossValidation method, of class MultinomialDPMM.
     */
  @Test public void testKFoldCrossValidation() {
    logger.info("kFoldCrossValidation");
    Configuration conf = Configuration.getConfiguration();
    int k = 5;
    Dataframe[] data = Datasets.multinomialClusters(conf);
    Dataframe trainingData = data[0];
    data[1].delete();
    String dbName = this.getClass().getSimpleName();
    MultinomialDPMM instance = new MultinomialDPMM(dbName, conf);
    MultinomialDPMM.TrainingParameters param = new MultinomialDPMM.TrainingParameters();
    param.setAlpha(0.01);
    param.setMaxIterations(100);
    param.setInitializationMethod(MultinomialDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
    param.setAlphaWords(1);
    ClustererValidator.ValidationMetrics vm = instance.kFoldCrossValidation(trainingData, param, k);
    double expResult = 1.0;
    double result = vm.getPurity();
    Assert.assertEquals(expResult, result, Constants.DOUBLE_ACCURACY_HIGH);
    instance.delete();
    trainingData.delete();
  }
}