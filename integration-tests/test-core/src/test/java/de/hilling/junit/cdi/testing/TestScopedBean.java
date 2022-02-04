package de.hilling.junit.cdi.testing;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import de.hilling.junit.cdi.scope.TestScoped;

@TestScoped
public class TestScopedBean {

    @Inject
    private DependantScopedBean dependantScopedBean;

    @PostConstruct
    public void setup() {
        dependantScopedBean.getAttribute();
    }

    public String getAttribute() {
        return dependantScopedBean.getAttribute();
    }
}
