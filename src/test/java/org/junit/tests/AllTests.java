package org.junit.tests;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import org.junit.AssumptionViolatedExceptionTest;
import org.junit.internal.AllInternalTests;
import org.junit.rules.AllRulesTests;
import org.junit.runner.AllRunnerTests;
import org.junit.runner.RunWith;
import org.junit.runners.AllRunnersTests;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.samples.AllSamplesTests;
import org.junit.tests.assertion.AllAssertionTests;
import org.junit.tests.deprecated.AllDeprecatedTests;
import org.junit.tests.description.AllDescriptionTests;
import org.junit.tests.experimental.AllExperimentalTests;
import org.junit.tests.junit3compatibility.AllJUnit3CompatibilityTests;
import org.junit.tests.listening.AllListeningTests;
import org.junit.tests.manipulation.AllManipulationTests;
import org.junit.tests.running.AllRunningTests;
import org.junit.tests.validation.AllValidationTests;
import org.junit.validator.AllValidatorTests;
import org.junit.tests.manipulation.OrderWithTest;
import org.junit.tests.manipulation.OrderableTest;

@RunWith(value = Suite.class) @SuiteClasses(value = { AllAssertionTests.class, AllDeprecatedTests.class, AllDescriptionTests.class, AllExperimentalTests.class, AllInternalTests.class, 
<<<<<<< /usr/src/app/output/junit-team/junit4/6e38776326e9a68beb47e8ea491eef4761be511f/src/test/java/org/junit/tests/AllTests.java/left.java
OrderableTest
=======
AllJUnit3CompatibilityTests
>>>>>>> /usr/src/app/output/junit-team/junit4/6e38776326e9a68beb47e8ea491eef4761be511f/src/test/java/org/junit/tests/AllTests.java/right.java
.class, 
<<<<<<< /usr/src/app/output/junit-team/junit4/6e38776326e9a68beb47e8ea491eef4761be511f/src/test/java/org/junit/tests/AllTests.java/left.java
OrderWithTest
=======
AllListeningTests
>>>>>>> /usr/src/app/output/junit-team/junit4/6e38776326e9a68beb47e8ea491eef4761be511f/src/test/java/org/junit/tests/AllTests.java/right.java
.class, AllManipulationTests.class, AllRulesTests.class, AllRunnersTests.class, AllRunnerTests.class, AllRunningTests.class, AllSamplesTests.class, AllValidationTests.class, AllValidatorTests.class, AssumptionViolatedExceptionTest.class, ObjectContractTest.class }) public class AllTests {
  public static Test suite() {
    return new JUnit4TestAdapter(AllTests.class);
  }
}