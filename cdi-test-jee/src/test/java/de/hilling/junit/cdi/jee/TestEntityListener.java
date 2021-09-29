package de.hilling.junit.cdi.jee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class TestEntityListener {
    private static final Logger LOG = LoggerFactory.getLogger(TestEntityListener.class);

    @Inject
    private UpdateCounter updateCounter;

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(Object o) {
        updateCounter.inc();
    }
}
