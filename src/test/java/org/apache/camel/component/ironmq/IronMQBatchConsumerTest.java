package org.apache.camel.component.ironmq;
import java.util.HashMap;
import io.iron.ironmq.Message;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class IronMQBatchConsumerTest extends CamelTestSupport {
  private IronMQEndpoint endpoint;

  @Test public void testConsumeBatchDelete() throws Exception {
    for (int counter = 0; counter <= 5; counter++) {
      Message message = new Message();
      message.setBody("{\"body\": \"Message " + counter + "\"}");
      message.setId("" + counter);
      ((MockQueue) endpoint.getQueue()).add(message);
    }
    MockEndpoint mock = getMockEndpoint("mock:result");
    mock.expectedMessageCount(5);
    assertMockEndpointsSatisfied();
    mock.message(0).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(0);
    mock.message(1).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(1);
    mock.message(2).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(2);
    mock.message(3).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(3);
    mock.message(4).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(4);
    mock.message(0).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(1).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(2).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(3).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(3).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(4).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(true);
    mock.expectedPropertyReceived(Exchange.BATCH_SIZE, 5);
    Message lastMessage = endpoint.getQueue().peek();
    assertTrue(lastMessage.getBody().contains("Message 5"));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Test public void testConsumeBatch() throws Exception {
    for (int counter = 0; counter < 6; counter++) {
      Message message = new Message();
      message.setBody("{\"body\": \"Message " + counter + "\"}");
      message.setId("f6fb6f99-5eb2-4be4-9b15-144774141458" + counter);
      ((MockQueue) endpoint.getQueue()).add(message);
    }
    MockEndpoint mock = getMockEndpoint("mock:result");
    mock.expectedMessageCount(5);
    assertMockEndpointsSatisfied();
    mock.message(0).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(0);
    mock.message(1).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(1);
    mock.message(2).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(2);
    mock.message(3).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(3);
    mock.message(4).exchangeProperty(Exchange.BATCH_INDEX).isEqualTo(4);
    mock.message(0).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(1).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(2).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(3).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(3).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(false);
    mock.message(4).exchangeProperty(Exchange.BATCH_COMPLETE).isEqualTo(true);
    mock.expectedPropertyReceived(Exchange.BATCH_SIZE, 5);
  }
>>>>>>> /usr/src/app/output/pax95/camel-ironmq/7b8e755aa410ddd2841a5471f78bf5256a647b76/src/test/java/org/apache/camel/component/ironmq/IronMQBatchConsumerTest.java/right.java


  @Override protected CamelContext createCamelContext() throws Exception {
    CamelContext context = super.createCamelContext();
    IronMQComponent component = new IronMQComponent(context);
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("projectId", "dummy");
    parameters.put("token", "dummy");
    parameters.put("maxMessagesPerPoll", "5");
    parameters.put("batchDelete", "true");
    endpoint = (IronMQEndpoint) component.createEndpoint("ironmq", "testqueue", parameters);
    endpoint.setClient(new IronMQClientMock("dummy", "dummy"));
    context.addComponent("ironmq", component);
    return context;
  }

  @Override protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
      @Override public void configure() {
        from(endpoint).to("mock:result");
      }
    };
  }
}