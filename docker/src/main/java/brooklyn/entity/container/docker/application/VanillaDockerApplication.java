package brooklyn.entity.container.docker.application;
import java.util.List;
import java.util.Map;
import org.apache.brooklyn.api.catalog.Catalog;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.entity.ImplementedBy;
import org.apache.brooklyn.config.ConfigKey;
import org.apache.brooklyn.core.config.ConfigKeys;
import org.apache.brooklyn.core.entity.BrooklynConfigKeys;
import org.apache.brooklyn.core.sensor.AttributeSensorAndConfigKey;
import org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess;
import org.apache.brooklyn.util.core.flags.SetFromFlag;
import org.apache.brooklyn.util.time.Duration;
import brooklyn.entity.container.DockerAttributes;
import brooklyn.entity.container.docker.DockerContainer;
import brooklyn.entity.container.docker.DockerHost;

@Catalog(name = "Docker Container", description = "A micro-service running in a Docker container.", iconUrl = "classpath:///container.png") @ImplementedBy(value = VanillaDockerApplicationImpl.class) public interface VanillaDockerApplication extends VanillaSoftwareProcess {
  @SetFromFlag(value = "startTimeout") ConfigKey<Duration> START_TIMEOUT = ConfigKeys.newConfigKeyWithDefault(BrooklynConfigKeys.START_TIMEOUT, Duration.FIVE_MINUTES);

  @SetFromFlag(value = "dockerfileUrl") ConfigKey<String> DOCKERFILE_URL = DockerAttributes.DOCKERFILE_URL;

  @SetFromFlag(value = "containerName") ConfigKey<String> CONTAINER_NAME = DockerContainer.DOCKER_CONTAINER_NAME.getConfigKey();

  @SetFromFlag(value = "imageName") ConfigKey<String> IMAGE_NAME = DockerAttributes.DOCKER_IMAGE_NAME.getConfigKey();

  @SetFromFlag(value = "imageTag") ConfigKey<String> IMAGE_TAG = DockerAttributes.DOCKER_IMAGE_TAG.getConfigKey();

  @SetFromFlag(value = "useSsh") ConfigKey<Boolean> DOCKER_USE_SSH = ConfigKeys.newConfigKeyWithDefault(DockerAttributes.DOCKER_USE_SSH, Boolean.FALSE);

  @SetFromFlag(value = "openPorts") ConfigKey<List<Integer>> DOCKER_OPEN_PORTS = DockerAttributes.DOCKER_OPEN_PORTS;

  @SetFromFlag(value = "directPorts") ConfigKey<List<Integer>> DOCKER_DIRECT_PORTS = DockerAttributes.DOCKER_DIRECT_PORTS;

  @SetFromFlag(value = "portBindings") ConfigKey<Map<Integer, Integer>> DOCKER_PORT_BINDINGS = DockerAttributes.DOCKER_PORT_BINDINGS;

  @SetFromFlag(value = "env") ConfigKey<Map<String, String>> DOCKER_CONTAINER_ENVIRONMENT = DockerContainer.DOCKER_CONTAINER_ENVIRONMENT.getConfigKey();

  @SetFromFlag(value = "volumes") ConfigKey<List<String>> DOCKER_CONTAINER_VOLUME_EXPORT = DockerAttributes.DOCKER_CONTAINER_VOLUME_EXPORT;

  @SetFromFlag(value = "volumeMappings") AttributeSensorAndConfigKey<Map<String, String>, Map<String, String>> DOCKER_HOST_VOLUME_MAPPING = DockerAttributes.DOCKER_HOST_VOLUME_MAPPING;

  @SetFromFlag(value = "links") ConfigKey<List<Entity>> DOCKER_LINKS = DockerAttributes.DOCKER_LINKS;

  ConfigKey<Boolean> SKIP_ENTITY_START = ConfigKeys.newConfigKeyWithDefault(BrooklynConfigKeys.SKIP_ENTITY_START, Boolean.TRUE);

  ConfigKey<Boolean> SKIP_ON_BOX_BASE_DIR_RESOLUTION = ConfigKeys.newConfigKeyWithDefault(BrooklynConfigKeys.SKIP_ON_BOX_BASE_DIR_RESOLUTION, Boolean.TRUE);

  DockerContainer getDockerContainer();

  DockerHost getDockerHost();

  String getDockerfile();

  List<Integer> getContainerPorts();
}