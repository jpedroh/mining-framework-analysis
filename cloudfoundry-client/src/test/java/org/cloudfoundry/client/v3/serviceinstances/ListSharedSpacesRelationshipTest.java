package org.cloudfoundry.client.v3.serviceinstances;
import org.junit.Test;

public class ListSharedSpacesRelationshipTest {
  @Test(expected = IllegalStateException.class) public void noServiceInstanceId() {
    ListSharedSpacesRelationshipRequest.builder().build();
  }

  @Test public void valid() {
    ListSharedSpacesRelationshipRequest.builder().serviceInstanceId("test-service-instance-id").build();
  }
}