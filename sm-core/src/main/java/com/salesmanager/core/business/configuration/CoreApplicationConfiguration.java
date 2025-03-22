package com.salesmanager.core.business.configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration @EnableAutoConfiguration @ComponentScan(value = { "com.salesmanager.core.business" }) @ImportResource(value = "classpath:/spring/shopizer-core-context.xml") 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/b80f72c11fb19cc7ad447095953e7e7885b64ab9/sm-core/src/main/java/com/salesmanager/core/business/configuration/CoreApplicationConfiguration.java/left.java
@Import(value = { DroolsConfiguration.class, DataConfiguration.class })
=======
>>>>>>> Unknown file: This is a bug in JDime.
 public class CoreApplicationConfiguration {
}