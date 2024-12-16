package de.hilling.junit.cdi.scope.context;

import de.hilling.junit.cdi.annotations.BypassTestInterceptor;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import org.immutables.value.Value;


/**
 * Generate an instance of custom scope.
 *
 * @param <T>
 * 		scope type.
 */
@BypassTestInterceptor
@Value.Immutable
public interface CustomScopeInstance<T> {
    public abstract Bean<T> getBean();

    public abstract CreationalContext<T> getCtx();

    T getInstance();
}