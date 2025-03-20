package com.premiumminds.billy.spain.test.services.jpa;
import org.junit.Before;
import org.junit.Test;
import com.premiumminds.billy.core.persistence.dao.TransactionWrapper;
import com.premiumminds.billy.spain.persistence.dao.DAOESCustomer;
import com.premiumminds.billy.spain.persistence.dao.DAOESInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESCustomerEntity;
import com.premiumminds.billy.spain.test.ESAbstractTest;
import com.premiumminds.billy.spain.test.util.ESCustomerTestUtil;

public class TestJPAESCustomer extends ESJPAAbstractTest {
  private TransactionWrapper<Void> transaction;

  @Before public void setUp() {
    this.transaction = new TransactionWrapper<Void>(ESAbstractTest.injector.getInstance(DAOESInvoice.class)) {
      @Override public Void runTransaction() throws Exception {
        final ESCustomerTestUtil customer = new ESCustomerTestUtil(ESAbstractTest.injector);
        DAOESCustomer daoESCustomer = ESAbstractTest.injector.getInstance(DAOESCustomer.class);
        ESCustomerEntity newCustomer = customer.getCustomerEntity();
        daoESCustomer.create(newCustomer);
        return null;
      }
    };
  }

  @Test public void testSimpleCustomerCreate() throws Exception {
    ESJPAAbstractTest.execute(ESAbstractTest.injector, this.transaction);
  }
}