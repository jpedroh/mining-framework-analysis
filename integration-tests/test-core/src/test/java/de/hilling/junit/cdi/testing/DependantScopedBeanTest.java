package de.hilling.junit.cdi.testing;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Don't actually use this feature: Injecting dependant scoped beans into the test might lead to unexpected behaviour.
 * <p>Ask yourself: What is the expectation regarding the lifecycle of the instance?</p>
 * <p>In will actually be re-resolved in each test-run</p>
 */
public class DependantScopedBeanTest extends BaseTest {

    @Inject
    private DependantScopedBean dependantScopedBean;

    @Inject
    private ApplicationBean applicationBean;

    @Inject
    private TestScopedBean testScopedBean;

    @Test
    public void callTestScoped() {
        Assertions.assertEquals("hello", testScopedBean.getAttribute());    }

    @Test
    public void getAttribute() {
        Assertions.assertEquals("hello", dependantScopedBean.getAttribute());
    }

    @Test
    public void setAttributeTransitive() {
        applicationBean.setAttribute("world");
        Assertions.assertEquals("world", dependantScopedBean.getAttribute());
    }

}
