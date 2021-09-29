package de.hilling.junit.cdi.lifecycle;

import de.hilling.junit.cdi.scope.TestState;
import org.immutables.value.Value;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifier to mark test events defined in {@link TestState}
 */
@Qualifier
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Value.Immutable
public @interface TestEvent {
    /**
     * @return current {@link TestState}
     */
    TestState value();
}
