package org.dasein.cloud.compute;
import org.dasein.cloud.AbstractProviderService;
import org.dasein.cloud.CloudProvider;
import javax.annotation.Nullable;

/**
 * Skeleton implementation of compute services with a default behavior of supporting no services. Override those services
 * you wish to provide in support of the cloud you are implementing.
 * @author George Reese
 * @version 2013.07 added topology support
 * @since unknown
 */
public abstract class AbstractComputeServices<T extends CloudProvider> extends AbstractProviderService<T> implements ComputeServices {
  protected AbstractComputeServices(T provider) {
    super(provider);
  }

  @Override public @Nullable AffinityGroupSupport getAffinityGroupSupport() {
    return null;
  }

  @Override public @Nullable AutoScalingSupport getAutoScalingSupport() {
    return null;
  }

  @Override public @Nullable MachineImageSupport getImageSupport() {
    return null;
  }

  @Override public @Nullable SnapshotSupport getSnapshotSupport() {
    return null;
  }

  @Override public @Nullable VirtualMachineSupport getVirtualMachineSupport() {
    return null;
  }

  @Override public @Nullable VolumeSupport getVolumeSupport() {
    return null;
  }

  @Override public @Nullable HttpLoadBalancerSupport getCIHttpLoadBalancerSupport() {
    return null;
  }

  @Override public boolean hasAffinityGroupSupport() {
    return (getAffinityGroupSupport() != null);
  }

  @Override public boolean hasAutoScalingSupport() {
    return (getAutoScalingSupport() != null);
  }

  @Override public boolean hasImageSupport() {
    return (getImageSupport() != null);
  }

  @Override public boolean hasSnapshotSupport() {
    return (getSnapshotSupport() != null);
  }

  @Override public boolean hasVirtualMachineSupport() {
    return (getVirtualMachineSupport() != null);
  }

  @Override public boolean hasVolumeSupport() {
    return (getVolumeSupport() != null);
  }

  @Override public boolean hasCIHttpLoadBalancerSupport() {
    return (getCIHttpLoadBalancerSupport() != null);
  }
}