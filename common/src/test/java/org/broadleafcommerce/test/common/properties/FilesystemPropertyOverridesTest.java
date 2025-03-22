package org.broadleafcommerce.test.common.properties;
import org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener;
import org.broadleafcommerce.common.config.BroadleafEnvironmentConfigurer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Validates that I can pass in a system argument pointing to a file on the filesystem to override any properties in the application
 * even when a profile is specified
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(value = SpringRunner.class) @ContextConfiguration(initializers = BroadleafEnvironmentConfiguringApplicationListener.class) @ActiveProfiles(value = "production") @DirtiesContext public class FilesystemPropertyOverridesTest {
  @Autowired protected Environment env;

  @BeforeClass public static void setOverrideProperty() {
    String overridePropertiesPath = FilesystemPropertyOverridesTest.class.getClassLoader().getResource("overridestest.properties").getFile();
    overridePropertiesPath = overridePropertiesPath.replace("%20", " ");
    overridePropertiesPath = overridePropertiesPath.replace("%40", "@");
    System.setProperty(BroadleafEnvironmentConfigurer.PROPERTY_OVERRIDES_PROPERTY, overridePropertiesPath);
  }

  @AfterClass public static void clearOverrideProperty() {
    System.clearProperty(BroadleafEnvironmentConfigurer.PROPERTY_OVERRIDES_PROPERTY);
  }

  @Test @DirtiesContext public void testPropertiesWereOverridden() {
    Assert.assertEquals("overridevalue", env.getProperty(DefaultDevelopmentOverridePropertiesTest.TEST_PROPERTY));
    Assert.assertTrue(((ConfigurableEnvironment) env).getPropertySources().contains(BroadleafEnvironmentConfigurer.OVERRIDE_SOURCES_NAME));
  }
}