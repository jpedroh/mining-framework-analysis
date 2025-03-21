package org.cloudfoundry.operations.stacks;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.StackResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.NoSuchElementException;

public final class DefaultStacks implements Stacks {
  private final Mono<CloudFoundryClient> cloudFoundryClient;

  public DefaultStacks(Mono<CloudFoundryClient> cloudFoundryClient) {
    this.cloudFoundryClient = cloudFoundryClient;
  }

  @Override public Mono<Stack> get(GetStackRequest request) {
    return this.cloudFoundryClient.flatMap((cloudFoundryClient) -> getStack(cloudFoundryClient, request.getName())).map(this::toStack).transform(OperationsLogging.log("Get Stack")).checkpoint();
  }

  @Override public Flux<Stack> list() {
    return this.cloudFoundryClient.flatMapMany(DefaultStacks::requestStacks).map(this::toStack).transform(OperationsLogging.log("List Stacks")).checkpoint();
  }

  private static Mono<StackResource> getStack(CloudFoundryClient cloudFoundryClient, String stack) {
    return requestStack(cloudFoundryClient, stack).single().onErrorResume(NoSuchElementException.class, (t) -> ExceptionUtils.illegalArgument("Stack %s does not exist", stack));
  }

  private static Flux<StackResource> requestStack(CloudFoundryClient cloudFoundryClient, String stack) {
    return PaginationUtils.requestClientV2Resources((page) -> cloudFoundryClient.stacks().list(ListStacksRequest.builder().name(stack).page(page).build()));
  }

  private static Flux<StackResource> requestStacks(CloudFoundryClient cloudFoundryClient) {
    return PaginationUtils.requestClientV2Resources((page) -> cloudFoundryClient.stacks().list(ListStacksRequest.builder().page(page).build()));
  }

  private Stack toStack(StackResource stackResource) {
    return Stack.builder().description(ResourceUtils.getEntity(stackResource).getDescription()).id(ResourceUtils.getId(stackResource)).name(ResourceUtils.getEntity(stackResource).getName()).build();
  }
}