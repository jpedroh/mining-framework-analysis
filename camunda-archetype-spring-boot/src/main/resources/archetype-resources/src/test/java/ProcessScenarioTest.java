package ${package};

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ProcessScenarioTest {

<<<<<<< /usr/src/app/output/camunda/camunda-archetypes/4bc11ca34e0b8a811a6d5f12d45eaddd2f646cc6/camunda-archetype-spring-boot/src/main/resources/archetype-resources/src/test/java/ProcessScenarioTest.java/left.java
  @Autowired
  private ProcessEngine processEngine;

||||||| /usr/src/app/output/camunda/camunda-archetypes/4bc11ca34e0b8a811a6d5f12d45eaddd2f646cc6/camunda-archetype-spring-boot/src/main/resources/archetype-resources/src/test/java/ProcessScenarioTest.java/base.java
  private static final String PROCESS_DEFINITION_KEY = "${artifactId}";

  @Autowired
  private ProcessEngine processEngine;

=======
  private static final String PROCESS_DEFINITION_KEY = "${artifactId}";

>>>>>>> /usr/src/app/output/camunda/camunda-archetypes/4bc11ca34e0b8a811a6d5f12d45eaddd2f646cc6/camunda-archetype-spring-boot/src/main/resources/archetype-resources/src/test/java/ProcessScenarioTest.java/right.java
  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

  @Rule
  public final ProcessEngineRule processEngine = new StandaloneInMemoryTestConfiguration().rule();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Mock
  private ProcessScenario myProcess;

  @Test
  public void testHappyPath() {
    // Define scenarios by using camunda-bpm-assert-scenario:

    //ExecutableRunner starter = Scenario.run(myProcess) //
    //    .startByKey(ProcessConstants.PROCESS_DEFINITION_KEY);

    // when(myProcess.waitsAtReceiveTask(anyString())).thenReturn((messageSubscription) -> {
    //  messageSubscription.receive();
    // });
    // when(myProcess.waitsAtUserTask(anyString())).thenReturn((task) -> {
    //  task.complete();
    // });

    // OK - everything prepared - let's go and execute the scenario
    //Scenario scenario = starter.execute();

    // now you can do some assertions   
    //verify(myProcess).hasFinished("EndEvent");
  }

}
