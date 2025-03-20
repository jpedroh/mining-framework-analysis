package com.premiumminds.billy.portugal.test.services.jpa;
import com.google.inject.Injector;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;

public class PTJPAAbstractTest extends PTPersistencyAbstractTest {
  public static void execute(final Injector injector, TransactionWrapper<?> transactionWrapper) throws Exception {
    transactionWrapper.execute();
  }
}