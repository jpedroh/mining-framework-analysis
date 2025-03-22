package org.dasein.cloud;
import org.dasein.cloud.admin.AdminServices;
import org.dasein.cloud.ci.CIServices;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.platform.PlatformServices;
import org.dasein.cloud.quotas.QuotaServices;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple base implementation of a cloud provider bootstrap object that defaults all services to <code>null</code>.
 * @author George Reese
 * @version 2013.07 added javadoc, fixed annotations on data center services, made it return an NPE
 * @since unknown
 */
public abstract class AbstractCloud extends CloudProvider {
  /**
     * Constructs a cloud provider instance.
     */
  public AbstractCloud() {
  }

  @Override public @Nullable AdminServices getAdminServices() {
    return null;
  }

  @Override public @Nullable ComputeServices getComputeServices() {
    CloudProvider compute = getComputeCloud();
    return (compute == null ? null : compute.getComputeServices());
  }

  @Override public @Nonnull ContextRequirements getContextRequirements() {
    return new ContextRequirements(new ContextRequirements.Field("apiKeys", ContextRequirements.FieldType.KEYPAIR), new ContextRequirements.Field("x509", ContextRequirements.FieldType.KEYPAIR, false));
  }

  @Override public @Nullable CIServices getCIServices() {
    CloudProvider compute = getComputeCloud();
    return (compute == null ? null : compute.getCIServices());
  }

  @Override public @Nullable IdentityServices getIdentityServices() {
    CloudProvider compute = getComputeCloud();
    return (compute == null ? null : compute.getIdentityServices());
  }

  @Override public @Nullable NetworkServices getNetworkServices() {
    CloudProvider compute = getComputeCloud();
    return (compute == null ? null : compute.getNetworkServices());
  }

  @Override public @Nullable PlatformServices getPlatformServices() {
    CloudProvider compute = getComputeCloud();
    return (compute == null ? null : compute.getPlatformServices());
  }

  @Override public @Nullable QuotaServices getQuotaServices() {
    CloudProvider compute = getComputeCloud();
    return (compute == null ? null : compute.getQuotaServices());
  }
}