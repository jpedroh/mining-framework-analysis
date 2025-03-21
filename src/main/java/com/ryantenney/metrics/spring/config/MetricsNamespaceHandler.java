package com.ryantenney.metrics.spring.config;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

class MetricsNamespaceHandler extends NamespaceHandlerSupport {
  public static final String METRICS_NAMESPACE = "http://www.ryantenney.com/schema/metrics";

  @Override public void init() {
    registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
    registerBeanDefinitionParser("metric-registry", new MetricRegistryBeanDefinitionParser());
    registerBeanDefinitionParser("health-check-registry", new HealthCheckRegistryBeanDefinitionParser());
    registerBeanDefinitionParser("reporter", new ReporterBeanDefinitionParser());
    registerBeanDefinitionParser(
<<<<<<< /usr/src/app/output/ryantenney/metrics-spring/4ddab2725aa59f451c78609e432999812eee675a/src/main/java/com/ryantenney/metrics/spring/config/MetricsNamespaceHandler.java/left.java
    "metric-set"
=======
    "register"
>>>>>>> /usr/src/app/output/ryantenney/metrics-spring/4ddab2725aa59f451c78609e432999812eee675a/src/main/java/com/ryantenney/metrics/spring/config/MetricsNamespaceHandler.java/right.java
    , new 
<<<<<<< /usr/src/app/output/ryantenney/metrics-spring/4ddab2725aa59f451c78609e432999812eee675a/src/main/java/com/ryantenney/metrics/spring/config/MetricsNamespaceHandler.java/left.java
    MetricSetBeanDefinitionParser
=======
    RegisterMetricBeanDefinitionParser
>>>>>>> /usr/src/app/output/ryantenney/metrics-spring/4ddab2725aa59f451c78609e432999812eee675a/src/main/java/com/ryantenney/metrics/spring/config/MetricsNamespaceHandler.java/right.java
    ());
  }
}