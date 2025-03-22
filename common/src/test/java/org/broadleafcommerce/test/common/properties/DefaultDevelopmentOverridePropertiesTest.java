package org.broadleafcommerce.test.common.properties;
import org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener;
import org.broadleafcommerce.common.config.BroadleafEnvironmentConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Validates that profile-specific properties override framework values with the default of 'development'
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(value = SpringRunner.class) @ContextConfiguration(initializers = BroadleafEnvironmentConfiguringApplicationListener.class) @DirtiesContext public class DefaultDevelopmentOverridePropertiesTest {
  public static final String TEST_PROPERTY = "test.property.source";

  @Autowired protected Environment env;

  @Test public void testProfileOverridesCommon() {
    Assert.assertEquals("developmentvalue", env.getProperty(TEST_PROPERTY));
    Assert.assertTrue(((ConfigurableEnvironment) env).getPropertySources().contains(BroadleafEnvironmentConfigurer.FRAMEWORK_SOURCES_NAME));
    Assert.assertTrue(((ConfigurableEnvironment) env).getPropertySources().contains(BroadleafEnvironmentConfigurer.PROFILE_AWARE_SOURCES_NAME));
  }
}