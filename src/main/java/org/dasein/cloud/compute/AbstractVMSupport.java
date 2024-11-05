package org.dasein.cloud.compute;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.ResourceStatus;
import org.dasein.cloud.Tag;
import org.dasein.cloud.identity.ServiceAction;
import org.dasein.cloud.util.*;
import org.dasein.util.CalendarWrapper;
import org.dasein.util.Jiterator;
import org.dasein.util.JiteratorPopulator;
import org.dasein.util.PopulatorThread;
import org.dasein.util.uom.storage.Gigabyte;
import org.dasein.util.uom.storage.Megabyte;
import org.dasein.util.uom.storage.Storage;
import org.dasein.util.uom.time.Day;
import org.dasein.util.uom.time.TimePeriod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractVMSupport<T extends CloudProvider> implements VirtualMachineSupport {
  private T provider;

  public AbstractVMSupport(T provider) {
    this.provider = provider;
  }

  @Override public VirtualMachine alterVirtualMachine(@Nonnull String vmId, @Nonnull VMScalingOptions options) throws InternalException, CloudException {
    throw new OperationNotSupportedException("VM alternations are not currently supported for " + getProvider().getCloudName());
  }

  @Override public VirtualMachine modifyInstance(@Nonnull String vmId, @Nonnull String[] firewalls) throws InternalException, CloudException {
    throw new OperationNotSupportedException("Instance firewall modifications are not currently supported for " + getProvider().getCloudName());
  }

  public void cancelSpotDataFeedSubscription() throws CloudException, InternalException {
    throw new OperationNotSupportedException("Spot Instances are not supported for " + getProvider().getCloudName());
  }

  @Override public void cancelSpotVirtualMachineRequest(String providerSpotVirtualMachineRequestID) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Spot Instances are not supported for " + getProvider().getCloudName());
  }

  @Override public @Nonnull VirtualMachine clone(@Nonnull String vmId, @Nonnull String intoDcId, @Nonnull String name, @Nonnull String description, boolean powerOn, @Nullable String... firewallIds) throws InternalException, CloudException {
    throw new OperationNotSupportedException("VM cloning is not currently supported for " + getProvider().getCloudName());
  }

  @Override public @Nonnull SpotVirtualMachineRequest createSpotVirtualMachineRequest(SpotVirtualMachineRequestCreateOptions options) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Spot Instances are not supported for " + getProvider().getCloudName());
  }

  public @Nonnull Iterable<SpotVirtualMachineRequest> listSpotVirtualMachineRequests(SpotVirtualMachineRequestFilterOptions options) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Spot Instances are not supported for " + getProvider().getCloudName());
  }

  @Override @Deprecated public @Nullable VMScalingCapabilities describeVerticalScalingCapabilities() throws CloudException, InternalException {
    return getCapabilities().getVerticalScalingCapabilities();
  }

  @Override public void disableAnalytics(@Nonnull String vmId) throws InternalException, CloudException {
  }

  @Override public void enableAnalytics(@Nonnull String vmId) throws InternalException, CloudException {
  }

  @Override public void enableSpotDataFeedSubscription(String bucketName) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Spot Instances are not supported for " + getProvider().getCloudName());
  }

  protected final @Nonnull ProviderContext getContext() throws CloudException {
    ProviderContext ctx = getProvider().getContext();
    if (ctx == null) {
      throw new CloudException("No context was defined for this request");
    }
    return ctx;
  }

  @Override @Deprecated public @Nonnegative int getCostFactor(@Nonnull VmState state) throws CloudException, InternalException {
    return getCapabilities().getCostFactor(state);
  }

  @Override public @Nullable String getPassword(@Nonnull String vmId) throws InternalException, CloudException {
    return null;
  }

  @Override public @Nullable String getUserData(@Nonnull String vmId) throws InternalException, CloudException {
    return null;
  }

  @Override public @Nonnull String getConsoleOutput(@Nonnull String vmId) throws InternalException, CloudException {
    return "";
  }

  @Override @Deprecated public int getMaximumVirtualMachineCount() throws CloudException, InternalException {
    return getCapabilities().getMaximumVirtualMachineCount();
  }

  @Override public @Nullable VirtualMachineProduct getProduct(@Nonnull String productId) throws InternalException, CloudException {
    APITrace.begin(getProvider(), "VM.getProduct");
    try {
      for (Architecture architecture : getCapabilities().listSupportedArchitectures()) {
        for (VirtualMachineProduct prd : listProducts(architecture)) {
          if (productId.equals(prd.getProviderProductId())) {
            return prd;
          }
        }
      }
      return null;
    }  finally {
      APITrace.end();
    }
  }

  protected final @Nonnull T getProvider() {
    return provider;
  }

  @Override @Deprecated public @Nonnull String getProviderTermForServer(@Nonnull Locale locale) {
    try {
      return getCapabilities().getProviderTermForVirtualMachine(locale);
    } catch (Exception ignore) {
      return "virtual machine";
    }
  }

  @Override public @Nullable VirtualMachine getVirtualMachine(@Nonnull String vmId) throws InternalException, CloudException {
    for (VirtualMachine vm : listVirtualMachines(null)) {
      if (vm.getProviderVirtualMachineId().equals(vmId)) {
        return vm;
      }
    }
    return null;
  }

  @Override public @Nonnull VmStatistics getVMStatistics(@Nonnull String vmId, @Nonnegative long from, @Nonnegative long to) throws InternalException, CloudException {
    return new VmStatistics();
  }

  @Override public @Nonnull Iterable<VmStatistics> getVMStatisticsForPeriod(@Nonnull String vmId, @Nonnegative long from, @Nonnegative long to) throws InternalException, CloudException {
    return Collections.emptyList();
  }

  @Override @Deprecated public @Nonnull Requirement identifyImageRequirement(@Nonnull ImageClass cls) throws CloudException, InternalException {
    return getCapabilities().identifyImageRequirement(cls);
  }

  @Override @Deprecated public @Nonnull Requirement identifyPasswordRequirement() throws CloudException, InternalException {
    return getCapabilities().identifyPasswordRequirement(Platform.UNKNOWN);
  }

  @Override @Deprecated public @Nonnull Requirement identifyPasswordRequirement(Platform platform) throws CloudException, InternalException {
    return getCapabilities().identifyPasswordRequirement(platform);
  }

  @Override @Deprecated public @Nonnull Requirement identifyRootVolumeRequirement() throws CloudException, InternalException {
    return getCapabilities().identifyRootVolumeRequirement();
  }

  @Override @Deprecated public @Nonnull Requirement identifyShellKeyRequirement() throws CloudException, InternalException {
    return getCapabilities().identifyShellKeyRequirement(Platform.UNKNOWN);
  }

  @Override @Deprecated public @Nonnull Requirement identifyShellKeyRequirement(Platform platform) throws CloudException, InternalException {
    return getCapabilities().identifyShellKeyRequirement(platform);
  }

  @Override @Deprecated public @Nonnull Requirement identifyStaticIPRequirement() throws CloudException, InternalException {
    return getCapabilities().identifyStaticIPRequirement();
  }

  @Override @Deprecated public @Nonnull Requirement identifyVlanRequirement() throws CloudException, InternalException {
    return getCapabilities().identifyVlanRequirement();
  }

  @Override @Deprecated public boolean isAPITerminationPreventable() throws CloudException, InternalException {
    return getCapabilities().isAPITerminationPreventable();
  }

  @Override @Deprecated public boolean isBasicAnalyticsSupported() throws CloudException, InternalException {
    return getCapabilities().isBasicAnalyticsSupported();
  }

  @Override @Deprecated public boolean isExtendedAnalyticsSupported() throws CloudException, InternalException {
    return getCapabilities().isExtendedAnalyticsSupported();
  }

  @Override @Deprecated public boolean isUserDataSupported() throws CloudException, InternalException {
    return getCapabilities().isUserDataSupported();
  }

  static private final ExecutorService launchPool = Executors.newCachedThreadPool();

  protected Future<String> launchAsync(final @Nonnull VMLaunchOptions withLaunchOptions) {
    return launchPool.submit(new Callable<String>() {
      @Override public String call() throws Exception {
        return launch(withLaunchOptions).getProviderVirtualMachineId();
      }
    });
  }

  @Override public @Nonnull Iterable<String> launchMany(final @Nonnull VMLaunchOptions withLaunchOptions, final @Nonnegative int count) throws CloudException, InternalException {
    if (count < 1) {
      throw new InternalException("Invalid attempt to launch less than 1 virtual machine (requested " + count + ").");
    }
    if (count == 1) {
      return Collections.singleton(launch(withLaunchOptions).getProviderVirtualMachineId());
    }
    final List<Future<String>> results = new ArrayList<Future<String>>();
    MachineImage image = null;
    ComputeServices services = getProvider().getComputeServices();
    if (services != null) {
      MachineImageSupport support = services.getImageSupport();
      if (support != null) {
        image = support.getImage(withLaunchOptions.getMachineImageId());
      }
    }
    NamingConstraints c = NamingConstraints.getHostNameInstance(image == null || image.getPlatform().equals(Platform.UNKNOWN) || image.getPlatform().equals(Platform.WINDOWS));
    String baseHost = c.convertToValidName(withLaunchOptions.getHostName(), Locale.US);
    if (baseHost == null) {
      baseHost = withLaunchOptions.getHostName();
    }
    for (int i = 1; i <= count; i++) {
      String hostName = c.incrementName(baseHost, i);
      String friendlyName = withLaunchOptions.getFriendlyName() + "-" + i;
      VMLaunchOptions options = withLaunchOptions.copy(hostName == null ? withLaunchOptions.getHostName() + "-" + i : hostName, friendlyName);
      results.add(launchAsync(options));
    }
    PopulatorThread<String> populator = new PopulatorThread<String>(new JiteratorPopulator<String>() {
      @Override public void populate(@Nonnull Jiterator<String> iterator) throws Exception {
        List<Future<String>> original = results;
        List<Future<String>> copy = new ArrayList<Future<String>>();
        Exception exception = null;
        boolean loaded = false;
        while (!original.isEmpty()) {
          for (Future<String> result : original) {
            if (result.isDone()) {
              try {
                iterator.push(result.get());
                loaded = true;
              } catch (Exception e) {
                exception = e;
              }
            } else {
              copy.add(result);
            }
          }
          original = copy;
          copy = new ArrayList<Future<String>>();
        }
        if (exception != null && !loaded) {
          throw exception;
        }
      }
    });
    populator.populate();
    return populator.getResult();
  }

  @Override @Deprecated public @Nonnull VirtualMachine launch(@Nonnull String fromMachineImageId, @Nonnull VirtualMachineProduct product, @Nonnull String dataCenterId, @Nonnull String name, @Nonnull String description, @Nullable String withKeypairId, @Nullable String inVlanId, boolean withAnalytics, boolean asSandbox, @Nullable String... firewallIds) throws InternalException, CloudException {
    VMLaunchOptions options = VMLaunchOptions.getInstance(product.getProviderProductId(), fromMachineImageId, name, name, description);
    options.inDataCenter(dataCenterId);
    if (withKeypairId != null) {
      options.withBootstrapKey(withKeypairId);
    }
    if (inVlanId != null) {
      options.inVlan(null, dataCenterId, inVlanId);
    }
    if (withAnalytics) {
      options.withExtendedAnalytics();
    }
    if (firewallIds != null) {
      options.behindFirewalls(firewallIds);
    }
    return launch(options);
  }

  @Override @Deprecated public @Nonnull VirtualMachine launch(@Nonnull String fromMachineImageId, @Nonnull VirtualMachineProduct product, @Nonnull String dataCenterId, @Nonnull String name, @Nonnull String description, @Nullable String withKeypairId, @Nullable String inVlanId, boolean withAnalytics, boolean asSandbox, @Nullable String[] firewallIds, @Nullable Tag... tags) throws InternalException, CloudException {
    VMLaunchOptions options = VMLaunchOptions.getInstance(product.getProviderProductId(), fromMachineImageId, name, name, description);
    options.inDataCenter(dataCenterId);
    if (withKeypairId != null) {
      options.withBootstrapKey(withKeypairId);
    }
    if (inVlanId != null) {
      options.inVlan(null, dataCenterId, inVlanId);
    }
    if (withAnalytics) {
      options.withExtendedAnalytics();
    }
    if (firewallIds != null) {
      options.behindFirewalls(firewallIds);
    }
    if (tags != null) {
      Map<String, Object> metaData = new HashMap<String, Object>();
      for (Tag tag : tags) {
        metaData.put(tag.getKey(), tag.getValue());
      }
      options.withMetaData(metaData);
    }
    return launch(options);
  }

  @Override public @Nonnull Iterable<String> listFirewalls(@Nonnull String vmId) throws InternalException, CloudException {
    return Collections.emptyList();
  }

  protected @Nonnull String getVMProductsResource() throws CloudException {
    Properties p = getContext().getCustomProperties();
    String value = null;
    if (p != null) {
      value = p.getProperty("vmproducts");
    }
    if (value == null) {
      String[] parts = getProvider().getClass().getPackage().getName().split("\\.");
      String impl;
      if (parts.length < 1) {
        impl = getProvider().getClass().getPackage().getName();
      } else {
        impl = parts[parts.length - 1];
      }
      value = System.getProperty("dasein.vmproducts." + impl);
      if (value == null) {
        value = "/org/dasein/cloud/" + impl + "/vmproducts.json";
      }
    }
    return value;
  }

  @Override final public Iterable<VirtualMachineProduct> listProducts(VirtualMachineProductFilterOptions options) throws InternalException, CloudException {
    List<VirtualMachineProduct> products = new ArrayList<VirtualMachineProduct>();
    for (Architecture arch : getCapabilities().listSupportedArchitectures()) {
      mergeProductLists(products, this.listProducts(options, arch));
    }
    return products;
  }

  private void mergeProductLists(List<VirtualMachineProduct> to, Iterable<VirtualMachineProduct> from) {
    List<VirtualMachineProduct> copy = new ArrayList<VirtualMachineProduct>(to);
    for (VirtualMachineProduct productFrom : from) {
      boolean found = false;
      for (VirtualMachineProduct productTo : copy) {
        if (productTo.getProviderProductId().equalsIgnoreCase(productFrom.getProviderProductId())) {
          found = true;
          break;
        }
      }
      if (!found) {
        to.add(productFrom);
      }
    }
  }

  @Override final public @Nonnull Iterable<VirtualMachineProduct> listProducts(@Nonnull Architecture architecture) throws InternalException, CloudException {
    return this.listProducts(null, architecture);
  }

  @Override public @Nonnull Iterable<VirtualMachineProduct> listProducts(@Nullable VirtualMachineProductFilterOptions options, @Nullable Architecture architecture) throws InternalException, CloudException {
    APITrace.begin(getProvider(), "VM.listProducts");
    try {
      String cacheName = "productsALL";
      if (architecture != null) {
        cacheName = "products" + architecture.name();
      }
      Cache<VirtualMachineProduct> cache = Cache.getInstance(getProvider(), cacheName, VirtualMachineProduct.class, CacheLevel.REGION_ACCOUNT, new TimePeriod<Day>(1, TimePeriod.DAY));
      Iterable<VirtualMachineProduct> products = cache.get(getContext());
      if (products != null) {
        return products;
      }
      List<VirtualMachineProduct> list = new ArrayList<VirtualMachineProduct>();
      try {
        String resource = getVMProductsResource();
        InputStream input = AbstractVMSupport.class.getResourceAsStream(resource);
        if (input == null) {
          input = AbstractVMSupport.class.getResourceAsStream("/org/dasein/cloud/std/vmproducts.json");
        }
        if (input == null) {
          return Collections.emptyList();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          json.append(line);
          json.append("\n");
        }
        JSONArray arr = new JSONArray(json.toString());
        JSONObject toCache = null;
        for (int i = 0; i < arr.length(); i++) {
          JSONObject productSet = arr.getJSONObject(i);
          String cloud, provider;
          if (productSet.has("cloud")) {
            cloud = productSet.getString("cloud");
          } else {
            continue;
          }
          if (productSet.has("provider")) {
            provider = productSet.getString("provider");
          } else {
            continue;
          }
          if (!productSet.has("products")) {
            continue;
          }
          if (toCache == null || (provider.equals("default") && cloud.equals("default"))) {
            toCache = productSet;
          }
          if (provider.equalsIgnoreCase(getProvider().getProviderName()) && cloud.equalsIgnoreCase(getProvider().getCloudName())) {
            toCache = productSet;
            break;
          }
        }
        if (toCache == null) {
          return Collections.emptyList();
        }
        JSONArray plist = toCache.getJSONArray("products");
        for (int i = 0; i < plist.length(); i++) {
          JSONObject product = plist.getJSONObject(i);
          boolean supported = false;
          if (architecture != null && product.has("architectures")) {
            JSONArray architectures = product.getJSONArray("architectures");
            for (int j = 0; j < architectures.length(); j++) {
              String a = architectures.getString(j);
              if (architecture.name().equals(a)) {
                supported = true;
                break;
              }
            }
            if (!supported) {
              continue;
            }
          } else {
            supported = true;
          }
          if (product.has("excludesRegions")) {
            JSONArray regions = product.getJSONArray("excludesRegions");
            for (int j = 0; j < regions.length(); j++) {
              String r = regions.getString(j);
              if (r.equals(getContext().getRegionId())) {
                supported = false;
                break;
              }
            }
          }
          if (!supported) {
            continue;
          }
          VirtualMachineProduct prd = toProduct(product);
          if (prd != null) {
            if (options != null) {
              if (options.matches(prd)) {
                list.add(prd);
              }
            } else {
              list.add(prd);
            }
          }
        }
        cache.put(getContext(), list);
      } catch (IOException e) {
        throw new InternalException(e);
      } catch (JSONException e) {
        throw new InternalException(e);
      }
      return list;
    }  finally {
      APITrace.end();
    }
  }

  @Override public Iterable<SpotPriceHistory> listSpotPriceHistories(SpotPriceHistoryFilterOptions options) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Spot Instances are not supported for " + getProvider().getCloudName());
  }

  @Override @Deprecated public Iterable<Architecture> listSupportedArchitectures() throws InternalException, CloudException {
    return getCapabilities().listSupportedArchitectures();
  }

  @Override public @Nonnull Iterable<ResourceStatus> listVirtualMachineStatus() throws InternalException, CloudException {
    List<ResourceStatus> status = new ArrayList<ResourceStatus>();
    for (VirtualMachine vm : listVirtualMachines()) {
      status.add(new ResourceStatus(vm.getProviderVirtualMachineId(), vm.getCurrentState()));
    }
    return status;
  }

  @Override public @Nonnull Iterable<VirtualMachine> listVirtualMachines() throws InternalException, CloudException {
    return Collections.<VirtualMachine>emptyList();
  }

  @Override public @Nonnull Iterable<VirtualMachine> listVirtualMachines(@Nullable VMFilterOptions options) throws InternalException, CloudException {
    if (options == null) {
      return listVirtualMachines();
    }
    List<VirtualMachine> vms = new ArrayList<VirtualMachine>();
    for (VirtualMachine vm : listVirtualMachines()) {
      if (options.matches(vm)) {
        vms.add(vm);
      }
    }
    return vms;
  }

  @Override public void pause(@Nonnull String vmId) throws InternalException, CloudException {
    throw new OperationNotSupportedException("Pause/unpause is not currently implemented for " + getProvider().getCloudName());
  }

  @Override public void reboot(@Nonnull String vmId) throws CloudException, InternalException {
    VirtualMachine vm = getVirtualMachine(vmId);
    if (vm == null) {
      throw new CloudException("No such virtual machine: " + vmId);
    }
    stop(vmId);
    long timeout = System.currentTimeMillis() + (CalendarWrapper.MINUTE * 5L);
    while (timeout > System.currentTimeMillis()) {
      try {
        vm = getVirtualMachine(vmId);
      } catch (Throwable ignore) {
      }
      if (vm == null) {
        return;
      }
      if (vm.getCurrentState().equals(VmState.STOPPED)) {
        start(vmId);
        return;
      }
    }
  }

  @Override public void resume(@Nonnull String vmId) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Resume/suspend is not currently implemented for " + getProvider().getCloudName());
  }

  @Override public void start(@Nonnull String vmId) throws InternalException, CloudException {
    throw new OperationNotSupportedException("Start/stop is not currently implemented for " + getProvider().getCloudName());
  }

  @Override public final void stop(@Nonnull String vmId) throws InternalException, CloudException {
    stop(vmId, false);
    long timeout = System.currentTimeMillis() + (CalendarWrapper.MINUTE * 5L);
    while (timeout > System.currentTimeMillis()) {
      try {
        Thread.sleep(10000L);
      } catch (InterruptedException ignore) {
      }
      try {
        VirtualMachine vm = getVirtualMachine(vmId);
        if (vm == null || VmState.TERMINATED.equals(vm.getCurrentState()) || VmState.STOPPED.equals(vm.getCurrentState())) {
          return;
        }
      } catch (Throwable ignore) {
      }
    }
    stop(vmId, true);
  }

  @Override public void stop(@Nonnull String vmId, boolean force) throws InternalException, CloudException {
    throw new OperationNotSupportedException("Start/stop is not currently implemented for " + getProvider().getCloudName());
  }

  @Override @Deprecated public final boolean supportsAnalytics() throws CloudException, InternalException {
    return (getCapabilities().isBasicAnalyticsSupported() || getCapabilities().isExtendedAnalyticsSupported());
  }

  @Override @Deprecated public boolean supportsPauseUnpause(@Nonnull VirtualMachine vm) {
    try {
      VirtualMachineCapabilities c = getCapabilities();
      VmState s = vm.getCurrentState();
      return (c.canPause(s) || c.canUnpause(s));
    } catch (Exception ignore) {
      return false;
    }
  }

  @Override @Deprecated public boolean supportsStartStop(@Nonnull VirtualMachine vm) {
    try {
      VirtualMachineCapabilities c = getCapabilities();
      VmState s = vm.getCurrentState();
      return (c.canStart(s) || c.canStop(s));
    } catch (Exception ignore) {
      return false;
    }
  }

  @Override @Deprecated public boolean supportsSuspendResume(@Nonnull VirtualMachine vm) {
    try {
      VirtualMachineCapabilities c = getCapabilities();
      VmState s = vm.getCurrentState();
      return (c.canSuspend(s) || c.canResume(s));
    } catch (Exception ignore) {
      return false;
    }
  }

  @Override public void suspend(@Nonnull String vmId) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Resume/suspend is not currently implemented for " + getProvider().getCloudName());
  }

  @Override public void terminate(@Nonnull String vmId) throws CloudException, InternalException {
    terminate(vmId, null);
  }

  @Override public void unpause(@Nonnull String vmId) throws CloudException, InternalException {
    throw new OperationNotSupportedException("Pause/unpause is not currently implemented for " + getProvider().getCloudName());
  }

  @Override public void updateTags(@Nonnull String vmId, @Nonnull Tag... tags) throws CloudException, InternalException {
  }

  @Override public void updateTags(@Nonnull String[] vmIds, @Nonnull Tag... tags) throws CloudException, InternalException {
    for (String id : vmIds) {
      updateTags(id, tags);
    }
  }

  @Override public void removeTags(@Nonnull String vmId, @Nonnull Tag... tags) throws CloudException, InternalException {
  }

  @Override public void removeTags(@Nonnull String[] vmIds, @Nonnull Tag... tags) throws CloudException, InternalException {
    for (String id : vmIds) {
      removeTags(id, tags);
    }
  }

  @Override public void setTags(@Nonnull String vmId, @Nonnull Tag... tags) throws CloudException, InternalException {
    setTags(new String[] { vmId }, tags);
  }

  @Override public void setTags(@Nonnull String[] vmIds, @Nonnull Tag... tags) throws CloudException, InternalException {
    for (String id : vmIds) {
      Tag[] collectionForDelete = TagUtils.getTagsForDelete(getVirtualMachine(id).getTags(), tags);
      if (collectionForDelete.length != 0) {
        removeTags(id, collectionForDelete);
      }
      updateTags(id, tags);
    }
  }

  @Override public @Nonnull String[] mapServiceAction(@Nonnull ServiceAction action) {
    return new String[0];
  }

  private @Nullable VirtualMachineProduct toProduct(@Nonnull JSONObject json) throws InternalException {
    VirtualMachineProduct prd = new VirtualMachineProduct();
    try {
      if (json.has("id")) {
        prd.setProviderProductId(json.getString("id"));
      } else {
        return null;
      }
      if (json.has("name")) {
        prd.setName(json.getString("name"));
      } else {
        prd.setName(prd.getProviderProductId());
      }
      if (json.has("description")) {
        prd.setDescription(json.getString("description"));
      } else {
        prd.setDescription(prd.getName());
      }
      if (json.has("cpuCount")) {
        prd.setCpuCount(json.getInt("cpuCount"));
      } else {
        prd.setCpuCount(1);
      }
      if (json.has("rootVolumeSizeInGb")) {
        prd.setRootVolumeSize(new Storage<Gigabyte>(json.getInt("rootVolumeSizeInGb"), Storage.GIGABYTE));
      } else {
        prd.setRootVolumeSize(new Storage<Gigabyte>(1, Storage.GIGABYTE));
      }
      if (json.has("ramSizeInMb")) {
        prd.setRamSize(new Storage<Megabyte>(json.getInt("ramSizeInMb"), Storage.MEGABYTE));
      } else {
        prd.setRamSize(new Storage<Megabyte>(512, Storage.MEGABYTE));
      }
      if (json.has("standardHourlyRates")) {
        JSONArray rates = json.getJSONArray("standardHourlyRates");
        for (int i = 0; i < rates.length(); i++) {
          JSONObject rate = rates.getJSONObject(i);
          if (rate.has("rate")) {
            prd.setStandardHourlyRate((float) rate.getDouble("rate"));
          }
        }
      }
    } catch (JSONException e) {
      throw new InternalException(e);
    }
    return prd;
  }

  @Override public @Nullable Iterable<VirtualMachineStatus> getVMStatus(@Nullable String... vmIds) throws InternalException, CloudException {
    throw new OperationNotSupportedException("Virtual Machine Status is not currently implemented for " + getProvider().getCloudName());
  }

  @Override public @Nullable Iterable<VirtualMachineStatus> getVMStatus(@Nullable VmStatusFilterOptions filterOptions) throws InternalException, CloudException {
    throw new OperationNotSupportedException("Virtual Machine Status is not currently implemented for " + getProvider().getCloudName());
  }
}