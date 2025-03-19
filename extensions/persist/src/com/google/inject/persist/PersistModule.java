package com.google.inject.persist;
import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import com.google.inject.AbstractModule;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * Install this module to add guice-persist library support for JPA persistence
 * providers.
 *
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public abstract class PersistModule extends AbstractModule {
  @Override protected final void configure() {
    configurePersistence();
    requireBinding(PersistService.class);
    requireBinding(UnitOfWork.class);
    bindInterceptor(annotatedWith(Transactional.class), any(), getTransactionInterceptor());
    bindInterceptor(annotatedWith(RequiresUnitOfWork.class), any(), getRequiresUnitOfWorkInterceptor());
    bindInterceptor(any(), annotatedWith(Transactional.class), getTransactionInterceptor());
    bindInterceptor(any(), annotatedWith(RequiresUnitOfWork.class), getRequiresUnitOfWorkInterceptor());
  }

  protected abstract void configurePersistence();

  protected abstract MethodInterceptor getTransactionInterceptor();

  protected abstract MethodInterceptor getRequiresUnitOfWorkInterceptor();
}