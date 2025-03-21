package org.cloudfoundry.client.v3.serviceinstances;
import org.cloudfoundry.client.v3.Relationship;
import org.junit.Test;

public class ShareServiceInstanceRequestTest {
  @Test(expected = IllegalStateException.class) public void noServiceInstanceId() {
    ShareServiceInstanceRequest.builder().data(Relationship.builder().id("test-space-id").build()).build();
  }

  @Test public void valid() {
    ShareServiceInstanceRequest.builder().serviceInstanceId("test-service-instance-id").data(Relationship.builder().id("test-space-id").build()).build();
  }
}