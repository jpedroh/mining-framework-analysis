package de.hilling.junit.cdi.scope;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.annotation.Annotation;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.hilling.junit.cdi.CdiTestJunitExtension;
import de.hilling.junit.cdi.ContextControl;
import de.hilling.junit.cdi.scopedbeans.ApplicationScopedBean;
import de.hilling.junit.cdi.scopedbeans.RequestScopedBean;
import de.hilling.junit.cdi.scopedbeans.ScopedBean;
import de.hilling.junit.cdi.scopedbeans.SessionScopedBean;

@ExtendWith(CdiTestJunitExtension.class)
class ContextControlTest {

    @Inject
    private RequestScopedBean     requestScopedBean;
    @Inject
    private ApplicationScopedBean applicationScopedBean;
    @Inject
    private SessionScopedBean     sessionScopedBean;
    @Inject
    private ContextControl        contextControl;

    @Test
    void restartRequestStopAll() {
        runTestStopAll(requestScopedBean, RequestScoped.class);
    }

    @Test
    void restartSessionStopAll() {
        runTestStopAll(sessionScopedBean, SessionScoped.class);
    }

    @Test
    void restartApplicationStopAll() {
        runTestStopAll(applicationScopedBean, ApplicationScoped.class);
    }

    @Test
    void restartRequest() {
        runTest(requestScopedBean, RequestScoped.class);
    }

    @Test
    void restartApplication() {
        runTest(applicationScopedBean, ApplicationScoped.class);
    }

    private void runTest(ScopedBean scopedBean, Class<? extends Annotation> scope) {
        contextControl.stopContext(scope);
        contextControl.startContext(scope);
        UUID uuid = scopedBean.getUuid();
        contextControl.stopContext(scope);
        contextControl.startContext(scope);
        UUID uuid2 = scopedBean.getUuid();
        assertNotEquals(uuid, uuid2);
    }

    private void runTestStopAll(ScopedBean scopedBean, Class<? extends Annotation> scope) {
        contextControl.stopContexts();
        contextControl.startContext(scope);
        UUID uuid = scopedBean.getUuid();
        contextControl.stopContexts();
        contextControl.startContext(scope);
        UUID uuid2 = scopedBean.getUuid();
        assertNotEquals(uuid, uuid2);
    }
}
