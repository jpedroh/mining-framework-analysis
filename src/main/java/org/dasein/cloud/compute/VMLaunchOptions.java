package org.dasein.cloud.compute;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.network.NICCreateOptions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings(value = { "UnusedDeclaration" }) public class VMLaunchOptions {
  static public class NICConfig {
    public String nicId;

    public NICCreateOptions nicToCreate;
  }

  static public @Nonnull VMLaunchOptions getInstance(@Nonnull String withStandardProductId, @Nonnull String usingMachineImageId, @Nonnull String havingFriendlyName, @Nonnull String withDescription) {
    return new VMLaunchOptions(withStandardProductId, usingMachineImageId, havingFriendlyName, havingFriendlyName, withDescription);
  }

  static public @Nonnull VMLaunchOptions getInstance(@Nonnull String withStandardProductId, @Nonnull String usingMachineImageId, @Nonnull String withHostName, @Nonnull String havingFriendlyName, @Nonnull String withDescription) {
    return new VMLaunchOptions(withStandardProductId, usingMachineImageId, withHostName, havingFriendlyName, withDescription);
  }

  private String bootstrapKey;

  private String bootstrapPassword;

  private String bootstrapUser;

  private String dataCenterId;

  private String description;

  private boolean extendedAnalytics;

  private String[] firewallIds;

  private String friendlyName;

  private String hostName;

  private String kernelId;

  private String machineImageId;

  private Map<String, Object> metaData;

  private String[] labels;

  private String networkProductId;

  private NICConfig[] networkInterfaces;

  private boolean preventApiTermination;

  private String privateIp;

  private boolean provisionPublicIp;

  private String ramdiskId;

  private String rootVolumeProductId;

  private String standardProductId;

  private String[] staticIpIds;

  private String userData;

  private String vlanId;

  private String subnetId;

  private VolumeAttachment[] volumes;

  private boolean ioOptimized;

  private boolean ipForwardingAllowed;

  private String roleId;

  private boolean associatePublicIpAddress;

  private String placementGroupId;

  private VMLaunchOptions() {
  }

  private VMLaunchOptions(@Nonnull String standardProductId, @Nonnull String machineImageId, @Nonnull String hostName, @Nonnull String friendlyName, @Nonnull String description) {
    this.standardProductId = standardProductId;
    this.machineImageId = machineImageId;
    this.description = description;
    this.hostName = hostName;
    this.friendlyName = friendlyName;
    extendedAnalytics = false;
  }

  public @Nonnull String build(@Nonnull CloudProvider provider) throws CloudException, InternalException {
    ComputeServices services = provider.getComputeServices();
    if (services == null) {
      throw new OperationNotSupportedException(provider.getCloudName() + " does not support compute services.");
    }
    VirtualMachineSupport support = services.getVirtualMachineSupport();
    if (support == null) {
      throw new OperationNotSupportedException(provider.getCloudName() + " does not have virtual machine support");
    }
    return support.launch(this).getProviderVirtualMachineId();
  }

  public @Nonnull Iterable<String> buildMany(@Nonnull CloudProvider provider, int count) throws CloudException, InternalException {
    ComputeServices services = provider.getComputeServices();
    if (services == null) {
      throw new OperationNotSupportedException(provider.getCloudName() + " does not support compute services.");
    }
    VirtualMachineSupport support = services.getVirtualMachineSupport();
    if (support == null) {
      throw new OperationNotSupportedException(provider.getCloudName() + " does not have virtual machine support");
    }
    return support.launchMany(this, count);
  }

  public @Nonnull VMLaunchOptions copy(@Nonnull String havingHostName, @Nonnull String havingFriendlyName) {
    VMLaunchOptions options = new VMLaunchOptions(standardProductId, machineImageId, havingHostName, havingFriendlyName, description);
    options.bootstrapKey = bootstrapKey;
    options.bootstrapPassword = bootstrapPassword;
    options.bootstrapUser = bootstrapUser;
    options.dataCenterId = dataCenterId;
    options.extendedAnalytics = extendedAnalytics;
    options.firewallIds = (firewallIds == null ? new String[0] : Arrays.copyOf(options.firewallIds, options.firewallIds.length));
    options.ioOptimized = ioOptimized;
    options.ipForwardingAllowed = ipForwardingAllowed;
    options.kernelId = kernelId;
    options.virtualMachineGroup = virtualMachineGroup;
    if (metaData != null) {
      options.metaData = new HashMap<String, Object>();
      options.metaData.putAll(metaData);
    }
    if (networkInterfaces != null && networkInterfaces.length > 0) {
      ArrayList<NICConfig> cfgs = new ArrayList<NICConfig>();
      for (NICConfig cfg : networkInterfaces) {
        if (cfg.nicToCreate != null) {
          NICConfig c = new NICConfig();
          c.nicToCreate = cfg.nicToCreate.copy(c.nicToCreate.getName() + " - " + hostName);
          cfgs.add(c);
        }
      }
      options.networkInterfaces = cfgs.toArray(new NICConfig[cfgs.size()]);
    } else {
      options.networkInterfaces = new NICConfig[0];
    }
    options.networkProductId = networkProductId;
    options.preventApiTermination = preventApiTermination;
    options.privateIp = null;
    options.provisionPublicIp = provisionPublicIp;
    options.ramdiskId = ramdiskId;
    options.rootVolumeProductId = rootVolumeProductId;
    options.staticIpIds = new String[0];
    options.userData = userData;
    options.vlanId = vlanId;
    options.subnetId = subnetId;
    options.volumes = new VolumeAttachment[0];
    options.ioOptimized = ioOptimized;
    options.ipForwardingAllowed = ipForwardingAllowed;
    options.roleId = roleId;
    if (volumes != null && volumes.length > 0) {
      ArrayList<VolumeAttachment> copy = new ArrayList<VolumeAttachment>();
      for (VolumeAttachment a : volumes) {
        if (a.volumeToCreate != null) {
          VolumeAttachment nv = new VolumeAttachment();
          nv.volumeToCreate = a.volumeToCreate.copy(a.volumeToCreate.getName() + "-" + hostName);
          copy.add(nv);
        }
      }
      options.volumes = copy.toArray(new VolumeAttachment[copy.size()]);
    }
    options.placementGroupId = placementGroupId;
    return options;
  }

  public @Nullable String getBootstrapKey() {
    return bootstrapKey;
  }

  public @Nullable String getBootstrapPassword() {
    return bootstrapPassword;
  }

  public @Nullable String getBootstrapUser() {
    return bootstrapUser;
  }

  public @Nullable String getDataCenterId() {
    return dataCenterId;
  }

  public @Nonnull String getDescription() {
    return description;
  }

  public boolean isExtendedAnalytics() {
    return extendedAnalytics;
  }

  public @Nonnull String[] getFirewallIds() {
    return (firewallIds == null ? new String[0] : firewallIds);
  }

  public @Nonnull String[] getLabels() {
    return (labels == null ? new String[0] : labels);
  }

  public @Nonnull String getFriendlyName() {
    return friendlyName;
  }

  public @Nonnull String getHostName() {
    return hostName;
  }

  public String getKernelId() {
    return kernelId;
  }

  public @Nonnull String getMachineImageId() {
    return machineImageId;
  }

  public @Nonnull Map<String, Object> getMetaData() {
    return (metaData == null ? new HashMap<String, Object>() : metaData);
  }

  public @Nullable String getNetworkProductId() {
    return networkProductId;
  }

  public boolean isPreventApiTermination() {
    return preventApiTermination;
  }

  public NICConfig[] getNetworkInterfaces() {
    return networkInterfaces;
  }

  public String getRamdiskId() {
    return ramdiskId;
  }

  public @Nullable String getRootVolumeProductId() {
    return rootVolumeProductId;
  }

  public @Nonnull String getStandardProductId() {
    return standardProductId;
  }

  public @Nonnull String[] getStaticIpIds() {
    if (staticIpIds == null) {
      return new String[0];
    }
    return Arrays.copyOf(staticIpIds, staticIpIds.length);
  }

  public @Nullable String getPrivateIp() {
    return privateIp;
  }

  public @Nullable String getUserData() {
    return userData;
  }

  public @Nullable String getSubnetId() {
    return subnetId;
  }

  public @Nullable String getVlanId() {
    return vlanId;
  }

  public @Nonnull VolumeAttachment[] getVolumes() {
    return (volumes == null ? new VolumeAttachment[0] : volumes);
  }

  public boolean isIoOptimized() {
    return ioOptimized;
  }

  public @Nonnull VMLaunchOptions behindFirewalls(@Nonnull String... firewallIds) {
    if (this.firewallIds == null || this.firewallIds.length < 1) {
      this.firewallIds = firewallIds;
    } else {
      if (firewallIds.length > 0) {
        String[] tmp = new String[this.firewallIds.length + firewallIds.length];
        int i = 0;
        for (String id : this.firewallIds) {
          tmp[i++] = id;
        }
        for (String id : firewallIds) {
          tmp[i++] = id;
        }
        this.firewallIds = tmp;
      }
    }
    return this;
  }

  public @Nonnull VMLaunchOptions withLabels(String... labels) {
    if (labels != null) {
      this.labels = Arrays.copyOf(labels, labels.length);
    }
    return this;
  }

  public @Nonnull VMLaunchOptions inDataCenter(@Nonnull String dataCenterId) {
    this.dataCenterId = dataCenterId;
    return this;
  }

  public @Nonnull VMLaunchOptions preventAPITermination() {
    this.preventApiTermination = true;
    return this;
  }

  public @Nonnull VMLaunchOptions inVlan(@Nullable String networkProductId, @Nonnull String dataCenterId, @Nonnull String vlanId) {
    this.networkProductId = networkProductId;
    this.dataCenterId = dataCenterId;
    this.vlanId = vlanId;
    return this;
  }

  public @Nonnull VMLaunchOptions inSubnet(@Nullable String networkProductId, @Nonnull String dataCenterId, @Nullable String vlanId, @Nonnull String subnetId) {
    this.networkProductId = networkProductId;
    this.dataCenterId = dataCenterId;
    this.vlanId = vlanId;
    this.subnetId = subnetId;
    return this;
  }

  public @Nonnull VMLaunchOptions withAttachments(@Nonnull VolumeAttachment... attachments) {
    if (volumes == null || volumes.length < 1) {
      volumes = attachments;
    } else {
      if (attachments.length > 0) {
        VolumeAttachment[] tmp = new VolumeAttachment[volumes.length + attachments.length];
        int i = 0;
        for (VolumeAttachment a : volumes) {
          tmp[i++] = a;
        }
        for (VolumeAttachment a : attachments) {
          tmp[i++] = a;
        }
        volumes = tmp;
      }
    }
    return this;
  }

  public @Nonnull VMLaunchOptions withAttachments(@Nonnull VolumeCreateOptions... toBeCreated) {
    int i = 0;
    if (volumes != null && volumes.length > 0) {
      VolumeAttachment[] tmp = new VolumeAttachment[volumes.length + toBeCreated.length];
      for (VolumeAttachment a : volumes) {
        tmp[i++] = a;
      }
      volumes = tmp;
    } else {
      volumes = new VolumeAttachment[toBeCreated.length];
    }
    for (VolumeCreateOptions options : toBeCreated) {
      VolumeAttachment a = new VolumeAttachment();
      a.deviceId = options.getDeviceId();
      a.volumeToCreate = options;
      volumes[i++] = a;
    }
    return this;
  }

  public @Nonnull VMLaunchOptions withAttachment(@Nonnull String existingVolumeId, @Nonnull String withDeviceId) {
    VolumeAttachment a = new VolumeAttachment();
    a.deviceId = withDeviceId;
    a.existingVolumeId = existingVolumeId;
    if (volumes == null || volumes.length < 1) {
      volumes = new VolumeAttachment[] { a };
    } else {
      VolumeAttachment[] tmp = new VolumeAttachment[volumes.length + 1];
      int i = 0;
      for (VolumeAttachment current : volumes) {
        tmp[i++] = current;
      }
      tmp[i] = a;
      volumes = tmp;
    }
    return this;
  }

  public @Nonnull VMLaunchOptions withBoostrapKey(@Nonnull String key) {
    this.bootstrapKey = key;
    return this;
  }

  public @Nonnull VMLaunchOptions withBootstrapUser(@Nonnull String user, @Nonnull String password) {
    this.bootstrapUser = user;
    this.bootstrapPassword = password;
    return this;
  }

  public @Nonnull VMLaunchOptions withExtendedAnalytics() {
    extendedAnalytics = true;
    return this;
  }

  public @Nonnull VMLaunchOptions withExtendedImages(@Nullable String providerKernelId, @Nullable String providerRamdiskId) {
    kernelId = providerKernelId;
    ramdiskId = providerRamdiskId;
    return this;
  }

  public @Nonnull VMLaunchOptions withMetaData(@Nonnull String key, @Nonnull Object value) {
    if (metaData == null) {
      metaData = new HashMap<String, Object>();
    }
    metaData.put(key, value);
    return this;
  }

  public @Nonnull VMLaunchOptions withMetaData(@Nonnull Map<String, Object> metaData) {
    if (this.metaData == null) {
      this.metaData = new HashMap<String, Object>();
    }
    this.metaData.putAll(metaData);
    return this;
  }

  public @Nonnull VMLaunchOptions withNetworkInterfaces(String... nicIds) {
    if (networkInterfaces == null || networkInterfaces.length < 1) {
      int i = 0;
      networkInterfaces = new NICConfig[nicIds.length];
      for (String id : nicIds) {
        NICConfig cfg = new NICConfig();
        cfg.nicId = id;
        networkInterfaces[i++] = cfg;
      }
    } else {
      if (nicIds.length > 0) {
        NICConfig[] tmp = new NICConfig[networkInterfaces.length + nicIds.length];
        int i = 0;
        for (NICConfig cfg : networkInterfaces) {
          tmp[i++] = cfg;
        }
        for (String id : nicIds) {
          NICConfig cfg = new NICConfig();
          cfg.nicId = id;
          tmp[i++] = cfg;
        }
        networkInterfaces = tmp;
      }
    }
    return this;
  }

  public @Nonnull VMLaunchOptions withNetworkInterfaces(NICCreateOptions... options) {
    if (networkInterfaces == null || networkInterfaces.length < 1) {
      int i = 0;
      networkInterfaces = new NICConfig[options.length];
      for (NICCreateOptions opt : options) {
        NICConfig cfg = new NICConfig();
        cfg.nicToCreate = opt;
        networkInterfaces[i++] = cfg;
      }
    } else {
      if (options.length > 0) {
        NICConfig[] tmp = new NICConfig[networkInterfaces.length + options.length];
        int i = 0;
        for (NICConfig cfg : networkInterfaces) {
          tmp[i++] = cfg;
        }
        for (NICCreateOptions opt : options) {
          NICConfig cfg = new NICConfig();
          cfg.nicToCreate = opt;
          tmp[i++] = cfg;
        }
        networkInterfaces = tmp;
      }
    }
    return this;
  }

  public @Nonnull VMLaunchOptions withRootVolumeProduct(@Nonnull String volumeProductId) {
    this.rootVolumeProductId = volumeProductId;
    return this;
  }

  public @Nonnull VMLaunchOptions withUserData(@Nonnull String userData) {
    this.userData = userData;
    return this;
  }

  public @Nonnull VMLaunchOptions withStaticIps(@Nonnull String... ipIds) {
    this.staticIpIds = ipIds;
    return this;
  }

  public @Nonnull VMLaunchOptions withIoOptimized(boolean ioOptimized) {
    this.ioOptimized = ioOptimized;
    return this;
  }

  public @Nonnull VMLaunchOptions withPrivateIp(@Nonnull String ipAddr) {
    this.privateIp = ipAddr;
    return this;
  }

  public boolean isProvisionPublicIp() {
    return provisionPublicIp;
  }

  public @Nonnull VMLaunchOptions withProvisionPublicIp(@Nonnull boolean provisionPublicIp) {
    this.provisionPublicIp = provisionPublicIp;
    return this;
  }

  public boolean isIpForwardingAllowed() {
    return ipForwardingAllowed;
  }

  public @Nonnull VMLaunchOptions withIpForwardingAllowed(boolean ipForwardingAllowed) {
    this.ipForwardingAllowed = ipForwardingAllowed;
    return this;
  }

  public @Nonnull VMLaunchOptions withRoleId(@Nonnull String roleId) {
    this.roleId = roleId;
    return this;
  }

  public @Nullable String getRoleId() {
    return roleId;
  }

  public VMLaunchOptions withAssociatePublicIpAddress(final boolean publicIpAddress) {
    this.associatePublicIpAddress = publicIpAddress;
    return this;
  }

  public boolean isAssociatePublicIpAddress() {
    return associatePublicIpAddress;
  }

  public String getPlacementGroupId() {
    return placementGroupId;
  }

  public VMLaunchOptions withPlacementGroupId(@Nonnull String placementGroupId) {
    this.placementGroupId = placementGroupId;
    return this;
  }

  private String virtualMachineGroup;

  public @Nullable String getVirtualMachineGroup() {
    return virtualMachineGroup;
  }

  public @Nonnull VMLaunchOptions inVirtualMachineGroup(@Nonnull String group) {
    this.virtualMachineGroup = group;
    return this;
  }
}