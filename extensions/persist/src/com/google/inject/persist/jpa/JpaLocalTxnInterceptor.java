package com.google.inject.persist.jpa;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 */
class JpaLocalTxnInterceptor implements MethodInterceptor {
  @Transactional private static class Internal {
  }

  @Inject private UnitOfWorkHandler unitOfWorkHandler;


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private final ThreadLocal<Boolean> didWeStartWork = new ThreadLocal<>();
>>>>>>> /usr/src/app/output/google/guice/bcb6b21359eae2e095ef68b3d9f6380fa41e8414/extensions/persist/src/com/google/inject/persist/jpa/JpaLocalTxnInterceptor.java/right.java


  @Override public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    unitOfWorkHandler.requireUnitOfWork();
    Transactional transactional = readTransactionMetadata(methodInvocation);
    EntityManager em = unitOfWorkHandler.getEntityManager();
    if (em.getTransaction().isActive()) {
      try {
        return methodInvocation.proceed();
      }  finally {
        unitOfWorkHandler.endRequireUnitOfWork();
      }
    }
    final EntityTransaction txn = em.getTransaction();
    txn.begin();
    Object result;
    try {
      result = methodInvocation.proceed();
    } catch (Exception e) {
      if (rollbackIfNecessary(transactional, e, txn)) {
        txn.commit();
      }
      throw e;
    } finally {
      if (!txn.isActive()) {
        unitOfWorkHandler.endRequireUnitOfWork();
      }
    }
    try {
      txn.commit();
    }  finally {
      unitOfWorkHandler.endRequireUnitOfWork();
    }
    return result;
  }

  private Transactional readTransactionMetadata(MethodInvocation methodInvocation) {
    Transactional transactional;
    Method method = methodInvocation.getMethod();
    Class<?> targetClass = methodInvocation.getThis().getClass();
    transactional = method.getAnnotation(Transactional.class);
    if (null == transactional) {
      transactional = targetClass.getAnnotation(Transactional.class);
    }
    if (null == transactional) {
      transactional = Internal.class.getAnnotation(Transactional.class);
    }
    return transactional;
  }

  /**
   * Returns True if rollback DID NOT HAPPEN (i.e. if commit should continue).
   *
   * @param transactional The metadata annotaiton of the method
   * @param e The exception to test for rollback
   * @param txn A JPA Transaction to issue rollbacks on
   */
  private boolean rollbackIfNecessary(Transactional transactional, Exception e, EntityTransaction txn) {
    boolean commit = true;
    for (Class<? extends Exception> rollBackOn : transactional.rollbackOn()) {
      if (rollBackOn.isInstance(e)) {
        commit = false;
        for (Class<? extends Exception> exceptOn : transactional.ignore()) {
          if (exceptOn.isInstance(e)) {
            commit = true;
            break;
          }
        }
        if (!commit) {
          txn.rollback();
        }
        break;
      }
    }
    return commit;
  }
}