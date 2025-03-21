package org.cloudfoundry.reactor.client;
import com.github.zafarkhaja.semver.Version;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

final class CloudFoundryClientCompatibilityChecker {
  private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.compatibility");

  private final Info info;

  CloudFoundryClientCompatibilityChecker(Info info) {
    this.info = info;
  }

  void check() {
    this.info.get(GetInfoRequest.builder().build()).map((response) -> Version.valueOf(response.getApiVersion())).zipWith(Mono.just(Version.valueOf(CloudFoundryClient.SUPPORTED_API_VERSION))).doOnNext(consumer((server, supported) -> logCompatibility(server, supported, this.logger))).subscribe();
  }

  private static void logCompatibility(Version server, Version supported, Logger logger) {
    String message = "Client supports API version {} and is connected to server with API version {}. Things may not work as expected.";
    if (server.greaterThan(supported)) {
      logger.info(message, supported, server);
    } else {
      if (server.lessThan(supported)) {
        logger.warn(message, supported, server);
      }
    }
  }
}