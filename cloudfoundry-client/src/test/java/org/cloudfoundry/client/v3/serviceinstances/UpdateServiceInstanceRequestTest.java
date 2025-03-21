package org.cloudfoundry.client.v3.serviceinstances;
import org.cloudfoundry.client.v3.Metadata;
import org.junit.Test;

public class UpdateServiceInstanceRequestTest {
  @Test(expected = IllegalStateException.class) public void noMetadata() {
    UpdateServiceInstanceRequest.builder().serviceInstanceId("test-service-instance-id").build();
  }

  @Test(expected = IllegalStateException.class) public void noServiceInstanceId() {
    UpdateServiceInstanceRequest.builder().metadata(Metadata.builder().build()).build();
  }

  @Test public void valid() {
    UpdateServiceInstanceRequest.builder().metadata(Metadata.builder().build()).serviceInstanceId("test-service-instance-id").build();
  }
}