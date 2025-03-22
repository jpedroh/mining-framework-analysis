package org.broadleafcommerce.test.common.context.autoconfig;
import org.broadleafcommerce.test.common.context.autoconfig.nested.ContainsNestedConfiguration;
import org.broadleafcommerce.test.common.context.autoconfig.nested.ContainsNestedConfiguration.NestedAfterAutoConfiguration;
import org.broadleafcommerce.test.common.context.autoconfig.scan.AfterAutoConfiguration;
import org.broadleafcommerce.test.common.context.autoconfig.scan.ComponentScanningConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@SpringBootTest @RunWith(value = SpringRunner.class) @TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true") public class PostAutoConfigurationTest {
  @Configuration @Import(value = { ComponentScanningConfiguration.class, ContainsNestedConfiguration.class }) @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class }) public static class Config {
  }

  @Autowired @Qualifier(value = "overridingBean") String overridingBean;

  @Autowired @Qualifier(value = "nonOverridingBean") String nonOverridingBean;

  @Autowired @Qualifier(value = "nestedRunsFirst") String nestedRunsFirst;

  @Test public void testPostAutoConfigurationOverridesAutoConfig() {
    Assert.assertEquals(AfterAutoConfiguration.class.getName(), overridingBean);
  }

  @Test public void testPostAutoConfigurationRunsSecond() {
    Assert.assertEquals(BaseAutoConfiguration.class.getName(), nonOverridingBean);
  }

  @Test public void testNestedPostAutoConfigurationRunsFirst() {
    Assert.assertEquals(NestedAfterAutoConfiguration.class.getName(), nestedRunsFirst);
  }
}