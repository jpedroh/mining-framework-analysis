  package     com . ryantenney . metrics . spring . config ;   import      org . springframework . beans . factory . xml . NamespaceHandlerSupport ;  class MetricsNamespaceHandler  extends NamespaceHandlerSupport  {    @ Override public void init  ( )  {   registerBeanDefinitionParser  ( "annotation-driven" ,  new AnnotationDrivenBeanDefinitionParser  ( ) ) ;   registerBeanDefinitionParser  ( "metric-registry" ,  new MetricRegistryBeanDefinitionParser  ( ) ) ;   registerBeanDefinitionParser  ( "health-check-registry" ,  new HealthCheckRegistryBeanDefinitionParser  ( ) ) ;   registerBeanDefinitionParser  ( "reporter" ,  new ReporterBeanDefinitionParser  ( ) ) ;   registerBeanDefinitionParser  ( 
<<<<<<<
"metric-set"
=======
"register"
>>>>>>>
 , 
<<<<<<<
 new MetricSetBeanDefinitionParser  ( )
=======
 new RegisterMetricBeanDefinitionParser  ( )
>>>>>>>
 ) ; }   public static final String  METRICS_NAMESPACE = "http://www.ryantenney.com/schema/metrics" ; }