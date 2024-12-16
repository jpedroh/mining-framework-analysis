package de.hilling.junit.cdi.jee;

import de.hilling.junit.cdi.CdiTestJunitExtension;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(CdiTestJunitExtension.class)
@DisplayName("EntityManager Lifecycle")
@Transactional(Transactional.TxType.REQUIRES_NEW)
class EntityManagerLifecycleTest {
    @PersistenceContext(unitName = "cdi-test")
    private EntityManager entityManagerJta;

    @PersistenceUnit(unitName = "cdi-test")
    private EntityManagerFactory entityManagerFactoryJta;

    @PersistenceContext(unitName = "cdi-test-local")
    private EntityManager entityManagerLocal;

    @PersistenceUnit(unitName = "cdi-test-local")
    private EntityManagerFactory entityManagerFactoryLocal;

    @Test
    void jtaInjected() {
        assertNotNull(entityManagerJta);
    }

    @Test
    void localInjected() {
        assertNotNull(entityManagerLocal);
    }

    @Test
    void localFactoryInjected() {
        assertNotNull(entityManagerFactoryLocal);
    }

    @Test
    void jtaFactoryInjected() {
        assertNotNull(entityManagerFactoryJta);
    }

    @Test
    void localTransactionNotActive() {
        assertFalse(entityManagerLocal.getTransaction().isActive());
    }

    @Test
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void globalTransactionActive() {
        assertFalse(transactionServices.isTransactionActive());
        entityManagerJta.persist(new UserEntity());
        assertFalse(transactionServices.isTransactionActive());
    }

    @Inject
    private TransactionServices transactionServices;
}