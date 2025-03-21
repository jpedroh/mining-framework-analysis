package brooklyn.clocker.example;
import java.util.Map;
import brooklyn.entity.container.docker.DockerContainer;
import org.apache.brooklyn.api.catalog.CatalogConfig;
import org.apache.brooklyn.core.entity.StartableApplication;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.entity.BrooklynConfigKeys;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import brooklyn.entity.container.DockerAttributes;
import brooklyn.entity.container.docker.DockerHost;
import brooklyn.entity.container.docker.application.VanillaDockerApplication;

/**
 * Brooklyn managed {@link VanillaDockerApplication}.
 */
@ImplementedBy(value = MicroserviceImageImpl.class) public interface Microservice extends StartableApplication {
  String DOCKER_LOCATION_PREFIX = "docker-";

  @CatalogConfig(label = "Container Name", priority = 90) @SetFromFlag(value = "containerName") ConfigKey<String> CONTAINER_NAME = DockerContainer.DOCKER_CONTAINER_NAME.getConfigKey();

  @CatalogConfig(label = "Open Ports", priority = 70) @SetFromFlag(value = "openPorts") ConfigKey<String> OPEN_PORTS = ConfigKeys.newStringConfigKey("docker.openPorts", "Comma separated list of ports the application uses");

  @CatalogConfig(label = "Direct Ports", priority = 70) @SetFromFlag(value = "directPorts") ConfigKey<String> DIRECT_PORTS = ConfigKeys.newStringConfigKey("docker.directPorts", "Comma separated list of ports to open directly on the host");

  @SetFromFlag(value = "portBindings") ConfigKey<Map<Integer, Integer>> PORT_BINDINGS = DockerAttributes.DOCKER_PORT_BINDINGS;

  @SetFromFlag(value = "env") ConfigKey<Map<String, Object>> DOCKER_CONTAINER_ENVIRONMENT = DockerContainer.DOCKER_CONTAINER_ENVIRONMENT.getConfigKey();

  @SetFromFlag(value = "volumeMappings") ConfigKey<Map<String, String>> DOCKER_HOST_VOLUME_MAPPING = DockerHost.DOCKER_HOST_VOLUME_MAPPING.getConfigKey();

  ConfigKey<String> ONBOX_BASE_DIR = ConfigKeys.newConfigKeyWithDefault(BrooklynConfigKeys.ONBOX_BASE_DIR, "/tmp/brooklyn");

  ConfigKey<Boolean> SKIP_ON_BOX_BASE_DIR_RESOLUTION = ConfigKeys.newConfigKeyWithDefault(BrooklynConfigKeys.SKIP_ON_BOX_BASE_DIR_RESOLUTION, Boolean.TRUE);
}