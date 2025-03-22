package org.mybatis.spring.annotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Retention(value = RetentionPolicy.RUNTIME) @Target(value = { ElementType.TYPE }) @Documented @Import(value = MapperScannerRegistrar.class) @Repeatable(value = MapperScans.class) public @interface MapperScan {
  @AliasFor(value = "basePackages") String[] value() default {  };

  @AliasFor(value = "value") String[] basePackages() default {  };

  Class<?>[] basePackageClasses() default {  };

  Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

  Class<? extends Annotation> annotationClass() default Annotation.class;

  Class<?> markerInterface() default Class.class;

  String sqlSessionTemplateRef() default "";

  String sqlSessionFactoryRef() default "";

  Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;

  String lazyInitialization() default "";

  String defaultScope() default AbstractBeanDefinition.SCOPE_DEFAULT;
}