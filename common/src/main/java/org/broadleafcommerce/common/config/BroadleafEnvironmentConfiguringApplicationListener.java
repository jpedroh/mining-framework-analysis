package org.broadleafcommerce.common.config;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * <p>
 * In non-boot this class should be hooked up in your web.xml as shown below
 * 
 * <pre>
 * {@literal
 * <context-param>
 *   <param-name>contextInitializerClasses</param-name>
 *   <param-value>org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener</param-value>
 * </context-param>
 * }
 * </pre>
 * 
 * For Spring Boot deployments see {@link org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringPostProcessor}
 * 
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 * @see BroadleafEnvironmentConfigurer
 */
public class BroadleafEnvironmentConfiguringApplicationListener extends BroadleafEnvironmentConfigurer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override public void initialize(ConfigurableApplicationContext applicationContext) {
    configure(applicationContext.getEnvironment());
  }
}