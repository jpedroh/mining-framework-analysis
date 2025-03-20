package com.premiumminds.billy.portugal.test.services.builders;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTPayment;
import com.premiumminds.billy.portugal.services.entities.PTPayment;
import com.premiumminds.billy.portugal.test.PTAbstractTest;
import com.premiumminds.billy.portugal.test.fixtures.MockPTPaymentEntity;

public class TestPTPaymentBuilder extends PTAbstractTest {
  private static final String PT_PAYMENT_YML = AbstractTest.YML_CONFIGS_DIR + "PTPayment.yml";

  @Test public void doTest() {
    MockPTPaymentEntity mock = this.createMockEntity(MockPTPaymentEntity.class, TestPTPaymentBuilder.PT_PAYMENT_YML);
    Mockito.when(this.getInstance(DAOPTPayment.class).getEntityInstance()).thenReturn(new MockPTPaymentEntity());
    PTPayment.Builder builder = this.getInstance(PTPayment.Builder.class);
    builder.setPaymentAmount(mock.getPaymentAmount()).setPaymentDate(mock.getPaymentDate()).setPaymentMethod(mock.getPaymentMethod());
    PTPayment payment = builder.build();
    Assert.assertTrue(payment != null);
    Assert.assertEquals(payment.getPaymentMethod(), mock.getPaymentMethod());
    Assert.assertEquals(payment.getPaymentAmount(), mock.getPaymentAmount());
    Assert.assertEquals(payment.getPaymentDate(), mock.getPaymentDate());
  }
}