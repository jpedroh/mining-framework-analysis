package org.cloudfoundry.client.v2;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.util.DelayUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class JobsTest extends AbstractIntegrationTest {
  @Autowired private CloudFoundryClient cloudFoundryClient;

  @Test public void getJob() {
    String organizationName = this.nameFactory.getOrganizationName();
    this.cloudFoundryClient.organizations().create(CreateOrganizationRequest.builder().name(organizationName).build()).map(ResourceUtils::getId).flatMap((organizationId) -> this.cloudFoundryClient.organizations().delete(DeleteOrganizationRequest.builder().organizationId(organizationId).async(true).build()).map(ResourceUtils::getId)).flatMap((jobId) -> this.cloudFoundryClient.jobs().get(GetJobRequest.builder().jobId(jobId).build()).map(ResourceUtils::getId).zipWith(Mono.just(jobId))).filter(predicate((getId, deleteId) -> !"0".equals(getId))).repeatWhenEmpty(5, DelayUtils.instant()).as(StepVerifier::create).consumeNextWith(tupleEquality()).expectComplete().verify(Duration.ofMinutes(5));
  }
}